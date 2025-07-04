package com.example.examensegundoparcial

import ExamenTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.examensegundoparcial.navigation.AppNavigation
import com.google.firebase.Firebase
import com.google.firebase.initialize


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(this)
        enableEdgeToEdge()
        setContent {
            ExamenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val repository = FirebaseRepository()
                    val userViewModel: UserViewModel = viewModel()
                    AppNavigation(navController = navController, repository = repository, userViewModel = userViewModel)
                }
            }
        }
    }
}
