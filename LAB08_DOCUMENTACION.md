# LAB08 — App de Productos con Hilt

## Objetivo

Construir una aplicación Android con Jetpack Compose que muestre productos usando **arquitectura por capas limpia**, integrando todos los requisitos del Lab08:

- **ViewModel** — gestión de estado reactivo
- **UseCase** — capa de dominio explícita, una clase por operación
- **Repository (interfaz)** — contrato desacoplado del origen de datos
- **Implementación Real y Fake** — ambas implementan la misma interfaz
- **Inyección de dependencias con Hilt** — sin factories manuales
- **StateFlow para UI reactiva** — la UI observa el estado, no lo pide

Además incorpora **todas las funcionalidades del Lab04**: 8 pantallas, CRUD completo de productos y categorías, carrito, favoritos, búsqueda, filtros y sistema de temas dinámicos.

---

## Estructura del Proyecto

```
app/src/main/java/com/example/lab08/
│
├── LAB08Application.kt           ← @HiltAndroidApp
├── MainActivity.kt               ← @AndroidEntryPoint + NavHost
│
├── di/
│   └── AppModule.kt              ← @Module Hilt: bind interface → impl
│
├── domain/                       ← Capa de Dominio (pura Kotlin, sin Android)
│   ├── model/
│   │   └── Product.kt            ← data class Product, Category
│   ├── repository/
│   │   └── ProductRepository.kt  ← interface (contrato)
│   └── usecase/
│       ├── GetProductsUseCase.kt
│       ├── GetCategoriesUseCase.kt
│       ├── GetProductByIdUseCase.kt
│       ├── AddProductUseCase.kt
│       ├── UpdateProductUseCase.kt
│       ├── DeleteProductUseCase.kt
│       ├── AddCategoryUseCase.kt
│       └── DeleteCategoryUseCase.kt
│
├── data/                         ← Capa de Datos
│   ├── MockData.kt               ← Seed data (9 productos, 4 categorías)
│   └── repository/
│       ├── RealProductRepository.kt  ← implementación principal (in-memory)
│       └── FakeProductRepository.kt  ← implementación mínima para tests/demos
│
└── ui/                           ← Capa de Presentación
    ├── components/
    │   ├── AppButton.kt
    │   ├── CategoryCard.kt
    │   ├── ProductCard.kt
    │   ├── ProductSearchBar.kt
    │   └── ThemeSelectorSheet.kt
    ├── screens/
    │   ├── HomeScreen.kt
    │   ├── DetailsScreen.kt
    │   ├── CartScreen.kt
    │   ├── FavoritesScreen.kt
    │   ├── CategoriesScreen.kt
    │   ├── AddProductScreen.kt
    │   ├── EditProductScreen.kt
    │   └── AddCategoryScreen.kt
    ├── state/
    │   └── ProductUiState.kt     ← sealed class: Loading / Success / Error
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt              ← 4 temas × 2 modos = 8 esquemas de color
    │   └── Type.kt
    └── viewmodel/
        └── StoreViewModel.kt     ← @HiltViewModel, inyecta todos los UseCases
```

---

## Capa de Dominio

### Modelos (`domain/model/Product.kt`)

```kotlin
data class Product(
    val id: Int,
    val name: String,
    val categoryId: Int,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isFavorite: Boolean = false
)

data class Category(val id: Int, val name: String, val icon: String)
```

Clases de datos puras. Sin dependencias de Android.

### Repository Interface (`domain/repository/ProductRepository.kt`)

```kotlin
interface ProductRepository {
    fun getProducts(): List<Product>
    fun getCategories(): List<Category>
    fun getProductById(id: Int): Product?
    fun addProduct(product: Product): Product
    fun updateProduct(product: Product): Boolean
    fun deleteProduct(id: Int): Boolean
    fun addCategory(category: Category): Category
    fun deleteCategory(id: Int): Boolean
}
```

**Por qué es una interfaz:** desacopla el dominio del origen de datos. El ViewModel no sabe si los datos vienen de una API, Room, o datos en memoria. Hilt decide qué implementación inyectar.

### UseCases (`domain/usecase/`)

Cada operación de negocio tiene su propia clase con `operator fun invoke()`:

```kotlin
class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): List<Product> = repository.getProducts()
}
```

**¿Por qué UseCases si la lógica parece simple?**  
En este lab la lógica es 1:1 con el repositorio. Pero la estructura es escalable: si en el futuro `GetProductsUseCase` necesita ordenar, paginar, filtrar duplicados o combinar fuentes, la lógica va aquí — NO en el ViewModel ni en el repositorio.

Cada UseCase recibe `@Inject constructor` porque Hilt los construye automáticamente.

---

## Capa de Datos

### RealProductRepository

```kotlin
@Singleton
class RealProductRepository @Inject constructor() : ProductRepository {
    private val products = MockData.products.toMutableList()
    private val categories = MockData.categories.toMutableList()
    // ...
}
```

- `@Singleton` garantiza una sola instancia en toda la app
- `@Inject constructor()` permite que Hilt lo construya sin parámetros manuales
- En producción se reemplazaría por Retrofit + Room

### FakeProductRepository

```kotlin
@Singleton
class FakeProductRepository @Inject constructor() : ProductRepository {
    // Dataset mínimo hardcodeado
}
```

Misma interfaz, datos de prueba mínimos. Útil para:
- Unit tests del ViewModel sin tocar datos reales
- UI previews de Compose
- Demos rápidas

**Para cambiar de Real a Fake**, solo se modifica `AppModule.kt`:

```kotlin
// De:
@Binds abstract fun bindProductRepository(impl: RealProductRepository): ProductRepository

// A:
@Binds abstract fun bindProductRepository(impl: FakeProductRepository): ProductRepository
```

Sin tocar el ViewModel, los UseCases ni ninguna pantalla.

---

## Inyección de Dependencias con Hilt

### Setup

| Archivo | Rol |
|---|---|
| `LAB08Application.kt` | `@HiltAndroidApp` — arranca el grafo de dependencias |
| `AndroidManifest.xml` | `android:name=".LAB08Application"` — registra la Application class |
| `MainActivity.kt` | `@AndroidEntryPoint` — habilita inyección en la Activity |
| `di/AppModule.kt` | `@Module @InstallIn(SingletonComponent)` — declara bindings |
| `StoreViewModel.kt` | `@HiltViewModel` — el ViewModel es construido por Hilt |

### Flujo de resolución

```
Hilt graph
│
├── ProductRepository ──bind──► RealProductRepository (@Singleton)
│
├── GetProductsUseCase ──inject──► ProductRepository (resolved above)
├── AddProductUseCase  ──inject──► ProductRepository
├── ... (todos los UseCases)
│
└── StoreViewModel (@HiltViewModel)
    └── inject──► GetProductsUseCase
    └── inject──► GetCategoriesUseCase
    └── inject──► AddProductUseCase
    └── inject──► UpdateProductUseCase
    └── inject──► DeleteProductUseCase
    └── inject──► AddCategoryUseCase
    └── inject──► DeleteCategoryUseCase
```

En `MainActivity`, el ViewModel se obtiene con:

```kotlin
val viewModel: StoreViewModel = hiltViewModel()
```

Sin factories manuales. Hilt resuelve toda la cadena automáticamente.

---

## ViewModel (`StoreViewModel.kt`)

```kotlin
@HiltViewModel
class StoreViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val addProductUseCase: AddProductUseCase,
    // ...
) : ViewModel() {
```

### Estado manejado

| Estado | Tipo | Propósito |
|---|---|---|
| `uiState` | `StateFlow<ProductUiState>` | Loading / Success / Error para la lista principal |
| `searchQuery` | `StateFlow<String>` | Búsqueda en tiempo real |
| `selectedCategoryId` | `StateFlow<Int?>` | Filtro de categoría activo |
| `favoriteIds` | `SnapshotStateList<Int>` | IDs de productos favoritos |
| `cartItems` | `SnapshotStateList<Product>` | Productos en el carrito |
| `currentTheme` | `mutableStateOf(AppThemeMode)` | Tema de color activo |
| `isDarkMode` | `mutableStateOf(Boolean)` | Modo oscuro |

### ¿Por qué StateFlow + SnapshotStateList juntos?

- `StateFlow` es ideal para estado derivado de operaciones asincrónicas o que necesita ser observado desde múltiples composables.
- `SnapshotStateList` es más eficiente para listas que se modifican frecuentemente (carrito, favoritos) porque Compose detecta cambios exactos sin recrear toda la lista.

---

## ProductUiState (sealed class)

```kotlin
sealed class ProductUiState {
    data object Loading : ProductUiState()
    data class Success(val products: List<Product>, val categories: List<Category>) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}
```

Las pantallas hacen `when (uiState)` para manejar cada caso explícitamente. El compilador fuerza el exhaustive check.

---

## Navegación

Definida en `MainActivity.kt` con Jetpack Navigation Compose:

```
Home  ←→  Details  ←→  EditProduct
 │                          │
 └── AddProduct          (delete)
 
Favorites ←→ Details

Categories ←→ AddCategory

Cart
```

La barra inferior muestra: Home · Favorites · Cart · Categories · Theme

Las pantallas de detalle/formulario **ocultan** la barra inferior automáticamente detectando la ruta actual.

```kotlin
if (currentRoute in BOTTOM_NAV_ROUTES) { NavigationBar { ... } }
```

---

## Sistema de Temas

4 temas de color × 2 modos (light/dark) = 8 esquemas de color:

| Tema | Color primario | Peculiaridad |
|---|---|---|
| Modern Blue | `#1565C0` | Default |
| Green | `#2E7D32` | — |
| Purple | `#6A1B9A` | — |
| Orange | `#E65100` | Activa `OrangeTypography` (Serif) |

El tema se guarda en el ViewModel y sobrevive rotaciones de pantalla. Se accede desde `MainActivity` para envolver todo el árbol de Compose:

```kotlin
LAB08Theme(themeMode = viewModel.currentTheme, darkMode = viewModel.isDarkMode) {
    MainContent(viewModel)
}
```

---

## Funcionalidades Completas

### Home
- Grid 2 columnas de productos
- Búsqueda en tiempo real (filtra nombre + descripción)
- Chips de categoría horizontales (incluye "Todo")
- FAB para agregar producto
- Estado Loading / Error (con retry) / Empty / Success

### Details
- Imagen full-width con Coil (AsyncImage)
- Badge de categoría
- Precio formateado `S/. X.XX`
- Botón favorito (rojo cuando activo)
- Botón agregar al carrito
- Botón editar en TopAppBar
- Auto-navega atrás si el producto fue eliminado

### Cart
- Lista de ítems con imagen thumbnail
- Precio total calculado en tiempo real
- Botón eliminar por ítem
- Botón "Confirm Purchase" (vacía el carrito)
- Estado vacío ilustrado

### Favorites
- Filtra la lista de productos por IDs en `favoriteIds`
- Grid igual al Home
- Estado vacío con instrucciones

### Categories
- Lista de categorías con emoji + nombre
- Eliminar con `AlertDialog` de confirmación
- FAB para agregar categoría

### AddProduct / EditProduct
- Formularios con validación inline
- Dropdown para seleccionar categoría
- URL de imagen opcional (genera una aleatoria si se deja vacía)
- EditProduct incluye botón Delete en TopAppBar con confirmación

### AddCategory
- Campo nombre + campo emoji
- `FlowRow` con 10 sugerencias de emoji como `FilterChip`

---

## Dependencias Clave

```toml
# libs.versions.toml
hilt = "2.56"
hiltNavigationCompose = "1.2.0"
navigationCompose = "2.9.0"
coil = "2.7.0"
ksp = "2.2.10-1.0.31"
```

| Librería | Propósito |
|---|---|
| `hilt-android` + `hilt-compiler` (KSP) | Inyección de dependencias |
| `hilt-navigation-compose` | `hiltViewModel()` en composables |
| `navigation-compose` | NavController + NavHost |
| `coil-compose` | Carga de imágenes remotas (AsyncImage) |
| `material-icons-extended` | Iconos completos de Material |
| `lifecycle-viewmodel-compose` | ViewModel en Compose |

---

## Cómo ejecutar

> **Paso 1** — Abrir el proyecto en Android Studio

Abrir `LAB08/` como proyecto Gradle en Android Studio Hedgehog o superior.

> **Paso 2** — Sync de Gradle

```
File → Sync Project with Gradle Files
```

Android Studio descargará automáticamente todas las dependencias. Si hay error en la versión de KSP (`2.2.10-1.0.31`), ajustar al último disponible en [github.com/google/ksp/releases](https://github.com/google/ksp/releases) que sea compatible con la versión de Kotlin del proyecto.

> **Paso 3** — Ejecutar en emulador o dispositivo físico

```
Run → Run 'app'   (Shift+F10)
```

Requiere API 24+ (Android 7.0 Nougat).

> **[CAPTURA DE PANTALLA HOME]**

> **[CAPTURA DE PANTALLA DETALLE]**

> **[CAPTURA DE PANTALLA CARRITO]**

> **[CAPTURA DE PANTALLA CATEGORÍAS]**

> **[CAPTURA DE PANTALLA TEMA OSCURO]**

---

## Cambiar de Real a Fake Repository

Editar `di/AppModule.kt`:

```kotlin
// Real (producción):
@Binds @Singleton
abstract fun bindProductRepository(impl: RealProductRepository): ProductRepository

// Fake (tests/demos) — descomentar y comentar la línea anterior:
// @Binds @Singleton
// abstract fun bindProductRepository(impl: FakeProductRepository): ProductRepository
```

Solo este cambio de una línea afecta a toda la app. Ningún otro archivo cambia.

---

## Diferencias vs Lab04

| Aspecto | Lab04 | Lab08 |
|---|---|---|
| DI | Manual (`StoreViewModelFactory`) | Hilt (`@HiltViewModel`) |
| UseCases | ❌ El VM llama al repo directamente | ✅ Una clase por operación |
| Repositorio | Solo `MockProductRepository` | `Real` + `Fake`, switchable sin tocar UI |
| Application class | No hay `@HiltAndroidApp` | `LAB08Application` registrada en manifest |
| Binding | `remember { MockProductRepository() }` en MainActivity | `@Binds` en `AppModule` |
| Testabilidad | Difícil (repo hardcodeado) | Alta (swap de impl en 1 línea) |

---

## Arquitectura en Diagrama

```
┌─────────────────────────────────────────────────────┐
│                    UI Layer                          │
│  Screens (Composables) ◄── StoreViewModel           │
│                              @HiltViewModel          │
└─────────────────────┬───────────────────────────────┘
                      │ invoca
┌─────────────────────▼───────────────────────────────┐
│                  Domain Layer                        │
│  GetProductsUseCase    AddProductUseCase             │
│  GetCategoriesUseCase  UpdateProductUseCase          │
│  GetProductByIdUseCase DeleteProductUseCase          │
│  AddCategoryUseCase    DeleteCategoryUseCase         │
│                                                      │
│  ProductRepository (interface)                       │
└─────────────────────┬───────────────────────────────┘
                      │ implementa (Hilt decide)
┌─────────────────────▼───────────────────────────────┐
│                   Data Layer                         │
│  RealProductRepository  ←── AppModule @Binds         │
│  FakeProductRepository  (alternativa para tests)     │
│  MockData (seed data)                                │
└─────────────────────────────────────────────────────┘
```
