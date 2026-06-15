package com.example.lab08.ui.state

import com.example.lab08.domain.model.Category
import com.example.lab08.domain.model.Product

sealed class ProductUiState {
    data object Loading : ProductUiState()
    data class Success(
        val products: List<Product>,
        val categories: List<Category>
    ) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}
