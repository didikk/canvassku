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
   - If silent refresh fails (e.g., refresh token expired, user blocked), clear local session tokens from `DataStore`.
   - Send a one-time UI Side-Effect via Kotlin `Channel` (`AuthEffect.ForceReLogin`).
   - Navigate user back to `LoginScreen` and display a clear prompt: _"Sesi Anda telah berakhir. Silakan login kembali."_

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

1. **Query Pending Data:** Query `VisitReportDao` for all records where `syncStatus == "PENDING_SYNC"`.
2. **Verify / Refresh Token:**
   - Read active JWT from `DataStore`.
   - If expired, execute silent token refresh before sending payloads.
   - If refresh fails due to invalid credentials, pause `WorkManager` execution and post a notification or event triggering re-login on next app open.
3. **Multipart Data Upload:**
   - Construct Multipart Form-Data payload for `POST /api/visit-reports`.
   - Attach metadata fields (`store`, `latitude`, `longitude`, `order_notes`) and image file payload (`photo`).
4. **State Transition:**
   - **HTTP 200/201 (Success):** Update Room DB record `syncStatus = "SYNCED"`.
   - **HTTP 5xx / Network Error:** Update Room DB record `syncStatus = "FAILED"` (eligible for manual or scheduled retry).
