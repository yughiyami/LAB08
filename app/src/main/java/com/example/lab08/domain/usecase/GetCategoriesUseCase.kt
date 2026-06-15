package com.example.lab08.domain.usecase

import com.example.lab08.domain.model.Category
import com.example.lab08.domain.repository.ProductRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): List<Category> = repository.getCategories()
}
