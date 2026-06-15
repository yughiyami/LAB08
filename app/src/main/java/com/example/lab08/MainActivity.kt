package com.example.lab08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lab08.ui.components.ThemeSelectorSheet
import com.example.lab08.ui.screens.AddCategoryScreen
import com.example.lab08.ui.screens.AddProductScreen
import com.example.lab08.ui.screens.CartScreen
import com.example.lab08.ui.screens.CategoriesScreen
import com.example.lab08.ui.screens.DetailsScreen
import com.example.lab08.ui.screens.EditProductScreen
import com.example.lab08.ui.screens.FavoritesScreen
import com.example.lab08.ui.screens.HomeScreen
import com.example.lab08.ui.theme.LAB08Theme
import com.example.lab08.ui.viewmodel.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Favorites : Screen("favorites")
    data object Cart : Screen("cart")
    data object Categories : Screen("categories")
    data object Details : Screen("details/{productId}") {
        fun route(id: Int) = "details/$id"
    }
    data object AddProduct : Screen("add_product")
    data object EditProduct : Screen("edit_product/{productId}") {
        fun route(id: Int) = "edit_product/$id"
    }
    data object AddCategory : Screen("add_category")
}

private val BOTTOM_NAV_ROUTES = setOf(
    Screen.Home.route, Screen.Favorites.route,
    Screen.Cart.route, Screen.Categories.route
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: StoreViewModel = hiltViewModel()
            LAB08Theme(themeMode = viewModel.currentTheme, darkMode = viewModel.isDarkMode) {
                MainContent(viewModel)
            }
        }
    }
}

@Composable
private fun MainContent(viewModel: StoreViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var showThemeSheet by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (currentRoute in BOTTOM_NAV_ROUTES) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                launchSingleTop = true
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Favorites.route,
                        onClick = { navController.navigate(Screen.Favorites.route) { launchSingleTop = true; restoreState = true } },
                        icon = { Icon(Icons.Default.Favorite, null) },
                        label = { Text("Favorites") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Cart.route,
                        onClick = { navController.navigate(Screen.Cart.route) { launchSingleTop = true; restoreState = true } },
                        icon = { Icon(Icons.Default.ShoppingCart, null) },
                        label = { Text("Cart") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Categories.route,
                        onClick = { navController.navigate(Screen.Categories.route) { launchSingleTop = true; restoreState = true } },
                        icon = { Icon(Icons.Default.Category, null) },
                        label = { Text("Categories") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { showThemeSheet = true },
                        icon = { Icon(Icons.Default.Palette, null) },
                        label = { Text("Theme") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onProductClick = { navController.navigate(Screen.Details.route(it)) },
                    onAddProduct = { navController.navigate(Screen.AddProduct.route) }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onProductClick = { navController.navigate(Screen.Details.route(it)) }
                )
            }
            composable(Screen.Cart.route) {
                CartScreen(viewModel = viewModel)
            }
            composable(Screen.Categories.route) {
                CategoriesScreen(
                    viewModel = viewModel,
                    onAddCategory = { navController.navigate(Screen.AddCategory.route) }
                )
            }
            composable(Screen.Details.route) { backStack ->
                val productId = backStack.arguments?.getString("productId")?.toIntOrNull() ?: return@composable
                DetailsScreen(
                    productId = productId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Screen.EditProduct.route(it)) }
                )
            }
            composable(Screen.AddProduct.route) {
                AddProductScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable(Screen.EditProduct.route) { backStack ->
                val productId = backStack.arguments?.getString("productId")?.toIntOrNull() ?: return@composable
                EditProductScreen(
                    productId = productId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AddCategory.route) {
                AddCategoryScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
        }
    }

    if (showThemeSheet) {
        ThemeSelectorSheet(
            currentTheme = viewModel.currentTheme,
            isDarkMode = viewModel.isDarkMode,
            onThemeChange = { viewModel.setTheme(it) },
            onDarkModeToggle = { viewModel.toggleDarkMode() },
            onDismiss = { showThemeSheet = false }
        )
    }
}
