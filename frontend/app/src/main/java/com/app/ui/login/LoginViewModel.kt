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
    private val _loginResponse = MutableLiveData<AuthResponse?>()
    val loginResponse: LiveData<AuthResponse?> = _loginResponse
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Completa todos los campos"
            return
        }

        val request = LoginRequest(email.trim(), password.trim())

        authRepository.login(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val authResponse = response.body()
                if (response.isSuccessful && authResponse?.success == true) {
                    _loginResponse.value = authResponse
                } else {
                    _errorMessage.value = authResponse?.message ?: "Credenciales incorrectas"
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _errorMessage.value = "Error de conexión: ${t.message}"
            }
        })
    }
}
