package com.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.widget.Toast
import com.app.R
import com.app.databinding.ActivityMainBinding
import com.app.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var username: String? = null
    private var email: String? = null
    private var userId: Long = -1L
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        observarViewModel()

        // Cargar recetas
        viewModel.loadPublicRecipes()

        userId = intent.getLongExtra("userId", -1L)
        username = intent.getStringExtra("username")
        email = intent.getStringExtra("email")
        acciones()
    }

    private fun observarViewModel() {
        viewModel.recipes.observe(this) { recipes ->
            // TODO: conectar con RecyclerView adapter
            // Por ahora solo comprobamos que llegan datos
            if (!recipes.isNullOrEmpty()) {
                Toast.makeText(this, "Recetas cargadas: ${recipes.size}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun acciones(){

        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
        }

        if (userId != -1L)
            binding.drawerUsername.text =
                getString(
                    R.string.username_with_id,
                    username ?: getString(R.string.unknown_user),
                    userId
                )
        else binding.drawerUsername.text = username ?: getString(R.string.unknown_user)

        binding.drawerEmail.text = email ?: getString(R.string.unknown_user)

        binding.logoutButton.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}