package com.app.subscripfy

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.subscripfy.data.model.GoogleAuthUiClient
import com.app.subscripfy.presentation.home.AddSubscriptionScreen
import com.app.subscripfy.presentation.home.EditSubscriptionScreen
import com.app.subscripfy.presentation.home.HomeScreen
import com.app.subscripfy.presentation.home.HomeViewModel
import com.app.subscripfy.presentation.home.MainScreen
import com.app.subscripfy.presentation.sign_in.SignInScreen
import com.app.subscripfy.presentation.sign_in.SignInViewModel
import com.app.subscripfy.ui.theme.SubscripfyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SubscripfyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // [LOGIKA ANTI-KEDIP]
                    // Tentukan halaman awal sebelum NavHost dibuat.
                    // Jika user sudah login -> langsung ke "home".
                    // Jika belum -> ke "sign_in".
                    val startDestination = if (googleAuthUiClient.getSignedInUser() != null) {
                        "home"
                    } else {
                        "sign_in"
                    }

                    NavHost(navController = navController, startDestination = startDestination) {

                        // --- RUTE 1: LOGIN ---
                        composable("sign_in") {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsState()

                            // [SOLUSI UTAMA]
                            // Reset status login setiap kali layar ini dibuka.
                            // Ini mencegah aplikasi "mengira" user masih login sukses.
                            LaunchedEffect(key1 = Unit) {
                                viewModel.resetState()
                            }

                            // ... (Sisa kode di bawah ini tetap sama) ...

                            // Pastikan TIDAK ADA LaunchedEffect lain yang mengecek googleAuthUiClient.getSignedInUser() di sini!
                            // Cukup LaunchedEffect(key1 = state.isSignInSuccessfull) saja.

                            LaunchedEffect(key1 = state.isSignInSuccessfull) {
                                if (state.isSignInSuccessfull) {
                                    Toast.makeText(applicationContext, "Login Berhasil!", Toast.LENGTH_LONG).show()
                                    navController.navigate("home") {
                                        popUpTo("sign_in") { inclusive = true }
                                    }
                                }
                            }

                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInResult = googleAuthUiClient.signIn()
                                        viewModel.onSignInResult(signInResult)
                                    }
                                }
                            )
                        }

                        // --- RUTE 2: HOME (DASHBOARD) ---
                        composable("home") {
                            val viewModel = viewModel<HomeViewModel>()
                            val userData = googleAuthUiClient.getSignedInUser()

                            // [PANGGIL MAIN SCREEN DISINI]
                            // MainScreen yang akan mengatur tab Home/Analysis/Profile
                            MainScreen(
                                userData = userData,
                                viewModel = viewModel,
                                onSignOut = {
                                    lifecycleScope.launch {
                                        navController.navigate("sign_in") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                        try {
                                            googleAuthUiClient.signOut()
                                            Toast.makeText(applicationContext, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) { e.printStackTrace() }
                                    }
                                },
                                onAddClick = {
                                    navController.navigate("add_subscription")
                                },
                                onEditClick = { subId ->
                                    navController.navigate("edit_subscription/$subId")
                                }
                            )
                        }

                        // --- RUTE 3: TAMBAH DATA ---
                        composable("add_subscription") {
                            // Ambil data user TERBARU langsung dari Client
                            val userData = googleAuthUiClient.getSignedInUser()

                            if (userData != null) {
                                AddSubscriptionScreen(
                                    userId = userData.userId, // Pastikan ini BUKAN string kosong
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    }
                                )
                            } else {
                                // Jika user null (aneh), lempar balik ke login
                                LaunchedEffect(Unit) {
                                    navController.navigate("sign_in") { popUpTo("home") { inclusive = true } }
                                }
                            }
                        }

                        // --- RUTE 4: EDIT DATA ---
                        composable(
                            route = "edit_subscription/{subId}",
                            arguments = listOf(
                                navArgument("subId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val subId = backStackEntry.arguments?.getString("subId")
                            if (subId != null) {
                                EditSubscriptionScreen(
                                    subscriptionId = subId,
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}