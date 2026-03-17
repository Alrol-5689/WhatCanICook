package com.app.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.dto.request.RegisterRequest
import com.app.dto.response.AuthResponse
import com.app.network.RetrofitClient
import com.app.repository.AuthRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val authRepository = AuthRepository(RetrofitClient.authApi)

    private val _registerResponse = MutableLiveData<AuthResponse>()
    val registerResponse: LiveData<AuthResponse> = _registerResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun register(username: String, email: String, password: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Completa todos los campos"
            return
        }

        val request = RegisterRequest(
            username = username.trim(),
            email = email.trim(),
            password = password.trim()
        )

        authRepository.register(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val authResponse = response.body()

                if (response.isSuccessful && authResponse?.success == true) {
                    _registerResponse.value = authResponse
                } else {
                    _errorMessage.value = authResponse?.message ?: "Error al registrar usuario"
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _errorMessage.value = "Error de conexión: ${t.message}"
            }
        })
    }
}