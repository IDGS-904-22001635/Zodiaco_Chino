package com.example.examensegundoparcial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.examensegundoparcial.navigation.Screens
import kotlinx.coroutines.launch
@Composable
fun ExamenScreen(
    navController: NavHostController,
    repository: FirebaseRepository
) {
    val preguntas = listOf(
        Pregunta("¿Cuál es la suma de 2 + 2?", listOf("8", "6", "4", "3"), 2),
        Pregunta("¿Qué protocolo se utiliza para asignar direcciones IP automáticamente?", listOf("DNS", "TCP", "DHCP", "FTP"), 2),
        Pregunta("¿Cuál es el protocolo utilizado para acceder a páginas web?", listOf("HTTP", "IP", "SSH", "SMTP"), 0),
        Pregunta("¿Qué significa la sigla 'IP'?", listOf("Internet Provider", "Internet Protocol", "Internal Port", "Intranet Protocol"), 1),
        Pregunta("¿Qué protocolo se utiliza para enviar correos electrónicos?", listOf("FTP", "SMTP", "HTTP", "SNMP"), 1),
        Pregunta("¿Qué capa del modelo OSI se encarga del enrutamiento?", listOf("Física", "Red", "Transporte", "Aplicación"), 1),
        Pregunta("¿Cuál de los siguientes es un protocolo seguro de transferencia de archivos?", listOf("FTP", "TFTP", "SFTP", "POP3"), 2),
        Pregunta("¿Qué tecnología permite conectarse de forma inalámbrica a una red local?", listOf("Bluetooth", "Wi-Fi", "USB", "Ethernet"), 1),
        Pregunta("¿Cuál es la dirección IP de loopback?", listOf("192.168.1.1", "10.0.0.1", "127.0.0.1", "0.0.0.0"), 2),
        Pregunta("¿Qué dispositivo conecta redes diferentes y dirige el tráfico?", listOf("Switch", "Hub", "Router", "Bridge"), 2),
        Pregunta("¿Qué puerto usa HTTPS por defecto?", listOf("80", "22", "443", "8080"), 2),
        Pregunta("¿Qué protocolo se usa para acceder de forma remota a una terminal segura?", listOf("Telnet", "SSH", "RDP", "FTP"), 1),
        Pregunta("¿Qué protocolo resuelve nombres de dominio en direcciones IP?", listOf("ARP", "DNS", "IP", "DHCP"), 1),
        Pregunta("¿Qué protocolo se utiliza para monitorear dispositivos de red?", listOf("SMTP", "ICMP", "SNMP", "ARP"), 2),
        Pregunta("¿Qué tipo de dirección IP cambia cada vez que te conectas a la red?", listOf("Estática", "Pública", "Privada", "Dinámica"), 3),
        Pregunta("¿Cuál es la máscara de subred para una red clase C típica?", listOf("255.255.255.0", "255.0.0.0", "255.255.0.0", "255.255.255.255"), 0),
        Pregunta("¿Qué capa del modelo OSI corresponde al cableado físico?", listOf("Enlace de datos", "Red", "Física", "Transporte"), 2),
        Pregunta("¿Qué dispositivo de red trabaja en la capa de enlace de datos?", listOf("Router", "Switch", "Modem", "Firewall"), 1),
        Pregunta("¿Qué puerto se usa para SSH?", listOf("23", "80", "443", "22"), 3),
        Pregunta("¿Qué tipo de red conecta dispositivos dentro de un edificio o zona local?", listOf("WAN", "MAN", "LAN", "PAN"), 2)
    )

    var respuestas by remember { mutableStateOf(List(preguntas.size) { -1 }) }
    var showAlert by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Examen de Redes", style = MaterialTheme.typography.headlineMedium)

        preguntas.forEachIndexed { index, pregunta ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "${index + 1}. ${pregunta.texto}",
                    style = MaterialTheme.typography.bodyLarge
                )

                pregunta.opciones.forEachIndexed { optionIndex, option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (respuestas[index] == optionIndex),
                                onClick = {
                                    val newRespuestas = respuestas.toMutableList()
                                    newRespuestas[index] = optionIndex
                                    respuestas = newRespuestas
                                }
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (respuestas[index] == optionIndex),
                            onClick = {
                                val newRespuestas = respuestas.toMutableList()
                                newRespuestas[index] = optionIndex
                                respuestas = newRespuestas
                            }
                        )
                        Text(text = option, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

        Button(
            onClick = {
                if (respuestas.all { it != -1 }) {
                    val calificacion = calcularCalificacion(preguntas, respuestas)

                    coroutineScope.launch {
                        repository.saveExamResults(
                            userId = repository.getCurrentUserId() ?: "",
                            calificacion = calificacion,
                            respuestas = respuestas
                        )

                        navController.navigate("${Screens.Resultados.name}/$calificacion") {
                            popUpTo(Screens.Examen.name) { inclusive = true }
                        }
                    }
                } else {
                    showAlert = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Terminar Examen")
        }
    }

    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text("Examen incompleto") },
            text = { Text("Por favor responde todas las preguntas antes de terminar.") },
            confirmButton = {
                Button(onClick = { showAlert = false }) {
                    Text("OK")
                }
            }
        )
    }
}

fun calcularCalificacion(preguntas: List<Pregunta>, respuestas: List<Int>): Int {
    val correctas = preguntas.indices.count { respuestas[it] == preguntas[it].respuestaCorrecta }
    return (correctas * 100) / preguntas.size
}

data class Pregunta(
    val texto: String,
    val opciones: List<String>,
    val respuestaCorrecta: Int
)