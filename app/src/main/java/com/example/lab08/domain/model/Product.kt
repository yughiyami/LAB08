package com.example.lab08.domain.model

data class Product(
    val id: Int,
    val name: String,
    val categoryId: Int,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isFavorite: Boolean = false
)

data class Category(
    val id: Int,
    val name: String,
    val icon: String
)
