package com.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.databinding.ActivityMainBinding
import com.app.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var username: String? = null
    private var email: String? = null
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = intent.getLongExtra("userId", -1L)
        username = intent.getStringExtra("username")
        email = intent.getStringExtra("email")
        acciones()
    }

    private fun acciones(){
        binding.textUsername.text = "Eres: ${username ?: "desconocido"} ${if (userId != -1L) userId else "X"}"
        binding.textEmail.text = "Tu correo es: ${email ?: "desconocido"}"

        binding.logoutButton.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }

    }

}