package com.app.subscripfy.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart // Ikon untuk Budget
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.subscripfy.data.model.UserData

// ... import ...

// ... imports ...

@Composable
fun MainScreen(
    userData: UserData?,
    viewModel: HomeViewModel,
    onSignOut: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E1E1E),
                contentColor = Color.White
            ) {
                val colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = Color(0xFFBB86FC),
                    indicatorColor = Color(0xFFBB86FC),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
                // ... (Item Navigasi sama seperti sebelumnya) ...
                NavigationBarItem(
                    selected = bottomNavController.currentDestination?.route == "home_tab",
                    onClick = { bottomNavController.navigate("home_tab") { popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } },
                    icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, colors = colors
                )
                NavigationBarItem(
                    selected = bottomNavController.currentDestination?.route == "stats_tab",
                    onClick = { bottomNavController.navigate("stats_tab") { popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } },
                    icon = { Icon(Icons.Default.PieChart, null) }, label = { Text("Stats") }, colors = colors
                )
                NavigationBarItem(
                    selected = bottomNavController.currentDestination?.route == "profile_tab",
                    onClick = { bottomNavController.navigate("profile_tab") { popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } },
                    icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") }, colors = colors
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home_tab",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home_tab") {
                HomeScreen(
                    userData = userData,
                    viewModel = viewModel,
                    onSignOut = onSignOut,
                    onAddClick = onAddClick,
                    onEditClick = onEditClick,
                    // [FITUR BARU] Callback untuk pindah ke tab profil
                    onProfileClick = {
                        bottomNavController.navigate("profile_tab") {
                            popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable("stats_tab") { StatsScreen(viewModel = viewModel) }
            composable("profile_tab") { ProfileScreen(userData = userData, onSignOut = onSignOut, viewModel = viewModel) }
        }
    }
}