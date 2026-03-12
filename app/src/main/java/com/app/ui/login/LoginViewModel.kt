package com.app.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.model.request.LoginRequest
import com.app.model.response.AuthResponse
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

        val request = LoginRequest(email.trim(), password.trim())
        /*data class LoginRequest(
            val email: String,
            val password: String
        )*/

        authRepository.login(request).enqueue(object : Callback<AuthResponse> {
        /*Aquí ViewModel no sabe de Retrofit por dentro. Solo sabe:
            “Repository, haz el login con estos datos”.
        Eso es importante porque separa responsabilidades.*/

            /*enqueue(...) significa:
            “Haz esta petición en segundo plano y cuando termine me avisas”.
            Eso evita bloquear la app.
            Cuando la petición termina, Retrofit llama a uno de estos dos métodos:
                •	onResponse(...)
                •	onFailure(...)*/

                /*
                ----AuthRepository----
                fun login(request: LoginRequest): Call<AuthResponse> {
                    return authApi.login(request)
                }
                	•	recibe un LoginRequest
                    •	se lo pasa a AuthApi
                    •	devuelve la llamada
                ----AuthApi (interface)----
                @POST("auth/login")
                fun login(@Body request: LoginRequest): Call<AuthResponse>
                	•	hacer POST /auth/login
                    •	enviar el request como JSON
                    •	esperar un AuthResponse
                    Retrofit usa esta interfaz para generar automáticamente la llamada HTTP.
                */
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val authResponse = response.body()
                             // HTTP ok        back -> {"success": true}
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