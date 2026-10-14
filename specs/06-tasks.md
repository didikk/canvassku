# 06. Executable Implementation Tasks

## Phase 1: Backend & Local Environment Setup (Strapi CMS)

- [x] Initialize local Strapi CMS project in the `backend/` directory.
- [x] Configure 5-minute JWT expiration in Strapi configuration (`config/plugins.js`).
- [x] Create Content Type `stores` (fields: `name` [String, Req], `address` [Text], `owner_name` [String]).
- [x] Create Content Type `visit-reports` (fields: `store` [Relation -> Has One `stores`], `latitude` [Float, Req], `longitude` [Float, Req], `order_notes` [Text, Req], `photo` [Media Single, Req]).
- [x] Configure Public Role permissions for auth endpoints (`POST /api/auth/local`, `POST /api/auth/refresh-token`) and Authenticated Role permissions for `stores` and `visit-reports`.
- [x] Seed 5 initial dummy stores in Strapi Admin Panel.
- [x] Seed bootstrap accounts: admin `admin@mail.test` / `admin123!` and app user `budi@mail.test` / `sales123!`.

## Phase 2: Android Infrastructure, Architecture & Theme Setup

- [x] Configure `gradle/libs.versions.toml` and `build.gradle.kts` dependencies for Room DB, Koin (using native Koin Compiler Plugin), Navigation3, Jetpack DataStore, WorkManager, Retrofit2 + OkHttp3, Play Services Location, and Core Splashscreen (`androidx.core:core-splashscreen`).
- [x] Apply the Koin Compiler Plugin (`io.insert-koin.compiler.plugin`) in `build.gradle.kts`.
- [x] Establish package folder structure under `com.didik.canvassku` (`core/`, `data/`, `domain/`, `presentation/navigation/`, `presentation/screens/`).
- [x] Create Material 3 Compose Theme (`Color.kt`, `Theme.kt`, `Type.kt`) mapping colors, typography, and shapes directly from `@specs/DESIGN.md`.
- [x] Configure Android 12+ Splash Screen theme (`Theme.App.Starting`) with Deep Blue (`#1A365D`) background and icon branding.
- [x] Implement Jetpack DataStore wrapper (`AuthPreferences`) storing `jwt_token`, `refresh_token`, and `token_timestamp`.
- [x] Implement Room DB entities (`StoreEntity`, `VisitReportEntity`), DAOs (`StoreDao`, `VisitReportDao`), and `AppDatabase`.
- [x] Configure Koin Module in `core/di/AppModule.kt` using `@Module` and `@ComponentScan("com.didik.canvassku")` annotations and initialize Koin in `CanvasskuApp.kt`.
- [x] Setup Navigation3 route contracts (`sealed interface NavRoute`) and root `NavHost` / stack controller in `presentation/navigation/`.

## Phase 3: Auth & Store List (Dashboard)

- [x] Implement `installSplashScreen()` in `MainActivity` with `setKeepOnScreenCondition` bound to `AuthPreferences` initial check, setting start route directly to `HomeScreen` or `LoginScreen`.
- [x] Implement Retrofit API interface (`StrapiApi`) using Kotlin `suspend` functions.
- [x] Implement OkHttp `AuthInterceptor` and `Authenticator` for attaching `Bearer` tokens, handling silent token refresh on 401 response, and emitting `AuthEventBus.sessionExpired` (`Channel<Unit>`) on refresh failure.
- [x] Implement `LoginScreen` UI matching `@specs/DESIGN.md` and `LoginViewModel` inside `presentation/screens/auth/` (MVVM with StateFlow backing fields, Channel effects, and `LoginState`).
- [x] Implement initial store master data fetch (`GET /api/stores`) post-login to seed Room DB.
- [x] Implement visit-report pull (`GET /api/visit-reports?populate=*`) post-login to seed Room as `SYNCED`; skip pull when `skip_visit_sync_on_login` is set in DataStore; detail shows remote photo URL when no local file.
- [x] Implement `HomeScreen` UI inside `presentation/screens/home/` displaying stores directly from Room DB (`Flow<List<Store>>`).
- [x] Add TopAppBar with manual sync trigger button and logout action (confirm dialog; logout clears Room + cancels pending sync work).
- [x] Home `PendingSyncBanner`: calm “Mengirim…” while report sync `RUNNING`; alarm “belum terkirim” + Kirim Sekarang when idle pending; snackbar “Laporan terkirim.” when pending hits 0 (suppressed on logout).

## Phase 4: Visit Form & Offline Engine

- [x] Implement `FusedLocationProviderClient` wrapper for passive GPS locking in `core/util/`.
- [x] Implement Camera launcher & local photo compression utility (`context.filesDir`).
- [x] Implement `VisitFormScreen` UI inside `presentation/screens/visit_form/` with automatic location badge, photo preview, order notes field, and submit button.
- [x] Implement `VisitFormViewModel` calling `SaveVisitReportUseCase` to persist reports directly to Room DB with `syncStatus = PENDING_SYNC`, enqueue sync, and navigate Home without waiting for upload.
- [x] Implement `SyncReportWorker` (`CoroutineWorker`) in `data/worker/` with `NetworkType.CONNECTED` constraint to query pending records, perform silent token refresh if needed, upload multipart payloads to Strapi, and update Room status (`SYNCED` / `FAILED`).

## Phase 5: Read-Only Visit Detail & Edge Case Verification

- [x] Implement `VisitDetailScreen` UI inside `presentation/screens/visit_detail/` for viewing saved/synced report details with status `AssistChip` (`PENDING_SYNC`, `SYNCED`, `FAILED`).
- [x] Test offline token bypass: Create a visit report offline with an expired JWT (> 5m) and verify instant save to Room DB without blocking the user.
- [x] Test auto-sync recovery: Create report offline, then reconnect network and verify `SyncReportWorker` auto-uploads to Strapi.

## Phase 6: Automated Testing & Verification

- [ ] **Unit Tests (Data Layer):**
  - [ ] Write `AuthInterceptor` & `TokenAuthenticator` tests using `MockWebServer` to verify silent 401 token refresh logic and retry flow.
  - [ ] Write `AuthPreferences` test using in-memory DataStore to verify token persistence and clear session actions.
- [ ] **Unit Tests (Domain / ViewModel):**
  - [ ] Write `VisitFormViewModelTest` verifying that saving a report triggers `SaveVisitReportUseCase` and transitions UI state to `Success`.
  - [ ] Write `SaveVisitReportUseCaseTest` to verify that offline reports are saved to Room DB with `syncStatus = PENDING_SYNC`.
- [ ] **Integration Tests (Room DB):**
  - [ ] Write Room DAO in-memory database tests (`StoreDaoTest`, `VisitReportDaoTest`) verifying reactive `Flow` emissions and status update transitions (`PENDING_SYNC` → `SYNCED`) via `SyncStatus` TypeConverter.
- [ ] **Worker Verification Test:**
  - [ ] Write `SyncReportWorkerTest` using `WorkManagerTestInitHelper` to verify background task execution under `NetworkType.CONNECTED` constraint.
