package com.example.proj_soundvault.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proj_soundvault.ui.screen.auth.LoginScreen
import com.example.proj_soundvault.ui.screen.auth.RegisterScreen
import com.example.proj_soundvault.ui.screen.brand.BrandCatalogScreen
import com.example.proj_soundvault.ui.viewmodel.AuthViewModel

@Composable
fun SoundVaultNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val session by authViewModel.sessionState.collectAsState()

    // Arahkan otomatis saat status login / logout / refresh gagal berubah
    LaunchedEffect(session.isLoggedIn) {
        if (session.isLoggedIn) {
            navController.navigate(Screen.BrandCatalog.route) {
                popUpTo(0) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (session.isLoggedIn) Screen.BrandCatalog.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.BrandCatalog.route) {
            BrandCatalogScreen(
                isAdmin = session.isAdmin,
                userName = session.namaLengkap,
                onLogout = { authViewModel.logout() }
            )
        }
    }
}