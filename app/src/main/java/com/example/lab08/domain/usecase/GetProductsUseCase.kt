package com.example.lab08.domain.usecase

import com.example.lab08.domain.model.Product
import com.example.lab08.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): List<Product> = repository.getProducts()
}
