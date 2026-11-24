package com.example.proj_soundvault.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proj_soundvault.data.remote.RetrofitClient // TODO CEK: sesuaikan nama kelas Retrofit client kamu
import com.example.proj_soundvault.data.repository.ListingRepository
import com.example.proj_soundvault.ui.screen.CreateEditListingScreen
import com.example.proj_soundvault.ui.screen.HomeCatalogScreen
import com.example.proj_soundvault.ui.screen.ItemDetailScreen
import com.example.proj_soundvault.ui.screen.auth.LoginScreen
import com.example.proj_soundvault.ui.screen.auth.RegisterScreen
import com.example.proj_soundvault.ui.screen.brand.BrandCatalogScreen
import com.example.proj_soundvault.ui.viewmodel.AuthViewModel
import com.example.proj_soundvault.ui.viewmodel.BrandViewModel // TODO CEK: sesuaikan package BrandViewModel kamu
import com.example.proj_soundvault.ui.viewmodel.ListingViewModel

@Composable
fun SoundVaultNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    brandViewModel: BrandViewModel = viewModel()
) {
    val context = LocalContext.current
    val session by authViewModel.sessionState.collectAsState()

    // TODO CEK: sesuaikan cara mengambil ApiService dari RetrofitClient proyekmu
    // Contoh umum: RetrofitClient.getApiService(context) atau RetrofitClient.apiService
    val listingRepository = remember {
        ListingRepository(RetrofitClient.getApiService(context))
    }
    val listingViewModel: ListingViewModel = viewModel(
        factory = ListingViewModel.Factory(listingRepository)
    )

    // TODO CEK: sesuaikan nama StateFlow di BrandViewModel kamu
    // Jika pakai uiState: val brandUiState by brandViewModel.uiState.collectAsState(); val brands = brandUiState.brands
    val brandUiState by brandViewModel.uiState.collectAsState()
    val brands = brandUiState.brands

    // Arahkan otomatis saat status login / logout / refresh gagal berubah
    LaunchedEffect(session.isLoggedIn) {
        if (session.isLoggedIn) {
            navController.navigate("home_catalog") {
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
        startDestination = if (session.isLoggedIn) "home_catalog" else Screen.Login.route
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

        composable("home_catalog") {
            HomeCatalogScreen(
                listingViewModel = listingViewModel,
                brands = brands,
                onItemClick = { itemId -> navController.navigate("item_detail/$itemId") },
                onCreateClick = { navController.navigate("create_listing") },
                onWishlistClick = { /* Disambungkan pada Week 13 (Wishlist) */ }
            )
        }

        composable(
            route = "item_detail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: return@composable
            ItemDetailScreen(
                itemId = itemId,
                // TODO CEK: sesuaikan nama field ID user di sessionState (misal: session.userId atau session.idUser)
                currentUserId = session.idUser,
                currentUserRole = if (session.isAdmin) "ADMIN" else "USER",
                listingViewModel = listingViewModel,
                onNavigateBack = { navController.popBackStack() },
                onEditClick = { id -> navController.navigate("edit_listing/$id") },
                onOfferClick = { /* Disambungkan pada Week 12 (OfferBottomSheet) */ }
            )
        }

        composable("create_listing") {
            CreateEditListingScreen(
                editItemId = null,
                brands = brands,
                listingViewModel = listingViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "edit_listing/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: return@composable
            CreateEditListingScreen(
                editItemId = itemId,
                brands = brands,
                listingViewModel = listingViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}