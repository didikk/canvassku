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
