
---

## 📂 Layers

### 1. Data Layer (`data/`)
Handles persistence and data sources.

- **dao/**
    - `QrDataDao.kt` → Database operations (insert, query, delete).
- **database/**
    - `QrDatabase.kt` → Room database configuration.
- **entity/**
    - `QrDataEntity.kt` → Database entity definition.
- **mapper/**
    - `QrDataMapper.kt` → Maps DB entity ↔ domain model.
- **repository/**
    - `QrRepositoryImpl.kt` → Implements `QrRepository`, bridging DB + framework.

👉 **Role**: Abstracts storage details and provides a clean API for accessing QR history/favorites.

---

### 2. Domain Layer (`domain/`)
Contains **business logic** and **contracts**, pure Kotlin (no Android dependencies).

- **model/**
    - `QrData.kt` → Core domain model.
- **repository/**
    - `QrRepository.kt` → Abstraction for accessing QR data.
- **validator/**
    - `QrDataValidator.kt` → Validates input (e.g., links, Wi-Fi configs).

👉 **Role**: Defines *what* the app does (rules, contracts) without knowing *how* it’s implemented.

---

### 3. Framework Layer (`framework/`)
Bridges the app with **Android APIs** and external libraries.

- **bitmap/**
    - `BitmapDecoder.kt`, `BitmapDecoderFactory.kt`, `LatestBitmapDecoder.kt`, `LegacyBitmapDecoder.kt`
    - Handles QR bitmap decoding for different Android versions.
- **camera/**
    - `QrAnalyzer.kt` → CameraX QR analyzer.
    - `QrCameraController.kt` → Manages camera lifecycle & preview.
- **image/**
    - `ImageSaver.kt`, `ImageSaverFactory.kt`, `LegacyImageSaver.kt`, `MediaStoreImageSaver.kt`
    - Saves QR codes as images (supports Android 10+ via MediaStore).
- **qr/**
    - `QrGenerator.kt` → QR code generation using ZXing.

👉 **Role**: Encapsulates Android-specific code, keeping domain layer pure.

---

### 4. Presentation Layer (`presentation/`)
Implements the **UI** using Jetpack Compose + MVVM.

- **create/**
    - `ChooseQrTypeScreen.kt`, `CreateQrScreen.kt`, `CreateQrViewModel.kt`
    - Flow for creating custom QR codes.
- **design_system/**
    - `Dimen.kt` → Shared dimensions and styles.
- **history/**
    - `QrHistoryScreen.kt`, `QrHistoryViewModel.kt`
    - Displays scanned/generated history & favorites.
- **mapper/**
    - `QrUiModelDataParser.kt`, `QrUiModelMapper.kt`
    - Converts domain models → UI models.
- **route/**
    - `Screen.kt` → Defines navigation routes.
- **scan_details/**
    - `QrDetailsScreen.kt`, `QrDetailViewModel.kt`
- **scanner/**
    - `QrScannerScreen.kt`, `QrScannerViewModel.kt`
- **splash/**
    - `SplashScreen.kt`
- **state/**
    - `QrDataUiModel.kt`, `QrTypeItem.kt`

👉 **Role**: Displays state, handles user actions, interacts with ViewModels.

---

### 5. Dependency Injection (`di/`)
- **`AppModules.kt`** → Provides dependencies (repositories, factories, database, etc.).

---

## 🔄 Data Flow (MVVM + Clean Architecture)

1. **UI Layer** (Compose screens)  
   → User interacts (e.g., scan QR).  
   → Calls **ViewModel**.

2. **ViewModel**  
   → Holds UI state.  
   → Calls **Repository**.

3. **Domain Layer**  
   → Repository interface defines contract.  
   → Validator ensures valid data.

4. **Data Layer**  
   → RepositoryImpl fetches data via DAO / framework.  
   → Returns `QrData` domain model.

5. **UI Mapping**  
   → Domain model → UI model.  
   → ViewModel updates state.  
   → Compose recomposes UI.
