package com.example.lab08.domain.usecase

import com.example.lab08.domain.model.Product
import com.example.lab08.domain.repository.ProductRepository
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(product: Product): Product = repository.addProduct(product)
}
