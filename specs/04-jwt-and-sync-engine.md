# 04. JWT & Background Sync Engine Specification

## JWT Lifecycle & Refresh Strategy

### Token Properties

- JWT expiry: **5 minutes** (`expiresIn: '5m'`) in Strapi (`config/plugins.js`).
- Session data (Jetpack DataStore): `jwt_token`, `refresh_token`, and `token_timestamp`.

### Expiration & Refresh Flow (OkHttp Interceptor)

When an API request returns HTTP 401 Unauthorized:

1. Attempt silent refresh
   - Interceptor handles the 401.
   - Runs a synchronized refresh using stored refresh tokens or credentials.
   - On success: update `jwt_token` in DataStore, attach the new token, and retry the request.

2. Failed refresh / unrecoverable auth
   - If silent refresh fails (e.g. refresh token expired, user blocked), clear session tokens from DataStore only (do not clear Room).
   - Persist `skip_visit_sync_on_login=true` in DataStore (survives process death; not cleared by `clearSession`).
   - Emit a one-shot signal via `AuthEventBus` (`Channel<Unit>` → `sessionExpired` Flow / `emitSessionExpired()`).
   - Navigate to `LoginScreen` and show: _"Sesi Anda telah berakhir. Silakan login kembali."_
   - On the next login, skip remote visit-report pull when that flag is set, then clear the flag.

## Post-Login Seed (Room SSOT)

After successful `POST /api/auth/local`:

1. Always seed stores: `GET /api/stores` → replace local `stores` table.
2. Visit reports pull (`GET /api/visit-reports?populate=*`) when `skip_visit_sync_on_login` is not set:
   - Replace local `visit_reports` with remote rows as `SYNCED` (safe after explicit logout wipe; skipped after session-expired re-login).
   - Store absolute photo URL in `localImagePath` when local file is absent (detail UI loads via Coil).
   - Session-expired recovery login: skip this pull entirely (flag from DataStore).

## Offline Strategy & JWT Bypass

1. Online
   - All network requests pass through `AuthInterceptor` with `Authorization: Bearer <jwt_token>`.

2. Offline
   - Skip network calls.
   - If the network is unavailable when submitting a visit report, bypass remote JWT verification.
   - Save the visit report to Room with `syncStatus = SyncStatus.PENDING_SYNC`, then enqueue `SyncReportWorker` (do not block navigation on network).

## Background Sync (`SyncReportWorker`)

- Framework: WorkManager `CoroutineWorker`.
- Constraint: `NetworkType.CONNECTED`.

### Worker Execution Workflow

1. Query pending data: `VisitReportDao` for `syncStatus` of `PENDING_SYNC` or `FAILED` (failed rows stay eligible for automatic / manual retry).
2. Verify / refresh token:
   - Read active JWT from DataStore.
   - If expired, run silent token refresh before sending payloads.
   - If refresh fails due to invalid credentials, pause WorkManager execution and emit `AuthEventBus.sessionExpired` (same path as Authenticator).
3. Two-step upload (Strapi 5):
   - `POST /api/upload` (multipart `files`) for the photo; read returned file `id`.
   - `POST /api/visit-reports` (JSON) with `data`: `store`, `latitude`, `longitude`, `order_notes`, `photo` (uploaded file id).
4. State transition:
   - HTTP 200/201: update Room `syncStatus = SYNCED`.
   - HTTP 5xx / network error: update Room `syncStatus = FAILED` (eligible for manual or scheduled retry).

## Explicit Logout Cleanup

When the user confirms logout on Home:

1. Cancel unique WorkManager work for visit-report upload.
2. `clearAllTables()` on Room (stores + visit reports).
3. Clear JWT / refresh tokens from DataStore.
4. Navigate to `LoginScreen`.

Next successful login re-seeds stores and pulls visit reports from Strapi (see Post-Login Seed).
