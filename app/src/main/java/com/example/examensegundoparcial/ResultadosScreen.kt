package com.example.examensegundoparcial
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.util.*


@Composable
fun ResultadosScreen(
    calificacion: Int,
    repository: FirebaseRepository,
    navController: NavHostController,
    userData: UserData,
) {
    val edad = calcularEdad(userData.diaNacimiento, userData.mesNacimiento, userData.anioNacimiento)
    val signoZodiacal = determinarSignoZodiacalChino(userData.anioNacimiento)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hola ${userData.nombre} ${userData.apellidoPaterno} ${userData.apellidoMaterno}",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tienes $edad años y tu signo zodiacal chino es:",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = getSignoZodiacalImage(signoZodiacal)),
            contentDescription = signoZodiacal,
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = signoZodiacal,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Calificación: $calificacion",
            style = MaterialTheme.typography.headlineMedium
        )
        Button(
            onClick = {
                navController.navigate("Formulario") {
                    popUpTo("Resultados") { inclusive = true } // Limpia la pila hasta "Resultados"
                }
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Volver al Formulario")
        }
    }

}

// Cálculo de edad considerando si ya pasó el cumpleaños en este año
fun calcularEdad(dia: Int, mes: Int, anio: Int): Int {
    val hoy = Calendar.getInstance()
    val cumple = Calendar.getInstance().apply {
        set(Calendar.YEAR, hoy.get(Calendar.YEAR))
        set(Calendar.MONTH, mes - 1)
        set(Calendar.DAY_OF_MONTH, dia)
    }
    var edad = hoy.get(Calendar.YEAR) - anio
    if (hoy.before(cumple)) edad--
    return edad
}

// Signo zodiacal chino según el año de nacimiento
fun determinarSignoZodiacalChino(anio: Int): String {
    val signos = listOf(
        "Mono", "Gallo", "Perro", "Cerdo", "Rata", "Buey",
        "Tigre", "Conejo", "Dragón", "Serpiente", "Caballo", "Cabra"
    )
    return signos[anio % 12]
}

// Obtiene el recurso de imagen según el signo zodiacal chino
fun getSignoZodiacalImage(signo: String): Int {
    return when (signo) {
        "Rata" -> R.drawable.rata
        "Buey" -> R.drawable.buey
        "Tigre" -> R.drawable.tigre
        "Conejo" -> R.drawable.conejo
        "Dragón" -> R.drawable.dragon
        "Serpiente" -> R.drawable.serpiente
        "Caballo" -> R.drawable.caballo
        "Cabra" -> R.drawable.cabra
        "Mono" -> R.drawable.mono
        "Gallo" -> R.drawable.gallo
        "Perro" -> R.drawable.perro
        "Cerdo" -> R.drawable.cerdo
        else -> R.drawable.unknown // Por si no coincide
    }
}