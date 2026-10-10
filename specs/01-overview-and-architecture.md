# 01. System Overview & Architecture Rules

## Business Context

Canvassku is an offline-first mobile sales canvassing application built for field sales agents operating in areas with unstable or non-existent internet connectivity.

## Core Architectural Principles

1. **Clean Architecture + MVVM:**
   - Presentation -> Domain -> Data layer separation.
2. **ViewModel, State & Side-Effects:**
   - **UI State:** Exposed via Kotlin Flow backing fields (`_uiState` MutableStateFlow + `uiState` StateFlow).
   - **Exclusive State:** Represented via `sealed class` or `sealed interface` (`Idle`, `Loading`, `Success`, `Error`).
   - **Side-Effects:** One-time UI events (navigation, snackbar) sent via Kotlin `Channel` (`_effect` Channel + `effect` Flow).
3. **Domain & UseCase Rules:**
   - Create `UseCase` ONLY when domain business logic validation is required OR when orchestrating multiple repositories.
   - If a request is a simple read/write pass-through, the ViewModel MUST call the `Repository` directly.
4. **Dependency Injection (Koin Compiler Plugin):**
   - Uses native `io.insert-koin.compiler.plugin` (Kotlin K2 IR plugin; no KSP processor needed for Koin).
   - Annotated with `@Module`, `@ComponentScan("com.canvassku")`, `@Single`, `@Factory`, `@KoinViewModel` processed at compile-time directly by the Kotlin compiler.
   - DI module is located at `core/di/AppModule.kt` and initialized in `CanvasskuApp.kt`.
5. **Offline-First Paradigm:**
   - Room Database acts as the Single Source of Truth (SSOT).
   - UI strictly reads from and writes to Room DB.
6. **Design Authority:**
   - `specs/DESIGN.md` (Google Stitch) is the authority for color tokens, typography, and component specs.

## Package Directory Structure

```text
com.canvassku/
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
        ├── auth/                       # LoginViewModel, LoginScreen
        ├── home/                       # HomeViewModel, HomeScreen (Store List / Dashboard)
        ├── visit_form/                 # VisitFormViewModel, VisitFormScreen
        └── visit_detail/               # VisitDetailViewModel, VisitDetailScreen
```
