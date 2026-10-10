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
    val syncStatus: String, // "PENDING_SYNC", "SYNCED", "FAILED"
    val createdAt: Long = System.currentTimeMillis()
)
```

## Local Storage Rules

- Room DAOs: All read queries return Flow<List<T>> for reactive UI updates.
- DataStore Preferences (AuthPreferences):
  - Stores jwt_token (String) and token_timestamp (Long).
  - Managed as a @Single Koin instance.
- Image Caching: Photo captured via Camera launcher compressed and stored in context.filesDir / cacheDir, referencing absolute local paths in VisitReportEntity.
