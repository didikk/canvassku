# 05. UI, Navigation & Design Specification

## Screen Stitch Mappings

- **Layar Login:** `https://stitch.withgoogle.com/projects/568185049473327263?node-id=570705487f424b10939eb0d92217dc45`
- **Layar Daftar Toko (Home):** `https://stitch.withgoogle.com/projects/568185049473327263?node-id=e405b4f5c45f41b393794bd455282ed3`
- **Layar Form Kunjungan:** `https://stitch.withgoogle.com/projects/568185049473327263?node-id=9745ead6cce44020be65fb8e0eefd688`
- **Layar Detail Kunjungan:** `https://stitch.withgoogle.com/projects/568185049473327263?node-id=099676cfb2ea4d8f85da67e6739f12e4`

## Navigation Architecture

- **Library:** Navigation3 (`androidx.navigation3`).
- **Route Contracts:** `sealed interface NavRoute` defined in `presentation/navigation/`.

## Screen Compose File Structure

- Keep the primary screen composable in `*Screen.kt` (e.g. `HomeScreen.kt`).
- Extract any additional private/supporting `@Composable` functions out of the screen file:
  - **One** supporting composable: place it in its own file next to the screen (same package).
  - **More than one** supporting composable: place them under a `components/` subfolder in that screen package (e.g. `presentation/screens/home/components/`).

## Home Pending-Sync Banner UX

After visit submit: save Room → enqueue WorkManager → navigate Home immediately (offline-first; do not await upload on the form).

`PendingSyncBanner` when `pendingReportCount > 0`:

| Condition | UI |
|-----------|-----|
| `isReportSyncing == true` (WorkInfo `RUNNING`) | Calm copy: _"Mengirim laporan…"_ / _"Sedang dikirim ke server"_. Cloud-upload icon. **No** "Kirim Sekarang". |
| `isReportSyncing == false` | Alarm copy: _"N Laporan belum terkirim"_ / _"Tersimpan di memori perangkat"_. SyncProblem icon + **Kirim Sekarang**. |

Snackbar: when pending count transitions from `> 0` to `0`, show _"Laporan terkirim."_ Suppress that message on explicit logout (Room wipe must not look like a successful sync).

Work waiting on network (`ENQUEUED` with `CONNECTED` constraint) is **not** syncing — keep the alarm banner so offline queue stays visible.
