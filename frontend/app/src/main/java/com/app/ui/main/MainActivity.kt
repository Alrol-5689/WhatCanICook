package com.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.widget.Toast
import com.app.R
import com.app.databinding.ActivityMainBinding
import com.app.ui.login.LoginActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ui.recipes.RecipeDetailActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var username: String? = null
    private var email: String? = null
    private var userId: Long = -1L
    private lateinit var viewModel: MainViewModel
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Crea vista desde XML
        setContentView(binding.root) // -> "Esta es la pantalla que quiero mostrar"

        // Creamos el adapter y le pasamos lo que debe hacer cuando se pulsa una receta
        recipeAdapter = RecipeAdapter { recipeId ->
            // Creamos un intent para abrir la pantalla de detalle
            val intent = Intent(this@MainActivity, RecipeDetailActivity::class.java)

            // Le pasamos el id de la receta para que la siguiente pantalla sepa cuál cargar
            intent.putExtra("recipeId", recipeId)

            // Lanzamos la nueva Activity
            startActivity(intent)
        }

        // Indicamos que el RecyclerView mostrará los elementos en forma de lista vertical
        binding.recipesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Conectamos el adapter con el RecyclerView para que pueda mostrar los datos
        binding.recipesRecyclerView.adapter = recipeAdapter

        // ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModelObserver()

        userId = intent.getLongExtra("userId", -1L)
        username = intent.getStringExtra("username")
        email = intent.getStringExtra("email")

        // Cargar recetas
        viewModel.loadPublicRecipes()

        actions()
    }

    private fun viewModelObserver() {
        // Observamos la lista de recetas que viene del ViewModel
        viewModel.recipes.observe(this) { recipes ->
            // Cuando llegan datos, filtramos las recetas del usuario logueado
            // para no mostrarlas en la pantalla principal
            val filteredRecipes = recipes
                ?.filter { recipe -> recipe.userId != userId }
                ?: emptyList()

            // Pasamos la lista filtrada al adapter
            recipeAdapter.setRecipes(filteredRecipes)
        }

        // Observamos posibles errores al cargar recetas
        viewModel.error.observe(this) { errorMsg ->
            // Mostramos un mensaje rápido en pantalla si algo falla
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun actions(){

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