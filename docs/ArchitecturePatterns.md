# Architecture Patterns & Best Practices

This document outlines the architectural patterns and best practices used in this KMP project.

---

## Table of Contents

1. [ViewModel State Management](#viewmodel-state-management)
2. [Reactive Validation Pattern](#reactive-validation-pattern)
3. [Error Handling & Repository Layer](#error-handling--repository-layer)
4. [Toast System](#toast-system)
5. [Navigation](#navigation)
6. [Clean Architecture Layers](#clean-architecture-layers)

---

## ViewModel State Management

### State Flow Pattern

```kotlin
class MyViewModel : ViewModel() {
    // ✅ Private mutable state - source of truth
    private val _state = MutableStateFlow(MyState())

    // ✅ Public read-only state with transformations
    val state = _state.asStateFlow()
        .onStart {
            // Initial setup, start observers
            observeData()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5.seconds),
            _state.value
        )
}
```

### When to Use `_state.value` vs `state.value`

**✅ Inside ViewModel - Use `_state.value`:**
```kotlin
fun onAction(action: MyAction) {
    val currentState = _state.value  // ✅ Direct source of truth

    if (!currentState.isValid) return

    _state.update { old ->
        old.copy(data = newData)
    }
}
```

**✅ Outside ViewModel - Collect `state` Flow:**
```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()  // ✅ Reactive

    // Use state here
}
```

**❌ Anti-patterns:**
```kotlin
// ❌ Don't use state.value inside ViewModel
val currentState = state.value

// ❌ Don't read .value in composables (breaks reactivity)
Text(viewModel.state.value.message)
```

### State Updates

```kotlin
// ✅ Use update for thread-safe modifications
_state.update { old ->
    old.copy(count = old.count + 1)
}

// ❌ Don't mutate directly (race conditions)
_state.value = _state.value.copy(count = _state.value.count + 1)
```

---

## Reactive Validation Pattern

### Pattern: Separate Validation Observer

Instead of validating on every state change, observe only relevant fields:

```kotlin
class FormViewModel : ViewModel() {
    private val _state = MutableStateFlow(FormState())

    val state = _state.asStateFlow()
        .onStart {
            observeData()
            observeFormForValidation()  // ✅ Separate validation observer
        }
        .stateIn(...)

    @OptIn(FlowPreview::class)
    private fun observeFormForValidation() {
        state
            .map { it.formField1 to it.formField2 }  // ✅ Extract only relevant fields
            .distinctUntilChanged()                   // ✅ Only emit when fields change
            .debounce(300.milliseconds)               // ✅ Debounce user input
            .onEach { (field1, field2) ->
                validateForm(field1, field2)
            }
            .launchIn(viewModelScope)
    }

    private fun validateForm(field1: String?, field2: String?) {
        val errors = mutableListOf<String>()

        if (field1?.isBlank() == true) {
            errors.add("Field 1 cannot be empty")
        }

        if (field2?.isBlank() == true) {
            errors.add("Field 2 cannot be empty")
        }

        val isValid = errors.isEmpty()

        _state.update { old ->
            old.copy(
                field1Errors = errors,
                isFormValid = isValid,
            )
        }
    }
}
```

### Benefits

- ✅ Validates only when form fields actually change
- ✅ Debounces to avoid validating on every keystroke
- ✅ No infinite loops (doesn't react to `isFormValid` changes)
- ✅ ~90% fewer validation calls during typing

### Guard Clause in Save Action

Always add a guard clause even if UI button is disabled:

```kotlin
fun onSave() {
    val currentState = _state.value

    // ✅ Guard clause for safety
    if (!currentState.isFormValid) {
        Log.w(TAG, "Form is invalid, not saving")
        return
    }

    // Save logic...
}
```

**Why:** Button could be triggered programmatically, via accessibility, or during race conditions.

---

## Error Handling & Repository Layer

### Repository Pattern: Catch Errors, Return Success/Failure

**✅ Repository handles all data layer exceptions:**

```kotlin
class MyRepositoryImpl(
    private val dao: MyDao,
) : MyRepository {
    companion object {
        const val TAG = "MyRepository"
    }

    override suspend fun saveData(data: Data): Boolean {
        return try {
            dao.insert(data)
            true
        } catch (e: Exception) {
            Log.tag(TAG).e(e) { "Failed to save data" }
            false
        }
    }

    override suspend fun getData(): Data? {
        return try {
            dao.get()
        } catch (e: Exception) {
            Log.tag(TAG).e(e) { "Failed to get data" }
            null
        }
    }

    override fun getDataFlow(): Flow<Data> {
        return try {
            dao.getFlow()
        } catch (e: Exception) {
            Log.tag(TAG).e(e) { "Failed to get data flow" }
            flowOf(Data.Empty)
        }
    }
}
```

**✅ ViewModel handles business logic based on result:**

```kotlin
class MyViewModel(
    private val repository: MyRepository,
    private val toastService: ToastService,
) : ViewModel() {

    fun saveData() {
        viewModelScope.launch {
            val result = repository.saveData(data)

            if (result) {
                toastService.showSuccess("Data saved successfully")
            } else {
                toastService.showError("Failed to save data")
            }
        }
    }
}
```

### Benefits

- ✅ Repository encapsulates all data layer concerns
- ✅ ViewModel doesn't need to know about database exceptions
- ✅ Clean separation of concerns
- ✅ Consistent error handling

### Anti-patterns

```kotlin
// ❌ Don't let exceptions leak to ViewModel
override suspend fun saveData(data: Data): Boolean {
    dao.insert(data)  // Could throw exception!
    return true
}

// ❌ Don't handle database exceptions in ViewModel
fun saveData() {
    viewModelScope.launch {
        try {
            repository.saveData(data)  // Repository should handle this
        } catch (e: SQLiteException) {
            // ViewModel shouldn't know about database exceptions
        }
    }
}
```

---

## Toast System

### Architecture

- **ToastService**: Singleton with SharedFlow for events
- **ToastHost**: Composable that collects and displays toasts
- **ToastDefaults**: Material3-style defaults for theming

### Usage in ViewModels

```kotlin
class MyViewModel(
    private val toastService: ToastService,
) : ViewModel() {

    fun performAction() {
        viewModelScope.launch {
            val result = repository.doSomething()

            if (result) {
                toastService.showSuccess("Action completed")
            } else {
                toastService.showError("Action failed")
            }
        }
    }
}
```

### Toast Types

```kotlin
// Success (green)
toastService.showSuccess("Settings saved successfully")

// Error (red)
toastService.showError("Failed to save settings")

// Warning (orange)
toastService.showWarning("Please check your input")

// Info (blue)
toastService.showInfo("New features available")

// Custom
toastService.showToast(
    ToastMessage(
        message = "Custom message",
        duration = 5.seconds,
        type = ToastType.SUCCESS
    )
)
```

### Customizing Toast Theme

In App.kt:

```kotlin
ToastHost(
    colors = ToastDefaults.colors(
        successContainerColor = Color(0xFF4CAF50),
        successContentColor = Color.White,
        errorContainerColor = Color(0xFFE53935),
        errorContentColor = Color.White,
    ),
    shape = RoundedCornerShape(12.dp)
)
```

### Benefits

- ✅ Centralized toast management
- ✅ No duplicate toasts stacking
- ✅ SharedFlow ensures proper event consumption
- ✅ Material3 theming support
- ✅ Easy to use from ViewModels

---

## Navigation

### NavigationService Pattern

```kotlin
class NavigationService {
    private val _navigationCommands = MutableSharedFlow<NavigationCommand>(
        extraBufferCapacity = 1
    )
    val navigationCommands = _navigationCommands.asSharedFlow()

    fun navigate(route: Route) {
        _navigationCommands.tryEmit(NavigationCommand.Navigate(route))
    }

    fun back() {
        _navigationCommands.tryEmit(NavigationCommand.Back)
    }
}
```

### Usage in ViewModels

```kotlin
class MyViewModel(
    private val nav: NavigationService,
) : ViewModel() {

    fun onBackClicked() {
        nav.back()
    }

    fun navigateToSettings() {
        nav.navigate(Route.Settings)
    }
}
```

### Benefits

- ✅ Type-safe navigation
- ✅ Testable (can mock NavigationService)
- ✅ Decoupled from Compose navigation
- ✅ Centralized navigation logic

---

## Clean Architecture Layers

### Layer Structure

```
📁 feature/
  📁 data/
    📁 database/
      - FeatureEntity.kt
      - FeatureDao.kt
      - FeatureDatabase.kt
    📁 mapper/
      - FeatureMapper.kt
    - FeatureRepositoryImpl.kt
  📁 domain/
    📁 model/
      - Feature.kt
    📁 repository/
      - FeatureRepository.kt (interface)
  📁 presentation/
    📁 composable/
      - FeatureScreen.kt
    📁 mapper/
      - FeatureMapper.kt
    - FeatureAction.kt
    - FeatureState.kt
    - FeatureViewModel.kt
```

### Layer Responsibilities

**Data Layer:**
- Database entities and DAOs
- Repository implementations
- API clients
- Data source abstractions
- Error handling for data operations

**Domain Layer:**
- Business models
- Repository interfaces
- Use cases (if needed)
- Business logic

**Presentation Layer:**
- ViewModels
- UI State
- UI Actions
- Composables
- Presentation mappers

### Dependency Rules

```
Presentation → Domain → Data
     ↓           ↓        ↓
   ViewModel   Model   Entity
```

- ✅ Presentation depends on Domain
- ✅ Data depends on Domain
- ❌ Domain NEVER depends on Data or Presentation
- ❌ Data NEVER depends on Presentation

### Example: Feature Implementation

**1. Domain Model:**
```kotlin
// domain/model/Setting.kt
data class Settings(
    val settings: List<Setting>,
) {
    data class Setting(
        val key: String,
        val value: String,
    )
}
```

**2. Repository Interface:**
```kotlin
// domain/repository/SettingsRepository.kt
interface SettingsRepository {
    suspend fun getSettings(): Settings
    fun getSettingsFlow(): Flow<Settings>
    suspend fun upsertSettings(settings: Settings): Boolean
    suspend fun deleteSetting(setting: Settings.Setting): Boolean
}
```

**3. Data Entity:**
```kotlin
// data/database/SettingsEntity.kt
@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val key: String,
    val value: String,
)
```

**4. Repository Implementation:**
```kotlin
// data/SettingsRepositoryImpl.kt
class SettingsRepositoryImpl(
    private val dao: SettingsDao,
) : SettingsRepository {
    override suspend fun upsertSettings(settings: Settings): Boolean {
        return try {
            dao.upsert(*settings.toEntities().toTypedArray())
            true
        } catch (e: Exception) {
            Log.e(e) { "Failed to upsert settings" }
            false
        }
    }
}
```

**5. Presentation State:**
```kotlin
// presentation/SettingsState.kt
data class SettingsState(
    val settingsForm: List<SettingForm>? = null,
    val newSettingForm: NewForm = NewForm(),
    val isFormValid: Boolean = true,
)
```

**6. ViewModel:**
```kotlin
// presentation/SettingsViewModel.kt
class SettingsViewModel(
    private val repository: SettingsRepository,
    private val nav: NavigationService,
    private val toastService: ToastService,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow().stateIn(...)

    fun onAction(action: SettingsAction) {
        // Handle actions
    }
}
```

---

## Naming Conventions

### State Properties

```kotlin
data class MyState(
    // ✅ Use descriptive names
    val settingsForm: List<SettingForm>? = null,
    val newSettingForm: NewForm = NewForm(),
    val isFormValid: Boolean = true,
    val isLoading: Boolean = false,

    // ❌ Avoid abbreviations
    val settings: List<Setting>? = null,  // Too generic
    val form: Form = Form(),              // What form?
    val valid: Boolean = true,            // Valid what?
)
```

### Actions

```kotlin
sealed interface MyAction {
    // ✅ Use verb phrases
    data object OnBackClicked : MyAction
    data object OnSaveSettings : MyAction
    data class OnValueChanged(val value: String) : MyAction

    // ❌ Avoid nouns
    data object Back : MyAction           // Not clear it's an action
    data object Save : MyAction           // What are we saving?
}
```

### Repositories

```kotlin
// ✅ Interface in domain
interface SettingsRepository

// ✅ Implementation in data with Impl suffix
class SettingsRepositoryImpl : SettingsRepository
```

---

## Dependency Injection with Koin

### Module Organization

```kotlin
val dataModule = module {
    // Databases
    single<MyDatabase> {
        get<DatabaseFactory>()
            .create<MyDatabase>(dbname = MyDatabase.DB_NAME)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    // DAOs
    single<MyDao> { get<MyDatabase>().myDao }

    // Repositories
    singleOf(::MyRepositoryImpl) bind MyRepository::class
}

val serviceModule = module {
    singleOf(::NavigationService)
    singleOf(::ToastService)
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
}
```

### Injection in ViewModels

```kotlin
class MyViewModel(
    private val repository: MyRepository,      // Domain interface
    private val nav: NavigationService,        // Service
    private val toastService: ToastService,    // Service
) : ViewModel()
```

---

## Testing Patterns

### ViewModel Testing

```kotlin
class MyViewModelTest {
    @Test
    fun `when save action, should call repository`() = runTest {
        // Given
        val repository = FakeMyRepository()
        val toastService = FakeToastService()
        val viewModel = MyViewModel(repository, toastService)

        // When
        viewModel.onAction(MyAction.OnSave)

        // Then
        assertEquals(1, repository.saveCallCount)
        assertTrue(toastService.lastToast is Success)
    }
}
```

### Repository Testing

```kotlin
class MyRepositoryTest {
    @Test
    fun `when database throws exception, should return false`() = runTest {
        // Given
        val dao = object : MyDao {
            override suspend fun insert(entity: MyEntity) {
                throw SQLiteException("DB error")
            }
        }
        val repository = MyRepositoryImpl(dao)

        // When
        val result = repository.save(data)

        // Then
        assertFalse(result)
    }
}
```

---

## Quick Reference Checklist

### When Creating a New Feature

- [ ] Create domain model
- [ ] Create repository interface in domain
- [ ] Create data entity
- [ ] Create data mapper
- [ ] Implement repository with error handling
- [ ] Create presentation state
- [ ] Create presentation actions
- [ ] Create ViewModel with:
  - [ ] Private `_state` MutableStateFlow
  - [ ] Public `state` with `stateIn()`
  - [ ] Separate validation observer if needed
  - [ ] Guard clauses in save/submit actions
- [ ] Create composable screen
- [ ] Add to Koin modules
- [ ] Add to navigation graph

### Before Committing

- [ ] Used `_state.value` inside ViewModel
- [ ] Repository catches all exceptions
- [ ] ViewModels use ToastService for feedback
- [ ] Validation is reactive and debounced
- [ ] Guard clauses in actions that depend on state validity
- [ ] Proper logging with tags
- [ ] No hardcoded strings (use StringValue)
- [ ] Clean architecture layers respected

---

## Common Anti-patterns to Avoid

```kotlin
// ❌ Using state.value inside ViewModel
val currentState = state.value

// ❌ Not handling repository errors
override suspend fun save(data: Data) {
    dao.insert(data)  // Could throw!
}

// ❌ Validating on every state change
val state = _state.asStateFlow()
    .onEach { validateForm() }  // Too frequent!

// ❌ No guard clause before save
fun onSave() {
    repository.save(data)  // What if form is invalid?
}

// ❌ Handling database exceptions in ViewModel
try {
    repository.save(data)
} catch (e: SQLiteException) {
    // Wrong layer!
}

// ❌ Not using update for state changes
_state.value = _state.value.copy(...)  // Race condition!

// ❌ Domain depending on data layer
class MyUseCase(
    private val dao: MyDao  // ❌ Should depend on repository interface
)
```

---

## Further Reading

- [Kotlin Flow Documentation](https://kotlinlang.org/docs/flow.html)
- [StateFlow and SharedFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Material3 Design Patterns](https://m3.material.io/)
