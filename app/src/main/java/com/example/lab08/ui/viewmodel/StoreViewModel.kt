package com.example.lab08.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lab08.domain.model.Category
import com.example.lab08.domain.model.Product
import com.example.lab08.domain.usecase.AddCategoryUseCase
import com.example.lab08.domain.usecase.AddProductUseCase
import com.example.lab08.domain.usecase.DeleteCategoryUseCase
import com.example.lab08.domain.usecase.DeleteProductUseCase
import com.example.lab08.domain.usecase.GetCategoriesUseCase
import com.example.lab08.domain.usecase.GetProductsUseCase
import com.example.lab08.domain.usecase.UpdateProductUseCase
import com.example.lab08.ui.state.ProductUiState
import com.example.lab08.ui.theme.AppThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    val favoriteIds = mutableStateListOf<Int>()
    val cartItems = mutableStateListOf<Product>()

    var currentTheme by mutableStateOf(AppThemeMode.MODERN_BLUE)
        private set
    var isDarkMode by mutableStateOf(false)
        private set

    init {
        loadData()
    }

    fun loadData() {
        _uiState.value = ProductUiState.Loading
        try {
            val products = getProducts()
            val categories = getCategories()
            _uiState.value = ProductUiState.Success(products, categories)
        } catch (e: Exception) {
            _uiState.value = ProductUiState.Error(e.message ?: "Unknown error")
        }
    }

    fun getFilteredProducts(): List<Product> {
        val state = _uiState.value as? ProductUiState.Success ?: return emptyList()
        return state.products.filter { product ->
            val matchesQuery = _searchQuery.value.isBlank() ||
                product.name.contains(_searchQuery.value, ignoreCase = true) ||
                product.description.contains(_searchQuery.value, ignoreCase = true)
            val matchesCategory = _selectedCategoryId.value == null ||
                product.categoryId == _selectedCategoryId.value
            matchesQuery && matchesCategory
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
    }

    fun addProduct(name: String, categoryId: Int, description: String, price: Double, imageUrl: String) {
        val product = Product(
            id = 0,
            name = name,
            categoryId = categoryId,
            description = description,
            price = price,
            imageUrl = imageUrl.ifBlank { "https://picsum.photos/seed/${name.hashCode()}/400/300" }
        )
        addProductUseCase(product)
        loadData()
    }

    fun updateProduct(product: Product) {
        updateProductUseCase(product)
        loadData()
    }

    fun deleteProduct(id: Int) {
        deleteProductUseCase(id)
        favoriteIds.remove(id)
        cartItems.removeIf { it.id == id }
        loadData()
    }

    fun addCategory(name: String, icon: String) {
        addCategoryUseCase(Category(id = 0, name = name, icon = icon))
        loadData()
    }

    fun deleteCategory(id: Int) {
        deleteCategoryUseCase(id)
        loadData()
    }

    fun toggleFavorite(productId: Int) {
        if (favoriteIds.contains(productId)) favoriteIds.remove(productId)
        else favoriteIds.add(productId)
    }

    fun addToCart(product: Product) {
        if (!cartItems.any { it.id == product.id }) cartItems.add(product)
    }

    fun removeFromCart(productId: Int) {
        cartItems.removeIf { it.id == productId }
    }

    fun setTheme(theme: AppThemeMode) {
        currentTheme = theme
    }

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
    }

    fun getProductById(id: Int): Product? {
        return (uiState.value as? ProductUiState.Success)?.products?.find { it.id == id }
    }
}
