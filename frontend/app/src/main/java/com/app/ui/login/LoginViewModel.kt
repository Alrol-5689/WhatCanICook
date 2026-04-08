package com.app.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.dto.request.LoginRequest
import com.app.dto.response.AuthResponse
import com.app.network.RetrofitClient
import com.app.repository.AuthRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    // Se crea AuthRepository
    // Conectamso LoginViewModel → AuthRepository → AuthApi
    private val authRepository = AuthRepository(RetrofitClient.authApi)
    private val _loginResponse = MutableLiveData<AuthResponse>()
    // _loginResponse -> Es privada y modificable. Solo el ViewModel puede cambiarla.
    val loginResponse: LiveData<AuthResponse> = _loginResponse
    // loginResponse -> Es pública pero solo de lectura.
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Completa todos los campos"
            return
        }

        // MOCK RESPONSE PARA DESARROLLO UI
        val authResponse = AuthResponse(true, "Mock OK", null)
        _loginResponse.value = authResponse
    }
}