package com.example.lab08.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lab08.ui.components.CategoryCard
import com.example.lab08.ui.components.ProductCard
import com.example.lab08.ui.components.ProductSearchBar
import com.example.lab08.ui.state.ProductUiState
import com.example.lab08.ui.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: StoreViewModel,
    onProductClick: (Int) -> Unit,
    onAddProduct: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val favoriteIds = viewModel.favoriteIds
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProduct,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add product")
            }
        }
    ) { padding ->
        when (uiState) {
            is ProductUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is ProductUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text((uiState as ProductUiState.Error).message)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadData() }) { Text("Retry") }
                }
            }
            is ProductUiState.Success -> {
                val state = uiState as ProductUiState.Success
                val filtered = viewModel.getFilteredProducts()
                Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(8.dp))
                    ProductSearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        onFilterClick = { showFilterSheet = true },
                        hasActiveFilter = selectedCategoryId != null
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            CategoryCard(
                                name = "Todo",
                                isSelected = selectedCategoryId == null,
                                onClick = { viewModel.setSelectedCategory(null) }
                            )
                        }
                        items(state.categories) { cat ->
                            CategoryCard(
                                name = "${cat.icon} ${cat.name}",
                                isSelected = selectedCategoryId == cat.id,
                                onClick = { viewModel.setSelectedCategory(cat.id) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    if (filtered.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No products found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(filtered) { product ->
                                ProductCard(
                                    product = product,
                                    isFavorite = favoriteIds.contains(product.id),
                                    onFavoriteToggle = { viewModel.toggleFavorite(product.id) },
                                    onAddToCart = { viewModel.addToCart(product) },
                                    onClick = { onProductClick(product.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        val state = uiState as? ProductUiState.Success
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Filter by Category", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                CategoryCard(name = "All", isSelected = selectedCategoryId == null, onClick = {
                    viewModel.setSelectedCategory(null); showFilterSheet = false
                })
                state?.categories?.forEach { cat ->
                    Spacer(Modifier.height(8.dp))
                    CategoryCard(
                        name = "${cat.icon} ${cat.name}",
                        isSelected = selectedCategoryId == cat.id,
                        onClick = { viewModel.setSelectedCategory(cat.id); showFilterSheet = false }
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
