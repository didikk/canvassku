# 06. Executable Implementation Tasks

## Phase 1: Backend & Local Environment Setup (Strapi CMS)

- [x] Initialize local Strapi CMS project in the `backend/` directory.
- [x] Configure 5-minute JWT expiration in Strapi configuration (`config/plugins.js`).
- [x] Create Content Type `stores` (fields: `name` [String, Req], `address` [Text], `owner_name` [String]).
- [x] Create Content Type `visit-reports` (fields: `store` [Relation -> Has One `stores`], `latitude` [Float, Req], `longitude` [Float, Req], `order_notes` [Text, Req], `photo` [Media Single, Req]).
- [x] Configure Public Role permissions for auth endpoints (`POST /api/auth/local`, `POST /api/auth/refresh-token`) and Authenticated Role permissions for `stores` and `visit-reports`.
- [x] Seed 5 initial dummy stores in Strapi Admin Panel.

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

- [ ] Implement `installSplashScreen()` in `MainActivity` with `setKeepOnScreenCondition` bound to `AuthPreferences` initial check, setting start route directly to `HomeScreen` or `LoginScreen`.
- [ ] Implement Retrofit API interface (`StrapiApi`) using Kotlin `suspend` functions.
- [ ] Implement OkHttp `AuthInterceptor` and `Authenticator` for attaching `Bearer` tokens, handling silent token refresh on 401 response, and emitting `AuthEffect.ForceReLogin` channel effect on refresh failure.
- [ ] Implement `LoginScreen` UI matching `@specs/DESIGN.md` and `LoginViewModel` inside `presentation/screens/auth/` (MVVM with StateFlow backing fields, Channel effects, and `LoginUiState` sealed interface).
- [ ] Implement initial store master data fetch (`GET /api/stores`) post-login to seed Room DB.
- [ ] Implement `HomeScreen` UI inside `presentation/screens/home/` displaying stores directly from Room DB (`Flow<List<Store>>`).
- [ ] Add TopAppBar with manual sync trigger button and logout action.

## Phase 4: Visit Form & Offline Engine

- [ ] Implement `FusedLocationProviderClient` wrapper for passive GPS locking in `core/util/`.
- [ ] Implement Camera launcher & local photo compression utility (`context.filesDir`).
- [ ] Implement `VisitFormScreen` UI inside `presentation/screens/visit_form/` with automatic location badge, photo preview, order notes field, and submit button.
- [ ] Implement `VisitFormViewModel` calling `SaveVisitReportUseCase` to persist reports directly to Room DB with `syncStatus = "PENDING_SYNC"`.
- [ ] Implement `SyncReportWorker` (`CoroutineWorker`) in `data/worker/` with `NetworkType.CONNECTED` constraint to query pending records, perform silent token refresh if needed, upload multipart payloads to Strapi, and update Room status (`SYNCED` / `FAILED`).

## Phase 5: Read-Only Visit Detail & Edge Case Verification

- [ ] Implement `VisitDetailScreen` UI inside `presentation/screens/visit_detail/` for viewing saved/synced report details with status `AssistChip` (`PENDING_SYNC`, `SYNCED`, `FAILED`).
- [ ] Add manual retry button on `VisitDetailScreen` for reports with status `FAILED`.
- [ ] Test offline token bypass: Create a visit report offline with an expired JWT (> 5m) and verify instant save to Room DB without blocking the user.
- [ ] Test auto-sync recovery: Create report offline, then reconnect network and verify `SyncReportWorker` auto-uploads to Strapi.
