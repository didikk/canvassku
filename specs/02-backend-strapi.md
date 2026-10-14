# 02. Backend Strapi Specification

## Auth & Token Refresh Configuration

- Plugin: built-in `users-permissions`.
- JWT expiration: **5 minutes** (`expiresIn: '5m'`) in `config/plugins.js`.
- Auth endpoints:
  - `POST /api/auth/local` → initial login (returns `jwt` and User object).
  - `POST /api/auth/refresh-token` (or `POST /api/auth/local` silent retry) → refresh JWT on HTTP 401.

## Content Types & Schemas

### Collection: `stores`

- `name` (Short Text, Required)
- `address` (Long Text, Optional)
- `owner_name` (Short Text, Optional)

### Collection: `visit-reports`

- `store` (Relation → Has One `stores`)
- `latitude` (Float / Decimal, Required)
- `longitude` (Float / Decimal, Required)
- `order_notes` (Long Text, Required)
- `photo` (Media Single Image, Required)
- `created_at` (DateTime)

## Role & Permission Settings

- Public role: `auth.local` and `auth.refreshToken`
- Authenticated role:
  - `stores`: `find`, `findOne`
  - `visit-reports`: `create`, `find`, `findOne` (`find` is required for post-login Room seed via `GET /api/visit-reports?populate=*`)
  - `upload` (`plugin::upload.content-api.upload`): required so the app can upload photos before creating a visit report (Strapi 5 no longer accepts `files.*` on entry create)

## Bootstrap Seed Accounts

Seeded on Strapi bootstrap (idempotent; skips if email already exists):

- Admin (Strapi Admin Panel): `admin@mail.test` / `admin123!`
- App user (`users-permissions`, Authenticated role): `budi@mail.test` / `sales123!`
