package com.example.lab08.domain.usecase

import com.example.lab08.domain.repository.ProductRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(id: Int): Boolean = repository.deleteCategory(id)
}
