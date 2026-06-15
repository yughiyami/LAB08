package com.example.lab08.di

import com.example.lab08.data.repository.RealProductRepository
import com.example.lab08.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    /**
     * Bind the interface to the real implementation.
     * To use the fake implementation for testing/demos, replace with:
     *   @Binds abstract fun bindRepository(impl: FakeProductRepository): ProductRepository
     */
    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: RealProductRepository
    ): ProductRepository
}
