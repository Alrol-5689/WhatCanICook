package com.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.R
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

        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
        }

        binding.textUsername.text =
            getString(
                R.string.user_info,
                username ?: "desconocido",
                if (userId != -1L) userId.toString() else "X"
            )

        binding.textEmail.text =
            getString(R.string.email_info, email ?: "desconocido")

        binding.logoutButton.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }

    }

}