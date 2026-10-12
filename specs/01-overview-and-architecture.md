# 01. System Overview & Architecture Rules

## Business Context

Canvassku is an offline-first mobile sales canvassing application built for field sales agents operating in areas with unstable or non-existent internet connectivity.

## Core Architectural Principles

1. **Clean Architecture + MVVM:**
   - Presentation -> Domain -> Data layer separation.
2. **ViewModel, State & Side-Effects:**
   - **UI State:** Exposed via Kotlin Flow backing fields (`_uiState` MutableStateFlow + `uiState` StateFlow).
   - **State naming:** Screen state types use the `*State` suffix (e.g. `LoginState`, `HomeState`), not `*UiState`.
   - **Single state holder:** Form fields and exclusive status live in one `*State` object (no parallel `StateFlow`s for the same screen).
   - **State updates:** Always mutate via `_uiState.update { ... }` (never `_uiState.value = ...`) for consistency and atomic copies.
   - **Exclusive Status:** Nested or sibling `sealed class` / `sealed interface` for mutually exclusive modes (`Idle`, `Loading`, `Success`, `Error`).
   - **Side-Effects:** One-time UI events (navigation, snackbar) sent via Kotlin `Channel` (`_effect` Channel + `effect` Flow).
3. **Domain & UseCase Rules:**
   - Create `UseCase` ONLY when domain business logic validation is required OR when orchestrating multiple repositories.
   - If a request is a simple read/write pass-through, the ViewModel MUST call the `Repository` directly.
4. **Dependency Injection (Koin Compiler Plugin):**
   - Uses native `io.insert-koin.compiler.plugin` (Kotlin K2 IR plugin; no KSP processor needed for Koin).
   - Annotated with `@Module`, `@ComponentScan("com.didik.canvassku")`, `@Single`, `@Factory`, `@KoinViewModel` processed at compile-time directly by the Kotlin compiler.
   - DI module is located at `core/di/AppModule.kt` and initialized in `CanvasskuApp.kt`.
   - Interface binding is automatic: a `@Singleton` / `@Factory` implementation class is bound to its supertypes. Do not add manual `provideX(impl): Interface` wrappers.
5. **Offline-First Paradigm:**
   - Room Database acts as the Single Source of Truth (SSOT).
   - UI strictly reads from and writes to Room DB.
6. **Design Authority:**
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
│   │   └── interceptor/                # AuthInterceptor, TokenAuthenticator (401 Refresh)
│   ├── repository/                     # StoreRepositoryImpl, VisitRepositoryImpl, AuthRepositoryImpl
│   └── worker/                         # SyncReportWorker (WorkManager background engine)
├── domain/
│   ├── model/                          # Store, VisitReport, AuthSession
│   ├── repository/                     # StoreRepository, VisitRepository, AuthRepository
│   └── usecase/                        # SaveVisitReportUseCase (Only when multi-repo/validation needed)
└── presentation/
    ├── navigation/                     # NavRoute contracts & Navigation3 Root NavHost
    └── screens/                        # Encapsulated Screen Features
        ├── auth/                       # LoginViewModel, LoginScreen, LoginState
        ├── home/                       # HomeViewModel, HomeScreen, HomeState
        │   └── components/             # EmptyStoresState, StoreCard (when >1 sub-composables)
        ├── visit_form/                 # VisitFormViewModel, VisitFormScreen
        └── visit_detail/               # VisitDetailViewModel, VisitDetailScreen
```

