# Development Workflow

Canvassku was built with AI assistance against written specs. After each phase, AI review checked the output against those specs. I also reviewed the code manually and ran tests plus field QA.

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

Those files defined system boundaries before coding: Room owns readable state, Strapi owns remote persistence, and the sync worker bridges the gap without blocking the visit form. Architecture details (Koin, Clean Architecture, StateFlow rules) live in `specs/01-overview-and-architecture.md`.

## Design System

Screen layouts and Material 3 tokens live in `specs/DESIGN.md`. The Android theme (`Color.kt`, `Type.kt`, `Theme.kt`) maps directly from that file. Screen design references for Login, Home, Visit Form, and Visit Detail are listed in `specs/05-ui-navigation-and-design.md`.

Microcopy stays in Bahasa Indonesia and favors operational wording (`Tersimpan Lokal`, `Mengirim laporan…`, `Kirim Sekarang`) over abstract tech labels.

## How Quality Is Enforced

AI does not invent product rules. Specs lock architecture, storage, auth/sync, UI, and the phase checklist before implementation starts. Generation and review both have to match those files.

Quality is checked in layers:

1. Specs first  
   Room as SSOT, JWT refresh, WorkManager sync, and screen state rules are written down. If a change conflicts with a spec, the spec or the change is wrong; there is no silent exception.

2. AI implementation against specs  
   Backend and Android are generated from the same contracts so API and client stay aligned. Freeform “just make it work” code is rejected when it breaks a written rule.

3. AI code review after each phase  
   When a phase in `specs/06-tasks.md` finishes, AI review prompts are run against the specs to catch:
   - Architecture leaks (UI talking past domain, or network writes skipping Room)
   - Auth/sync edge cases from `03` and `04` (session-expired vs logout, skip visit pull flag, failed sync retry eligibility)
   - Drift from DESIGN.md tokens or pending-sync banner rules  

   Findings are fixed before the next phase starts.

4. Manual engineering review  
   Line-by-line review in Android Studio after AI review. Focus areas included code structure and layer boundaries, naming and readability, state ownership, concurrency and lifecycle safety, error handling, and cleanup of unused or unsafe generated code. AI review checks spec fit; this pass judges whether the code is solid enough to keep.

5. Automated tests and field QA  
   Unit and integration tests cover interceptor refresh, Room status transitions, and WorkManager sync (`./gradlew test`; see [android/README.md](../android/README.md)). Device checks cover the offline path below.

## Field QA

Offline behavior was checked on device, not only in unit tests:

1. Enable Airplane Mode.
2. Create a visit report (location + photo + notes). Confirm it lands in Room as `PENDING_SYNC` and Home shows the pending banner.
3. Disable Airplane Mode.
4. Confirm `SyncReportWorker` runs under `NetworkType.CONNECTED`, uploads photo then report to Strapi, and Room status moves to `SYNCED`.

Also verified: expired JWT during offline save does not block local persistence; silent refresh runs when the network returns and sync resumes.
