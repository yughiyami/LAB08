package com.example.lab08.data.repository

import com.example.lab08.data.MockData
import com.example.lab08.domain.model.Category
import com.example.lab08.domain.model.Product
import com.example.lab08.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real implementation backed by in-memory data seeded from MockData.
 * In a production app this would talk to a remote API or Room database.
 */
@Singleton
class RealProductRepository @Inject constructor() : ProductRepository {

    private val products = MockData.products.toMutableList()
    private val categories = MockData.categories.toMutableList()
    private var nextProductId = products.maxOf { it.id } + 1
    private var nextCategoryId = categories.maxOf { it.id } + 1

    override fun getProducts(): List<Product> = products.toList()

    override fun getCategories(): List<Category> = categories.toList()

    override fun getProductById(id: Int): Product? = products.find { it.id == id }

    override fun addProduct(product: Product): Product {
        val newProduct = product.copy(id = nextProductId++)
        products.add(newProduct)
        return newProduct
    }

    override fun updateProduct(product: Product): Boolean {
        val index = products.indexOfFirst { it.id == product.id }
        if (index == -1) return false
        products[index] = product
        return true
    }

    override fun deleteProduct(id: Int): Boolean {
        return products.removeIf { it.id == id }
    }

    override fun addCategory(category: Category): Category {
        val newCategory = category.copy(id = nextCategoryId++)
        categories.add(newCategory)
        return newCategory
    }

    override fun deleteCategory(id: Int): Boolean {
        return categories.removeIf { it.id == id }
    }
}
