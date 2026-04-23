package com.app.ui.users.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.R
import com.app.databinding.FragmentUserSearchBinding
import com.app.dto.model.UserDto
import com.app.dto.request.FriendRequest
import com.app.network.RetrofitClient
import com.app.ui.main.MainActivity
import com.app.ui.users.search.adapter.UserAdapter
import com.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserSearchFragment : Fragment(R.layout.fragment_user_search) {

    private var _binding: FragmentUserSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserSearchViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter
    private var searchRunnable: Runnable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUserSearchBinding.bind(view)

        binding.menuButton.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }

        setupRecyclerView()
        observeViewModel()
        setupLiveSearch()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            onAddFriendClick = { user -> sendFriendRequest(user) }
        )

        binding.recyclerUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerUsers.adapter = userAdapter
    }

    private fun observeViewModel() {
        viewModel.users.observe(viewLifecycleOwner) { users ->
            userAdapter.setUsers(users)
            binding.textSearchHint.text =
                if (users.isNullOrEmpty()) getString(R.string.no_se_encontraron_usuarios)
                else getString(R.string.usuarios_encontrados, users.size)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLiveSearch() {
        val handler = Handler(Looper.getMainLooper())
        binding.editSearchUser.doAfterTextChanged { editable ->
            val query = editable?.toString().orEmpty()

            searchRunnable?.let { handler.removeCallbacks(it) }
            searchRunnable = Runnable { viewModel.searchUsers(query) }
            handler.postDelayed(searchRunnable!!, 300)
        }
    }

    private fun sendFriendRequest(user: UserDto) {
        val requesterId = SessionManager.userId
        if (requesterId == -1L) {
            Toast.makeText(requireContext(), "Usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }
        if (requesterId == user.id) {
            Toast.makeText(requireContext(), "No puedes añadirte a ti mismo", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.friendApi.sendFriendRequest(FriendRequest(requesterId, user.id))
            .enqueue(object : Callback<com.app.dto.model.FriendDto> {
                override fun onResponse(
                    call: Call<com.app.dto.model.FriendDto>,
                    response: Response<com.app.dto.model.FriendDto>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Solicitud enviada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al enviar solicitud", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<com.app.dto.model.FriendDto>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message ?: "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        val handler = Handler(Looper.getMainLooper())
        searchRunnable?.let { handler.removeCallbacks(it) }
        super.onDestroyView()
        _binding = null
    }
}
