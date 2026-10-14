# 01. System Overview & Architecture Rules

## Business Context

Canvassku is an offline-first sales canvassing app for field agents in areas with unstable or no internet.

## Core Architectural Principles

1. Clean Architecture + MVVM
   - Presentation → Domain → Data layer separation.
2. ViewModel, state, and side-effects
   - UI state: Kotlin Flow backing fields (`_uiState` MutableStateFlow + `uiState` StateFlow).
   - State naming: screen state types use the `*State` suffix (e.g. `LoginState`, `HomeState`), not `*UiState`.
   - Single state holder: form fields and exclusive status live in one `*State` object (no parallel `StateFlow`s for the same screen).
   - State updates: always via `_uiState.update { ... }` (never `_uiState.value = ...`) for atomic copies.
   - Exclusive status: nested or sibling `sealed class` / `sealed interface` for mutually exclusive modes (`Idle`, `Loading`, `Success`, `Error`).
   - Side-effects: one-time UI events (navigation, snackbar) via Kotlin `Channel` (`_effect` Channel + `effect` Flow).
3. Domain and UseCase rules
   - Create a `UseCase` only when domain validation is required or when orchestrating multiple repositories.
   - For a simple read/write pass-through, the ViewModel must call the `Repository` directly.
4. Dependency injection (Koin Compiler Plugin)
   - Native `io.insert-koin.compiler.plugin` (Kotlin K2 IR plugin; no KSP processor for Koin).
   - Annotations `@Module`, `@ComponentScan("com.didik.canvassku")`, `@Single`, `@Factory`, `@KoinViewModel` processed at compile time by the Kotlin compiler.
   - DI module at `core/di/AppModule.kt`, started in `CanvasskuApp.kt`.
   - Interface binding is automatic: a `@Singleton` / `@Factory` implementation binds to its supertypes. Do not add manual `provideX(impl): Interface` wrappers.
5. Offline-first
   - Room is the Single Source of Truth (SSOT).
   - UI reads from and writes to Room only.
6. Design authority
   - `specs/DESIGN.md` (Google Stitch) is the authority for color tokens, typography, and component specs.

## Package Directory Structure

```text
com.didik.canvassku/
├── CanvasskuApp.kt                     # Application class with Koin startKoin
├── MainActivity.kt                     # Single Activity with SplashScreen API & Navigation3
├── core/
│   ├── di/                             # Koin DI Module (@Module & @ComponentScan)
│   ├── design/                         # Material 3 Theme (Color.kt, Type.kt, Theme.kt from DESIGN.md)
│   └── util/                           # Helpers (FusedLocation, ImageCompressor)
├── data/
│   ├── local/
│   │   ├── db/                         # AppDatabase, StoreDao, VisitReportDao
│   │   ├── entity/                     # StoreEntity, VisitReportEntity
│   │   └── preferences/                # AuthPreferences (DataStore)
│   ├── remote/
│   │   ├── api/                        # StrapiApi (Retrofit with suspend functions)
│   │   ├── dto/                        # LoginRequestDto, LoginResponseDto, VisitReportDto
│   │   ├── interceptor/                # AuthInterceptor, TokenAuthenticator (401 Refresh)
│   │   └── AuthEventBus.kt             # Channel one-shot sessionExpired → force re-login
│   ├── repository/                     # StoreRepositoryImpl, VisitRepositoryImpl, AuthRepositoryImpl
│   └── worker/                         # SyncReportWorker (WorkManager background engine)
├── domain/
│   ├── model/                          # Store, VisitReport, SyncStatus, StoreListItem
│   ├── repository/                     # StoreRepository, VisitRepository, AuthRepository, SyncScheduler
│   └── usecase/                        # LoginUseCase, SaveVisitReportUseCase (multi-repo / validation)
└── presentation/
    ├── navigation/                     # NavRoute contracts & Navigation3 Root NavHost
    └── screens/
        ├── auth/                       # LoginViewModel, LoginScreen, LoginState
        ├── home/                       # HomeViewModel, HomeScreen, HomeState
        │   └── components/             # EmptyStoresState, StoreCard (when >1 sub-composables)
        ├── visit_form/                 # VisitFormViewModel, VisitFormScreen
        └── visit_detail/               # VisitDetailViewModel, VisitDetailScreen
```
