package com.app.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.databinding.ActivityRecipesBinding
import com.app.ui.main.MainActivity

class FeedFragment : Fragment() {

    private var _binding: ActivityRecipesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textWelcome.text = "INICIO"

        binding.menuButton.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }

        setupDailyContent()
    }

    private fun setupDailyContent() {
        // Consejo aleatorio
        val randomConsejo = DailyContentData.consejos.random()
        binding.textConsejoDia.text = randomConsejo

        // Plato exótico aleatorio
        val randomPlato = DailyContentData.platosExoticos.random()
        binding.textPlatoNombre.text = randomPlato.nombre
        binding.textPlatoPais.text = "Origen: ${randomPlato.pais}"
        binding.textPlatoIngrediente.text = "Ingrediente principal: ${randomPlato.ingredientePrincipal}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
