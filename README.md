# s8113459Assignment2 — NIT3213 Final Assignment

An Android app (Kotlin, MVVM, Hilt) for **NIT3213 Mobile Application Development**.
It authenticates a student against the `nit3213api`, loads the entities for the returned
keypass, and shows each entity in detail.

| | |
|---|---|
| **Student** | Duc Long Tran (s8113459) |
| **Campus / auth endpoint** | Sydney — `POST /sydney/auth` |
| **API base URL** | `https://nit3213apinew.onrender.com/` |

---

## Features

| Screen | What it does |
|---|---|
| **Login** | Student ID (username) + first name (password, case sensitive). Validates input before calling the API, shows a progress indicator, and displays clear errors (wrong credentials, unknown student ID, no internet, server error). A leading `s` in the ID is removed automatically. |
| **Dashboard** | Uses the `keypass` from login to call `GET /dashboard/{keypass}` and shows the entities in a **RecyclerView** (`ListAdapter` + `DiffUtil`). Each card shows a summary of the entity **without the description**. Has loading, error + retry, empty states and pull-to-refresh. Tapping a card opens Details. |
| **Details** | Shows **every property** of the selected entity as label/value rows plus the full **description**. |

The API returns different property names for every topic. For this student the keypass is **`mythology`**
(entities with `name`, `culture`, `domain`, `symbol`, `parentage`, `romanEquivalent`, `description`), but the app
reads entity properties **dynamically** and turns keys into readable labels (`artistName` → *Artist Name*).
It works for any keypass without code changes.

---

## Architecture

The project follows **MVVM** with a small **clean-architecture** split into three layers:

```
UI (Activities + ViewModels)  ──►  Domain (models + repository interfaces)  ◄──  Data (Retrofit + repository implementations)
```

* **UI layer** – Activities only render state and forward user actions. Each screen has a
  `@HiltViewModel` exposing a `StateFlow` of UI state; one-off navigation uses a `Channel`.
* **Domain layer** – plain Kotlin models (`Entity`, `Dashboard`, `DataError`, `AppResult`) and the
  `AuthRepository` / `DashboardRepository` interfaces. No Android or networking code.
* **Data layer** – `ApiService` (Retrofit), DTOs, `EntityMapper` (JSON → domain) and repository
  implementations. `safeApiCall` turns HTTP codes and exceptions into `DataError`s in one place.

### Dependency injection (Hilt)

| Module | Provides |
|---|---|
| `AppConfigModule` | `@BaseUrl` and `@AuthLocation` strings (from `BuildConfig`) and the `@IoDispatcher` |
| `NetworkModule` | singleton `OkHttpClient`, `Retrofit` and `ApiService` |
| `RepositoryModule` | `@Binds` `AuthRepositoryImpl` → `AuthRepository`, `DashboardRepositoryImpl` → `DashboardRepository` |

* `App` is annotated with `@HiltAndroidApp`; every Activity is an `@AndroidEntryPoint`.
* ViewModels receive repositories through **constructor injection** and depend only on the interfaces,
  which is what makes them easy to unit test with fakes/mocks.
* Navigation arguments (keypass, selected entity) reach the ViewModels through Hilt-provided `SavedStateHandle`.
* Custom **qualifiers** (`@IoDispatcher`, `@BaseUrl`, `@AuthLocation`) keep configuration out of the classes that use it.

### Project structure

```
app/src/main/java/com/s8113459/assignment2/
├── App.kt                         # @HiltAndroidApp
├── di/                            # Hilt modules + qualifiers
├── domain/
│   ├── model/                     # Entity, EntityField, Dashboard, DataError, AppResult
│   └── repository/                # AuthRepository, DashboardRepository (interfaces)
├── data/
│   ├── remote/                    # ApiService + dto/
│   ├── mapper/                    # EntityMapper
│   └── repository/                # *RepositoryImpl + safeApiCall
└── ui/
    ├── common/                    # formatters, error-message mapping, view helpers
    ├── login/                     # LoginActivity, LoginViewModel, LoginUiState
    ├── dashboard/                 # DashboardActivity, DashboardViewModel, EntityAdapter
    └── details/                   # DetailsActivity, DetailsViewModel
app/src/test/java/...              # unit tests (see below)
```

---

## Tech stack

Kotlin 1.9.24 · Android Gradle Plugin 8.5.2 · Gradle 8.7 · minSdk 24 / target & compile SDK 34

| Purpose | Library |
|---|---|
| DI | Hilt 2.51.1 (KSP) |
| Networking | Retrofit 2.11 + Gson converter, OkHttp 4.12 (logging interceptor, debug only) |
| Async | Kotlin Coroutines 1.8 + StateFlow |
| UI | Material Components 1.12 (Material 3, light + dark theme), ViewBinding, RecyclerView, SwipeRefreshLayout |
| Tests | JUnit 4, MockK, kotlinx-coroutines-test, OkHttp MockWebServer |

All versions are declared in `gradle/libs.versions.toml` (Gradle version catalog).

---

## Build and run

### Requirements
* **Android Studio** Koala (2024.1) or newer
* **JDK 17 or 21** for Gradle. Gradle 8.7 does **not** run on JDK 22+.
  If Android Studio shows *"Please select Gradle JVM… incompatible with JVM 25"*, click **Use JVM 21**
  (or *Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK → 21*).
* An emulator or device running **Android 7.0 (API 24)** or newer, with internet access.

### Steps
1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```
2. In Android Studio choose **File → Open** and select the `s8113459Assignment2` folder (the one containing `settings.gradle.kts`).
3. Wait for **Gradle sync** to finish (dependencies are downloaded on first sync).
4. Select the **app** run configuration and a device, then press **Run ▶**.

Command line alternative:
```bash
./gradlew assembleDebug        # build the APK  (Windows: gradlew.bat assembleDebug)
./gradlew installDebug         # install on a connected device / emulator
```

### Logging in
* **Username:** your student ID **without** the `s` (e.g. `12345678`)
* **Password:** your first name exactly as registered — it is **case sensitive**

> The API runs on a free Render instance. The **first request can take up to a minute** while the
> server wakes up; the app uses 60-second timeouts and tells the user to wait.

### Changing the campus
The auth location is a build config value in `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "AUTH_LOCATION", "\"sydney\"")   // "footscray", "sydney" or "br"
```
It is injected through Hilt (`@AuthLocation`), so no other code needs to change.

---

## Unit tests

Run all tests:
```bash
./gradlew test               # Windows: gradlew.bat test
```
or in Android Studio right-click `app/src/test` → **Run 'Tests in …'**.

| Test class | What it covers |
|---|---|
| `LoginViewModelTest` | input validation, `s`-prefix/whitespace normalisation, case-sensitive password, loading state, double-tap protection, success navigation event, error states, clearing errors |
| `DashboardViewModelTest` | loads with the keypass from `SavedStateHandle`, loading → success, error state, retry, pull-to-refresh keeps the list, missing argument |
| `DetailsViewModelTest` | exposes all fields and description, null description, title fallback, missing argument |
| `RepositoryTest` | real Retrofit + Gson against **MockWebServer**: request method/path/body, keypass parsing, entity mapping (including the real `mythology` payload), HTTP 400/404/5xx and dropped-connection error mapping |
| `EntityMapperTest` | dynamic properties, description extraction (case-insensitive), numbers/arrays/nulls, totals |
| `FormattersTest` | key → label formatting |

ViewModels are tested with **MockK** fakes of the repository interfaces and a `MainDispatcherRule`
that swaps `Dispatchers.Main` for a test dispatcher.

---

## Troubleshooting

| Problem | Fix |
|---|---|
| *"Incorrect student ID or first name"* | Check the first name's capitalisation; the ID must have no `s`. |
| *"This student ID was not found for this campus"* | The ID is not registered for the campus in `AUTH_LOCATION`. |
| First login is slow / times out | The Render server is waking up — wait and press **Sign in** again. |
| Gradle sync fails with a JVM version error | Set Gradle JDK to 17 or 21 (see Requirements). |
