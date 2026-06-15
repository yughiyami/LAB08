package com.example.lab08.domain.repository

import com.example.lab08.domain.model.Category
import com.example.lab08.domain.model.Product

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
