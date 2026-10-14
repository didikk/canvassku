# Canvassku Backend (Strapi)

Strapi 5 CMS for Canvassku, using SQLite (`better-sqlite3`) for local development. The Android app talks to the Content API for auth, stores, visit reports, and media upload.

## Requirements

- Node.js 20+ (see `package.json` `engines`)
- npm 6+

## Quick Start

```bash
cd backend
npm install
npm run develop
```

| URL | Purpose |
| --- | --- |
| [http://localhost:1337/admin](http://localhost:1337/admin) | Admin panel |
| `http://localhost:1337/api` | Content API |

Other scripts:

```bash
npm run build    # build admin panel
npm run start    # run without auto-reload
```

## Automated Seeding (`src/index.ts`)

On `npm run develop`, `bootstrap()` in `src/index.ts` runs idempotent setup so a fresh clone works without committing local SQLite files:

1. Enable Public permissions for local login and token refresh.
2. Enable Authenticated permissions for stores, visit-reports, and upload.
3. Create Strapi admin `admin@mail.test` / `admin123!` if missing.
4. Create app user `budi` (`budi@mail.test` / `sales123!`) in the Authenticated role if missing.
5. Seed five dummy stores when the `stores` collection is empty.

Re-running bootstrap skips rows that already exist (matched by email or existing store count).

### Seed credentials

| Role | Identifier | Password |
| --- | --- | --- |
| Strapi Admin | `admin@mail.test` | `admin123!` |
| App user | `budi@mail.test` (username `budi`) | `sales123!` |

## Schema Definitions

### `stores`

| Field | Type | Notes |
| --- | --- | --- |
| `name` | Short text | Required |
| `address` | Long text | Optional |
| `owner_name` | Short text | Optional |

### `visit-reports`

| Field | Type | Notes |
| --- | --- | --- |
| `store` | Relation (Has One → `stores`) | Required |
| `latitude` | Decimal | Required |
| `longitude` | Decimal | Required |
| `order_notes` | Long text | Required |
| `photo` | Media (single image) | Required; upload via `/api/upload` first (Strapi 5) |

Schemas live under `src/api/*/content-types/*/schema.json`.

## JWT Configuration

`config/plugins.ts` configures `users-permissions` with refresh-token session management and a short access token lifetime for client testing:

- `jwt.expiresIn: '5m'`
- `sessions.accessTokenLifespan: 300` seconds (5 minutes)

The Android client relies on this short TTL to exercise silent refresh through OkHttp `TokenAuthenticator`. Refresh tokens use longer lifespans (see the same config file).

## Auth Endpoints Used by the App

| Method | Path | Role |
| --- | --- | --- |
| `POST` | `/api/auth/local` | Login |
| `POST` | `/api/auth/refresh` | Silent token refresh |
| `GET` | `/api/stores` | Post-login store seed |
| `GET` | `/api/visit-reports?populate=*` | Post-login visit pull (when allowed) |
| `POST` | `/api/upload` | Photo upload before report create |
| `POST` | `/api/visit-reports` | Create synced visit report |

## Related Docs

- [Root README](../README.md)
- [Development Workflow](../docs/DEVELOPMENT_WORKFLOW.md)
- [Android README](../android/README.md)
- Spec: [`../specs/02-backend-strapi.md`](../specs/02-backend-strapi.md)
