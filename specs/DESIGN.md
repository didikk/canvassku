---
name: CanvassKu Mobile Field Engine
colors:
  surface: '#f7fafc'
  surface-dim: '#d7dadc'
  surface-bright: '#f7fafc'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f1f4f6'
  surface-container: '#ebeef0'
  surface-container-high: '#e5e9eb'
  surface-container-highest: '#e0e3e5'
  on-surface: '#181c1e'
  on-surface-variant: '#43474e'
  inverse-surface: '#2d3133'
  inverse-on-surface: '#eef1f3'
  outline: '#74777f'
  outline-variant: '#c4c6cf'
  surface-tint: '#455f88'
  primary: '#002045'
  on-primary: '#ffffff'
  primary-container: '#1a365d'
  on-primary-container: '#86a0cd'
  inverse-primary: '#adc7f7'
  secondary: '#0a6c44'
  on-secondary: '#ffffff'
  secondary-container: '#9ff5c1'
  on-secondary-container: '#167249'
  tertiary: '#2d1d00'
  on-tertiary: '#ffffff'
  tertiary-container: '#493100'
  on-tertiary-container: '#cb9524'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d6e3ff'
  primary-fixed-dim: '#adc7f7'
  on-primary-fixed: '#001b3c'
  on-primary-fixed-variant: '#2d476f'
  secondary-fixed: '#9ff5c1'
  secondary-fixed-dim: '#83d8a6'
  on-secondary-fixed: '#002111'
  on-secondary-fixed-variant: '#005231'
  tertiary-fixed: '#ffdeaa'
  tertiary-fixed-dim: '#f8bc4b'
  on-tertiary-fixed: '#271900'
  on-tertiary-fixed-variant: '#5f4100'
  background: '#f7fafc'
  on-background: '#181c1e'
  surface-variant: '#e0e3e5'
typography:
  title-large:
    fontFamily: Roboto Flex
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  title-medium:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: 0.15px
  body-large:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  body-medium:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.25px
  label-large:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-medium:
    fontFamily: Roboto Flex
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system establishes a high-utility, disciplined enterprise mobile experience tailored for fast-paced field sales agents and canvassers across Indonesia. Operating under the structural logic of Google Material Design 3 (Material You), the visual identity balances institutional reliability with rapid on-the-ground ergonomic efficiency.

### Design Principles
- **Field-Ready Ergonomics:** Interfaces are optimized for one-handed operation under direct sunlight, featuring clear visual affordances, large touch targets (minimum 48dp), and immediate visual feedback.
- **Data Clarity & Trust:** Financial, stock, and route tracking rely on restrained, structured hierarchy to eliminate ambiguity during high-velocity merchant visits.
- **Systematic Cohesion:** Adheres directly to Material 3 dynamic container models, elevation levels, and typography roles to ensure standard Android native integration.

### Voice & Tone (Bahasa Indonesia)
Microcopy is concise, direct, and action-oriented. Statuses avoid ambiguous tech jargon, favoring transparent, operational terms:
- Completed states: *"Tersinkronisasi"*, *"Kunjungan Selesai"*
- Pending/Offline states: *"Menunggu Sinkronisasi"*, *"Tersimpan Lokal"*
- Direct Calls to Action: *"Mulai Kunjungan"*, *"Check-in Lokasi"*, *"Kirim Laporan"*

## Colors

The color system adapts enterprise corporate rigor to Material 3 container tokens, emphasizing readability and operational statuses.

### Core Roles
- **Primary (`#1A365D`):** Deep Blue. Anchors top app bars, primary action buttons, key indicators, and active navigation nodes. Communicates security and institutional strength.
- **On-Primary (`#FFFFFF`):** High-contrast crisp white applied to glyphs and labels sitting atop primary surfaces.
- **Secondary (`#2F855A`):** Forest Green. Represents completed field actions, validated check-ins, successful transactions, and active targets.
- **Tertiary (`#D69E2E`):** Sync Amber. Applied to offline queues, pending data synchronization, and field warnings requiring user attention.
- **Neutral Background (`#F7FAFC`):** Soft, low-strain cool white used for the global viewport canvas.
- **Neutral Surface (`#FFFFFF`):** Base white allocated to interactive sheets, dialogs, and elevated cards.
- **Surface Container (`#EDF2F7`):** Cool light grey used for structural card backgrounds, segmented button tracks, and inactive chip states.
- **Outline (`#CBD5E0`):** Low-contrast architectural grey for borders on OutlinedCards, input perimeters, and list dividers.
- **Text Primary (`#1A202C`):** High-contrast deep slate for primary headlines, critical data values, and titles.
- **Text Secondary (`#4A5568`):** Muted slate for metadata, helper text, timestamps, and secondary captions.

## Typography

The typography scale utilizes **Roboto Flex** across all functional tiers, fulfilling the Material 3 typographic token specification while guaranteeing rendering consistency across diverse Android distributions.

### Token Application
- **Title Large (`22px / 28px / SemiBold`):** Screen headers in M3 TopAppBars, modal sheet titles, and high-level summary totals.
- **Title Medium (`16px / 24px / SemiBold`):** Merchant card names, section headers within forms, and list group indicators.
- **Body Large (`16px / 24px / Regular`):** Primary data entry, form inputs, customer addresses, and detailed notes.
- **Body Medium (`14px / 20px / Regular`):** Secondary information, sync logs, transaction histories, and dialog body content.
- **Label Large (`14px / 20px / SemiBold`):** Buttons (Filled, Tonal, Outlined), Extended FAB text, and action sheet options.
- **Label Medium (`12px / 16px / Medium`):** Chips, status badges (*"Tersinkronisasi"*), metadata stamps, and navigation bar item labels.

## Layout & Spacing

The layout is built around a standard 4-column mobile grid with fluid scaling on compact viewports (< 600dp).

### Architectural Rules
- **Canvas Margins:** Screen margins default strictly to `16px` (`margin`), maximizing the horizontal area for data cards and forms on standard mobile displays.
- **Grid Gutters:** Card lists and split-form inputs utilize `16px` (`gutter`) to ensure distinct visual separation without wasting horizontal density.
- **Component Padding Scale:**
  - `space-xs` (4px): Inner badge padding, micro-icon margins.
  - `space-sm` (8px): Chip vertical padding, spacing between stacked label and input pairs.
  - `space-md` (16px): Standard internal card padding, button horizontal padding, list item vertical gaps.
  - `space-lg` (24px): Form section grouping gaps, bottom sheet internal padding.
  - `space-xl` (32px): Terminal spacing before bottom action bars and sticky footers.
- **Field Ergonomics:** All touch targets maintain a minimum dimension of `48px` vertically to support high error tolerance during active outdoor usage.

## Elevation & Depth

This system adopts Material 3 tonal elevation combined with soft, low-contrast ambient shadows to express depth without visual clutter under bright daylight.

### Elevation Levels
- **Level 0 (Flat):** Surface Container (`#EDF2F7`) and canvas backgrounds. Zero shadow. Used for base views, inactive containers, and table headers.
- **Level 1 (Card Rest):** Used for OutlinedCards and standard list tiles. Features a subtle border (`1px solid #CBD5E0`) paired with an ambient shadow: `box-shadow: 0px 1px 3px rgba(26, 32, 44, 0.08), 0px 1px 2px rgba(26, 32, 44, 0.04)`.
- **Level 2 (Active Sheets / Tonal Buttons):** Surface (`#FFFFFF`) with `box-shadow: 0px 2px 6px rgba(26, 32, 44, 0.10), 0px 1px 4px rgba(26, 32, 44, 0.06)`.
- **Level 3 (Extended FAB & Bottom Nav):** Lifted control elements. Uses `box-shadow: 0px 4px 12px rgba(26, 54, 93, 0.15), 0px 2px 6px rgba(26, 32, 44, 0.08)` to introduce a primary-tinted ambient halo.
- **Level 4 (Modals & Bottom Sheets):** Full modal isolation with `box-shadow: 0px 8px 24px rgba(26, 32, 44, 0.18)` over a scrim overlay (`rgba(26, 32, 44, 0.40)`).

## Shapes

The shape system leverages Material 3's expressive corner architecture to establish tactile affordances across functional tiers.

### Corner Radius Allocation
- **Cards & Modal Containers (`rounded-2xl` / 16px):** OutlinedCards, route overview cards, and bottom sheet dialogs utilize generous 16px radii for an approachable, modern feel.
- **Form Controls & Input Fields (`rounded-xl` / 12px):** Text fields, search bars, and dropdown menus utilize 12px corners, keeping input boundaries structured yet soft.
- **Pill Controls (`rounded-full` / 9999px):** Buttons (Filled, Tonal), FilterChips, AssistChips, and Extended FABs use fully rounded ends to indicate immediate tactile interactivity.

## Components

### Buttons
- **FilledButton:** Primary call-to-action (e.g., *"Check-in Lokasi"*). Background `#1A365D`, text and icons `#FFFFFF`, shape `rounded-full`, height `48px`, padding `16px 24px`. Label Large font.
- **FilledTonalButton:** Secondary tier actions (e.g., *"Batal"*, *"Simpan Draf"*). Background `#EDF2F7`, text `#1A365D`, shape `rounded-full`, height `48px`, padding `16px 24px`.

### Extended Floating Action Button (FAB)
- Floats at bottom-right, `16px` offset from margins. Height `56px`, shape `rounded-full`, background `#1A365D`, text/icon `#FFFFFF`. Elevation Level 3. Label Large font with a `24px` leading icon (e.g., *"Mulai Kunjungan"*).

### Chips
- **FilterChip:** Used for list filtering (*"Semua"*, *"Tertunda"*, *"Selesai"*). Height `32px`, shape `rounded-full`. Inactive: background `#FFFFFF`, border `1px solid #CBD5E0`, text `#4A5568`. Selected: background `#EDF2F7`, border `1px solid #1A365D`, text `#1A365D`, with a leading checkmark.
- **AssistChip:** Action shortcuts (e.g., *"Peta Rute"*, *"Telepon"*). Height `32px`, shape `rounded-full`, background `#FFFFFF`, border `1px solid #CBD5E0`, text `#1A202C`, leading icon `18px`.

### Cards
- **OutlinedCard:** Merchant route items and inventory metrics. Background `#FFFFFF`, border `1px solid #CBD5E0`, corner radius `16px` (`rounded-2xl`), padding `16px`. Elevation Level 1.

### Input Fields
- Container height `56px`, shape `12px` (`rounded-xl`), background `#FFFFFF`, border `1px solid #CBD5E0`.
- Label placed inside upper bounds on focus. Active/Focus border shifts to `2px solid #1A365D`.
- Helper and validation text formatted with Body Medium in Text Secondary (`#4A5568`).

### Checkboxes & Radios
- Size `24px x 24px`. Selected state filled with `#1A365D` featuring white check or radio dot. Unselected state bordered with `2px solid #CBD5E0`.

### Lists
- Multi-line merchant list tiles feature a fixed minimum height of `72px`, separated by hairline dividers (`1px solid #EDF2F7`). Includes Title Medium for headers, Body Medium for address strings, and trailing status badges.

### TopAppBar (M3)
- Height `64px`, background `#FFFFFF`, surface tint `#1A365D`. Title Large (`#1A202C`). Left icon hosts navigation drawer/back chevron; right actions house synchronization queue status indicators (`#D69E2E` for pending items).