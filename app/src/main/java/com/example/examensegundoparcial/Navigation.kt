package com.example.examensegundoparcial.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.examensegundoparcial.AuthScreen
import com.example.examensegundoparcial.ExamenScreen
import com.example.examensegundoparcial.FirebaseRepository
import com.example.examensegundoparcial.FormularioScreen
import com.example.examensegundoparcial.ResultadosScreen
import com.example.examensegundoparcial.UserViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    repository: FirebaseRepository,
    userViewModel: UserViewModel
) {
    val isLoggedIn = repository.getCurrentUserId() != null
    val userViewModel: UserViewModel = viewModel()
    val userData = userViewModel.userData.collectAsState().value  // Obtener datos actuales
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screens.Formulario.name else Screens.Auth.name
    ) {
        composable(Screens.Auth.name) {
            AuthScreen(
                navController = navController,
                repository = repository
            )
        }

        composable(Screens.Formulario.name) {
            FormularioScreen(
                navController = navController,
                repository = repository,
                userViewModel = userViewModel
            )
        }

        composable(Screens.Examen.name) {
            ExamenScreen(
                navController = navController,
                repository = repository
            )
        }

        composable("${Screens.Resultados.name}/{calificacion}") { backStackEntry ->
            val calificacion = backStackEntry.arguments?.getString("calificacion")?.toInt() ?: 0
            val userData = userViewModel.userData.collectAsState().value // ✅ obtener datos
            ResultadosScreen(
                navController = navController,
                userData = userData,
                repository = repository,
                calificacion = calificacion
            )
        }
    }
}

enum class Screens {
    Auth,
    Formulario,
    Examen,
    Resultados
}