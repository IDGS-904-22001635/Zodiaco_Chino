package com.example.examensegundoparcial

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData

    fun setUserData(user: UserData) {
        _userData.value = user
    }
}
data class UserData(
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val diaNacimiento: Int = 0,
    val mesNacimiento: Int = 0,
    val anioNacimiento: Int = 0,
    val sexo: String = ""
)
