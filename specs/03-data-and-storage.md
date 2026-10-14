# 03. Data & Storage Specification

## Room Database Entities (SSOT)

```kotlin
@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey val id: String,
    val name: String,
    val address: String?,
    val ownerName: String?
)

@Entity(tableName = "visit_reports")
data class VisitReportEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val storeId: String,
    val latitude: Double,
    val longitude: Double,
    val orderNotes: String,
    val localImagePath: String,
    val syncStatus: SyncStatus, // Room TypeConverter stores enum name
    val createdAt: Long = System.currentTimeMillis()
)

enum class SyncStatus {
    NOT_VISITED,   // list UI only; never persisted on a visit_reports row
    PENDING_SYNC,
    SYNCED,
    FAILED,
}
```

- Persist only `PENDING_SYNC` / `SYNCED` / `FAILED` on reports. Home store list derives status from the latest report (or `NOT_VISITED` when none). Failed reports map to `PENDING_SYNC` for home filters.

## Local Storage Rules

- Room DAOs: all read queries return `Flow<List<T>>` for reactive UI updates.
- `AppDatabase` registers `Converters` for `SyncStatus` ↔ String (enum `.name`).
- DataStore Preferences (`AuthPreferences`):
  - Stores `jwt_token` (String) and `token_timestamp` (Long).
  - Managed as a `@Single` Koin instance.
- Image caching: camera capture is compressed and stored under `context.filesDir` / `cacheDir`; `VisitReportEntity` stores absolute local paths.
- Remote photos (post-login seed): `localImagePath` may hold an `http(s)` URL; Visit Detail loads file or URL via Coil (`SubcomposeAsyncImage`).

## Logout & Local Data Wipe

- Explicit logout (Home confirm dialog) must:
  1. Cancel pending `SyncReportWorker` unique work.
  2. Clear all Room tables (`AppDatabase.clearAllTables()`).
  3. Clear auth session from DataStore.
- Pending / failed visit reports that were not uploaded are permanently lost after logout confirm.
- Orphan local photo files under `filesDir` may remain (not required to delete in MVP).

## Session-Expired Re-Login vs Logout

- Session expired (refresh failure → `AuthEventBus.emitSessionExpired()`): clear session tokens only; set `skip_visit_sync_on_login` in DataStore. Room data stays so offline pending reports are not wiped.
- Explicit logout: wipe Room + cancel sync work + clear session + clear skip flag (see above).
- Visit pull (`syncFromRemote`) runs only when skip flag is unset; it may replace the whole `visit_reports` table.
