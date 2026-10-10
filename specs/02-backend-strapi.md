# 02. Backend Strapi Specification

## Auth & Token Refresh Configuration

- **Plugin:** Built-in `users-permissions` plugin.
- **JWT Expiration:** Configured strictly to **5 minutes** (`expiresIn: '5m'`) in `config/plugins.js`.
- **Auth Endpoints:**
  - `POST /api/auth/local` -> Initial login (returns `jwt` token and User object).
  - `POST /api/auth/refresh-token` (or `POST /api/auth/local` silent retry) -> Refreshes JWT token when HTTP 401 is encountered.

## Content Types & Schemas

### Collection: `stores`

- `name` (Short Text, Required)
- `address` (Long Text, Optional)
- `owner_name` (Short Text, Optional)

### Collection: `visit-reports`

- `store` (Relation -> Has One `stores`)
- `latitude` (Float / Decimal, Required)
- `longitude` (Float / Decimal, Required)
- `order_notes` (Long Text, Required)
- `photo` (Media Single Image, Required)
- `created_at` (DateTime)

## Role & Permission Settings

- **Public Role:** Allowed `auth.local` and `auth.refreshToken`
- **Authenticated Role:**
  - `stores`: `find`, `findOne`
  - `visit-reports`: `create`, `find`, `findOne`
