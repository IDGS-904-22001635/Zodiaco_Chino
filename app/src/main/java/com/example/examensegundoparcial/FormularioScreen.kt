package com.example.examensegundoparcial

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch



@SuppressLint("UnrememberedMutableState")
@Composable
fun FormularioScreen(
    navController: NavHostController,
    repository: FirebaseRepository,
    userViewModel: UserViewModel // ✅ ViewModel ya viene como parámetro
) {
    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var dia by remember { mutableStateOf("") }
    var mes by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }

    val sexoOptions = listOf("Masculino", "Femenino", "Otro")
    var selectedSexo by remember { mutableStateOf(sexoOptions[0]) }

    val coroutineScope = rememberCoroutineScope()
    val userId = repository.getCurrentUserId()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Formulario de Datos Personales", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre(s)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apellidoPaterno,
            onValueChange = { apellidoPaterno = it },
            label = { Text("Apellido Paterno") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apellidoMaterno,
            onValueChange = { apellidoMaterno = it },
            label = { Text("Apellido Materno") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Fecha de Nacimiento", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = dia,
                onValueChange = { if (it.length <= 2) dia = it },
                label = { Text("Día") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = mes,
                onValueChange = { if (it.length <= 2) mes = it },
                label = { Text("Mes") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = anio,
                onValueChange = { if (it.length <= 4) anio = it },
                label = { Text("Año") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Text("Sexo", style = MaterialTheme.typography.labelLarge)
        Column {
            sexoOptions.forEach { option ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (option == selectedSexo),
                            onClick = { selectedSexo = option }
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == selectedSexo),
                        onClick = { selectedSexo = option }
                    )
                    Text(
                        text = option,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    nombre = ""
                    apellidoPaterno = ""
                    apellidoMaterno = ""
                    dia = ""
                    mes = ""
                    anio = ""
                    selectedSexo = sexoOptions[0]
                    errorMessage = null
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpiar")
            }

            Button(
                onClick = {
                    if (nombre.isBlank() || apellidoPaterno.isBlank() || dia.isBlank() || mes.isBlank() || anio.isBlank()) {
                        errorMessage = "Por favor llena todos los campos requeridos"
                        return@Button
                    }

                    val userData = UserData(
                        nombre = nombre,
                        apellidoPaterno = apellidoPaterno,
                        apellidoMaterno = apellidoMaterno,
                        diaNacimiento = dia.toIntOrNull() ?: 0,
                        mesNacimiento = mes.toIntOrNull() ?: 0,
                        anioNacimiento = anio.toIntOrNull() ?: 0,
                        sexo = selectedSexo
                    )

                    userViewModel.setUserData(userData) // ✅ Guardar en ViewModel

                    coroutineScope.launch {
                        if (userId != null) {
                            repository.saveUserData(userId, userData)
                            navController.navigate("Examen") {
                                popUpTo("Formulario") { inclusive = true }
                            }
                        } else {
                            errorMessage = "Usuario no autenticado"
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Siguiente")
            }
        }

        errorMessage?.let { err ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = err,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}