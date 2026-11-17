package com.example.proj_soundvault.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object BrandCatalog : Screen("brand_catalog")
}