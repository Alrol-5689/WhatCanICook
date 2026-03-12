package com.app.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.databinding.ActivityLoginBinding
import com.app.model.request.LoginRequest
import com.app.model.response.AuthResponse
import com.app.network.AuthService
import com.app.network.RetrofitClient
import com.app.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authService = RetrofitClient.instance.create(AuthService::class.java)

        acciones()
    }

    private fun acciones() {
        binding.loginButton.setOnClickListener {

            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "Completa todos los campos", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(email, password)

            authService.login(request).enqueue(object : Callback<AuthResponse> {

                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {

                    if (response.isSuccessful && response.body()?.success == true) {

                        val authResponse = response.body()

                        Snackbar.make(binding.root, "Login correcto", Snackbar.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("username", authResponse?.username)
                        intent.putExtra("email", authResponse?.email)
                        startActivity(intent)
                        finish()

                    } else {

                        val message = response.body()?.message ?: "Credenciales incorrectas"
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Snackbar.make(binding.root, "Error de conexión: ${t.message}", Snackbar.LENGTH_SHORT).show()
                }
            })
        }
    }
}