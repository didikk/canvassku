# Canvassku Android

Kotlin + Jetpack Compose client for Canvassku. The app reads and writes Room first; network sync is opportunistic.

## Architecture

Clean Architecture + MVVM with Room as the reactive single source of truth (SSOT).

- **Presentation:** Compose screens, ViewModels (`StateFlow` + `Channel` effects)
- **Domain:** models, repository interfaces, UseCases where validation or multi-repo orchestration is needed
- **Data:** Room, DataStore, Retrofit/OkHttp, WorkManager, repository implementations

UI never treats the network response as screen state. After a write, Room Flows drive recomposition.

## Directory Structure

```text
com.didik.canvassku/
├── CanvasskuApp.kt
├── MainActivity.kt
├── core/
│   ├── di/                  # Koin modules (@Module, @ComponentScan)
│   ├── design/              # Material 3 theme from specs/DESIGN.md
│   └── util/                # LocationHelper, ImageCompressor
├── data/
│   ├── local/
│   │   ├── db/              # AppDatabase, DAOs, converters
│   │   ├── entity/
│   │   └── preferences/     # AuthPreferences (DataStore)
│   ├── remote/
│   │   ├── api/
│   │   ├── dto/
│   │   ├── interceptor/     # AuthInterceptor, TokenAuthenticator
│   │   └── AuthEventBus.kt
│   ├── repository/
│   └── worker/              # SyncReportWorker, SyncScheduler
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
└── presentation/
    ├── navigation/          # NavRoute + Navigation3 host
    └── screens/
        ├── auth/
        ├── home/
        ├── visit_form/
        └── visit_detail/
```

## Technical Highlights

### Silent JWT refresh

OkHttp `AuthInterceptor` attaches `Authorization: Bearer <jwt>`. On HTTP 401, `TokenAuthenticator` performs a synchronized refresh, updates DataStore, and retries the request. If refresh fails, session tokens are cleared (Room is kept), `skip_visit_sync_on_login` is set, and `AuthEventBus` forces re-login.

Access tokens expire after 5 minutes (configured in Strapi) so refresh handling can be tested quickly.

### Background sync

`SyncReportWorker` (`CoroutineWorker`) runs with `NetworkType.CONNECTED`. It loads `PENDING_SYNC` / `FAILED` reports, refreshes the JWT if needed, uploads the photo via `POST /api/upload`, then creates the visit report via `POST /api/visit-reports`. Room status becomes `SYNCED` or `FAILED`.

### Splash Screen API

Android 12+ splash uses `Theme.App.Starting` (Deep Blue `#1A365D`) with `installSplashScreen()` in `MainActivity`, held until the initial auth route is known.

## Setup

1. Start the Strapi backend (see [backend/README.md](../backend/README.md)).
2. Open this `android/` folder in Android Studio.
3. Point the API base URL at your Strapi host (`10.0.2.2:1337` for the emulator).
4. On first launch (targetSdk 37+), grant `ACCESS_LOCAL_NETWORK` when prompted. Without it, calls to LAN or the emulator host (`10.0.2.2`) time out.
5. Sign in with `budi@mail.test` / `sales123!`.

The permission is declared in `AndroidManifest.xml` and requested at runtime via `LocalNetworkPermission.kt` / Login.

## Testing Guide

From the `android/` directory:

```bash
./gradlew test
```

Phase 6 coverage includes:

| Area | Approach |
| --- | --- |
| `AuthInterceptor` / `TokenAuthenticator` | MockWebServer for 401 refresh and retry |
| `AuthPreferences` | In-memory DataStore for token persist/clear |
| `VisitFormViewModel` / `SaveVisitReportUseCase` | Offline save → `PENDING_SYNC` |
| `StoreDao` / `VisitReportDao` | Room in-memory DB and Flow/status transitions |
| `SyncReportWorker` | `WorkManagerTestInitHelper` under connected constraints |

## Related Docs

- [Root README](../README.md)
- [Development Workflow](../docs/DEVELOPMENT_WORKFLOW.md)
- [Backend README](../backend/README.md)
- Specs: [`../specs/`](../specs/)
