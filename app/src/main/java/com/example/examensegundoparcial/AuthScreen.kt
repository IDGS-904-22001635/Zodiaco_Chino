package com.example.examensegundoparcial

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.example.examensegundoparcial.navigation.Screens
@Composable
fun AuthScreen(
    navController: NavHostController,
    repository: FirebaseRepository
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Por favor ingresa email y contraseña"
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    coroutineScope.launch {
                        try {
                            repository.signInWithEmailAndPassword(email, password)
                            navController.navigate(Screens.Formulario.name) {
                                // Opciones de navegación
                                popUpTo(Screens.Auth.name) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error al iniciar sesión: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                if (email.isBlank()) {
                    errorMessage = "Por favor ingresa un email"
                    return@TextButton
                }

                isLoading = true
                errorMessage = null

                coroutineScope.launch {
                    try {
                        repository.createUserWithEmailAndPassword(email, password)
                        navController.navigate(Screens.Formulario.name) {
                            // Opciones de navegación
                            popUpTo(Screens.Auth.name) { inclusive = true }
                        }
                    } catch (e: Exception) {
                        errorMessage = "Error al registrar: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            }
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}