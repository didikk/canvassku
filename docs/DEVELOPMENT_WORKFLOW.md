# Development Workflow

This is a working record of how Canvassku was planned, designed, implemented, and verified. The app was built with AI assistance, then reviewed and tested by a human engineer.

## Spec-Driven Method

Implementation started from modular specs in [`specs/`](../specs/), not from ad-hoc screen code.

| Spec | What it locked down |
| --- | --- |
| `01-overview-and-architecture.md` | Clean Architecture + MVVM, package layout, Koin K2 IR rules, Room as SSOT |
| `02-backend-strapi.md` | Content types, roles, seed accounts, JWT lifetime |
| `03-data-and-storage.md` | Room entities, sync status rules, logout vs session-expired wipe behavior |
| `04-jwt-and-sync-engine.md` | Silent 401 refresh, post-login seed, WorkManager upload flow |
| `05-ui-navigation-and-design.md` | Navigation3 routes, screen ownership, pending-sync banner behavior |
| `06-tasks.md` | Phased checklist from Strapi setup through automated tests |
| `DESIGN.md` | Material 3 color, type, and component tokens |

Those files defined system boundaries before coding: Room owns readable state, Strapi owns remote persistence, and the sync worker bridges the gap without blocking the visit form.

## UI/UX Design System

Screen layouts and Material 3 tokens were produced with AI-assisted design tooling, then captured as `specs/DESIGN.md`. The Android theme (`Color.kt`, `Type.kt`, `Theme.kt`) maps directly from that file. Screen design references for Login, Home, Visit Form, and Visit Detail are listed in `specs/05-ui-navigation-and-design.md`.

Microcopy stays in Bahasa Indonesia and favors operational wording (`Tersimpan Lokal`, `Mengirim laporan…`, `Kirim Sekarang`) over abstract tech labels.

## AI-Assisted Implementation

An AI coding agent kept shared context across `backend/` and `android/` so API contracts and client behavior stayed aligned. During generation, the agent had to follow these constraints:

- Koin Compiler Plugin (`io.insert-koin.compiler.plugin`) with K2 IR annotations (`@Module`, `@ComponentScan`, `@Single`, `@Factory`, `@KoinViewModel`). No KSP-based Koin processor.
- Clean Architecture layer direction: Presentation → Domain → Data.
- ViewModels expose one `*State` via `StateFlow`, update with `_uiState.update { ... }`, and send one-shot effects through a `Channel`.
- UseCases only when validation or multi-repository orchestration is required; otherwise ViewModels call repositories directly.

## Dual Code Review Process

### Automated AI code review

After each phase in `specs/06-tasks.md`, AI review prompts were run against the specs to catch:

- Architecture leaks (UI talking past domain, or network writes skipping Room)
- Missing edge cases from `03` and `04` (session-expired vs logout, skip visit pull flag, failed sync retry eligibility)
- Drift from DESIGN.md tokens or pending-sync banner rules

Findings were fixed before the next phase started.

### Manual engineering review

Line-by-line review in Android Studio focused on what automated checks miss easily:

- State immutability and exclusive status sealed types
- Thread safety around token refresh and DataStore reads
- Resource cleanup on logout (cancel WorkManager unique work, clear Room tables, clear session)

## Field QA Verification

Offline behavior was checked on device, not only in unit tests:

1. Enable Airplane Mode.
2. Create a visit report (location + photo + notes). Confirm it lands in Room as `PENDING_SYNC` and Home shows the pending banner.
3. Disable Airplane Mode.
4. Confirm `SyncReportWorker` runs under `NetworkType.CONNECTED`, uploads photo then report to Strapi, and Room status moves to `SYNCED`.

Also verified: expired JWT during offline save does not block local persistence; silent refresh runs when the network returns and sync resumes.
