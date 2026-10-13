# 04. JWT & Background Sync Engine Specification

## JWT Lifecycle & Refresh Strategy

### Token Properties

- **Masa Berlaku Token (JWT Expiry):** Configured to **5 minutes** (`expiresIn: '5m'`) in Strapi (`config/plugins.js`).
- **Session Data (Jetpack DataStore):** Stores `jwt_token`, `refresh_token`, and `token_timestamp`.

### Expiration & Refresh Flow (OkHttp Interceptor)

When an API request returns **HTTP 401 Unauthorized**:

1. **Attempt Silent Refresh:**
   - Interceptor intercepts the 401 error.
   - Triggers a synchronized refresh request using stored refresh tokens or credentials.
   - On success: Updates `jwt_token` in `DataStore`, attaches the new token to the request, and retries seamlessly.

2. **Failed Refresh / Unrecoverable Auth:**
   - If silent refresh fails (e.g., refresh token expired, user blocked), clear local session tokens from `DataStore` **only** (do **not** clear Room).
   - Persist `skip_visit_sync_on_login=true` in DataStore (survives process death; **not** cleared by `clearSession`).
   - Send a one-time UI Side-Effect via Kotlin `Channel` (`AuthEffect.ForceReLogin`).
   - Navigate user back to `LoginScreen` and display a clear prompt: _"Sesi Anda telah berakhir. Silakan login kembali."_
   - On the subsequent login, **skip** remote visit-report pull when that flag is set, then clear the flag.

---

## Post-Login Seed (Room SSOT)

After successful `POST /api/auth/local`:

1. **Always** seed stores: `GET /api/stores` → replace local `stores` table.
2. **Visit reports pull** (`GET /api/visit-reports?populate=*`) when `skip_visit_sync_on_login` is **not** set:
   - Replace local `visit_reports` with remote rows as `SYNCED` (safe after explicit logout wipe; skipped after ForceReLogin).
   - Store absolute photo URL in `localImagePath` when local file is absent (detail UI loads via Coil).
   - **ForceReLogin recovery login:** skip this pull entirely (flag from DataStore).

---

## Offline Strategy & JWT Bypass

1. **Online State:**
   - All network requests pass through `AuthInterceptor` with `Authorization: Bearer <jwt_token>`.

2. **Offline State:**
   - Network calls are skipped completely.
   - If internet is unavailable when submitting a visit report, **bypass remote JWT verification entirely**.
   - Save the visit report immediately into Room DB with `syncStatus = "PENDING_SYNC"`.

---

## Background Sync (`SyncReportWorker`)

- **Framework:** WorkManager `CoroutineWorker`.
- **Constraint:** `NetworkType.CONNECTED`.

### Worker Execution Workflow

1. **Query Pending Data:** Query `VisitReportDao` for records where `syncStatus` is `"PENDING_SYNC"` or `"FAILED"` (failed rows stay eligible for automatic / manual retry).
2. **Verify / Refresh Token:**
   - Read active JWT from `DataStore`.
   - If expired, execute silent token refresh before sending payloads.
   - If refresh fails due to invalid credentials, pause `WorkManager` execution and post a notification or event triggering re-login on next app open.
3. **Two-step upload (Strapi 5):**
   - `POST /api/upload` (multipart `files`) to store the photo; read returned file `id`.
   - `POST /api/visit-reports` (JSON) with `data`: `store`, `latitude`, `longitude`, `order_notes`, `photo` (uploaded file id).
4. **State Transition:**
   - **HTTP 200/201 (Success):** Update Room DB record `syncStatus = "SYNCED"`.
   - **HTTP 5xx / Network Error:** Update Room DB record `syncStatus = "FAILED"` (eligible for manual or scheduled retry).

---

## Explicit Logout Cleanup

When the user confirms logout on Home:

1. Cancel unique WorkManager work for visit-report upload.
2. `clearAllTables()` on Room (stores + visit reports).
3. Clear JWT / refresh tokens from DataStore.
4. Navigate to `LoginScreen`.

Next successful login then re-seeds stores **and** pulls visit reports from Strapi (see Post-Login Seed).
