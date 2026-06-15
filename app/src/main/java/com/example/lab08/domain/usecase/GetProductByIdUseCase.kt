package com.example.lab08.domain.usecase

import com.example.lab08.domain.model.Product
import com.example.lab08.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(id: Int): Product? = repository.getProductById(id)
}
