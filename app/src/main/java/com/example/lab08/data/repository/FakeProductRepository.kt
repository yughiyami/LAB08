package com.example.lab08.data.repository

import com.example.lab08.domain.model.Category
import com.example.lab08.domain.model.Product
import com.example.lab08.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fake/test implementation with a minimal hardcoded dataset.
 * Useful for UI previews, automated tests, and development without network.
 * Switch to this in AppModule by changing the @Binds target.
 */
@Singleton
class FakeProductRepository @Inject constructor() : ProductRepository {

    private val categories = mutableListOf(
        Category(1, "Tech", "🔧"),
        Category(2, "Books", "📚")
    )

    private val products = mutableListOf(
        Product(1, "Fake Phone", 1, "A fake phone for testing", 99.99, "https://picsum.photos/seed/fake1/400/300"),
        Product(2, "Fake Book", 2, "A fake book for testing", 19.99, "https://picsum.photos/seed/fake2/400/300")
    )

    private var nextProductId = 3
    private var nextCategoryId = 3

    override fun getProducts(): List<Product> = products.toList()

    override fun getCategories(): List<Category> = categories.toList()

    override fun getProductById(id: Int): Product? = products.find { it.id == id }

    override fun addProduct(product: Product): Product {
        val new = product.copy(id = nextProductId++)
        products.add(new)
        return new
    }

    override fun updateProduct(product: Product): Boolean {
        val index = products.indexOfFirst { it.id == product.id }
        if (index == -1) return false
        products[index] = product
        return true
    }

    override fun deleteProduct(id: Int): Boolean = products.removeIf { it.id == id }

    override fun addCategory(category: Category): Category {
        val new = category.copy(id = nextCategoryId++)
        categories.add(new)
        return new
    }

    override fun deleteCategory(id: Int): Boolean = categories.removeIf { it.id == id }
}
