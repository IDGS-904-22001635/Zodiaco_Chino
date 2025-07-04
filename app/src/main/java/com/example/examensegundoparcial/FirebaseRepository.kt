package com.example.examensegundoparcial

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirebaseRepository {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    // Autenticación
    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun signOut() {
        auth.signOut()
    }

    // Firestore - Datos del usuario
    suspend fun saveUserData(userId: String, userData: UserData) {
        db.collection("users").document(userId).set(userData).await()
    }

    suspend fun getUserData(userId: String): UserData? {
        return db.collection("users").document(userId).get().await().toObject(UserData::class.java)
    }

    suspend fun saveExamResults(
        userId: String,
        calificacion: Int,
        respuestas: List<Int>
    ) {
        val results = mapOf(
            "calificacion" to calificacion,
            "respuestas" to respuestas,
            "fecha" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )
        db.collection("exam_results").document(userId).set(results).await()
    }

    suspend fun getExamResults(userId: String): FirebaseExamResults? {
        return db.collection("exam_results").document(userId).get().await().toObject(
            FirebaseExamResults::class.java)
    }
}

data class FirebaseUserData(
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val diaNacimiento: Int = 0,
    val mesNacimiento: Int = 0,
    val anioNacimiento: Int = 0,
    val sexo: String = ""
)

data class FirebaseExamResults(
    val calificacion: Int = 0,
    val respuestas: List<Int> = emptyList(),
    val fecha: String = ""
)