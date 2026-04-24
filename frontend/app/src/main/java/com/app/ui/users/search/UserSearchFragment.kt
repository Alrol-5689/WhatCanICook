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
import com.app.ui.users.search.adapter.FriendAction
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

    private var acceptedFriendUserIds: Set<Long> = emptySet()
    private var pendingIncomingByUserId: Map<Long, Long> = emptyMap() // otherUserId -> friendId
    private var pendingOutgoingByUserId: Map<Long, Long> = emptyMap() // otherUserId -> friendId

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUserSearchBinding.bind(view)

        binding.menuButton.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }

        setupRecyclerView()
        observeViewModel()
        setupLiveSearch()
        loadFriendshipState()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            currentUserId = SessionManager.userId,
            onActionClick = { user, action ->
                when (action) {
                    FriendAction.REMOVE -> removeFriendship(user)
                    FriendAction.ACCEPT -> acceptRequestFrom(user)
                    FriendAction.CANCEL -> cancelRequestTo(user)
                    FriendAction.ADD -> sendFriendRequest(user)
                    FriendAction.NONE -> Unit
                }
            }
        )

        binding.recyclerUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerUsers.adapter = userAdapter
        userAdapter.setAcceptedFriendUserIds(acceptedFriendUserIds)
        userAdapter.setPendingIncomingUserIds(pendingIncomingByUserId.keys)
        userAdapter.setPendingOutgoingUserIds(pendingOutgoingByUserId.keys)
    }

    private fun observeViewModel() {
        viewModel.users.observe(viewLifecycleOwner) { users ->
            val filtered = (users ?: emptyList()).filter { it.id != SessionManager.userId }
            userAdapter.setUsers(filtered)
            binding.textSearchHint.text =
                if (filtered.isEmpty()) getString(R.string.no_se_encontraron_usuarios)
                else getString(R.string.usuarios_encontrados, filtered.size)
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
                        loadFriendshipState()
                    } else {
                        Toast.makeText(requireContext(), "Error al enviar solicitud", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<com.app.dto.model.FriendDto>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message ?: "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun acceptRequestFrom(user: UserDto) {
        val friendId = pendingIncomingByUserId[user.id]
        if (friendId == null) {
            Toast.makeText(requireContext(), "Solicitud no encontrada", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.friendApi.acceptFriend(friendId)
            .enqueue(object : Callback<com.app.dto.model.FriendDto> {
                override fun onResponse(
                    call: Call<com.app.dto.model.FriendDto>,
                    response: Response<com.app.dto.model.FriendDto>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Solicitud aceptada", Toast.LENGTH_SHORT).show()
                        loadFriendshipState()
                    } else {
                        Toast.makeText(requireContext(), "Error al aceptar solicitud", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<com.app.dto.model.FriendDto>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message ?: "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cancelRequestTo(user: UserDto) {
        val requesterId = SessionManager.userId
        if (requesterId == -1L) {
            Toast.makeText(requireContext(), "Usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.friendApi.cancelFriendRequest(requesterId, user.id)
            .enqueue(object : Callback<com.app.dto.response.ApiMessageResponse> {
                override fun onResponse(
                    call: Call<com.app.dto.response.ApiMessageResponse>,
                    response: Response<com.app.dto.response.ApiMessageResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Solicitud cancelada", Toast.LENGTH_SHORT).show()
                        loadFriendshipState()
                    } else {
                        Toast.makeText(requireContext(), "Error al cancelar solicitud", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<com.app.dto.response.ApiMessageResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message ?: "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun removeFriendship(user: UserDto) {
        val currentUserId = SessionManager.userId
        if (currentUserId == -1L) {
            Toast.makeText(requireContext(), "Usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.friendApi.removeFriendship(currentUserId, user.id)
            .enqueue(object : Callback<com.app.dto.response.ApiMessageResponse> {
                override fun onResponse(
                    call: Call<com.app.dto.response.ApiMessageResponse>,
                    response: Response<com.app.dto.response.ApiMessageResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Amistad eliminada", Toast.LENGTH_SHORT).show()
                        loadFriendshipState()
                    } else {
                        Toast.makeText(requireContext(), "Error al eliminar amistad", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<com.app.dto.response.ApiMessageResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message ?: "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadFriendshipState() {
        val userId = SessionManager.userId
        if (userId == -1L) {
            acceptedFriendUserIds = emptySet()
            pendingIncomingByUserId = emptyMap()
            pendingOutgoingByUserId = emptyMap()
            userAdapter.setAcceptedFriendUserIds(acceptedFriendUserIds)
            userAdapter.setPendingIncomingUserIds(pendingIncomingByUserId.keys)
            userAdapter.setPendingOutgoingUserIds(pendingOutgoingByUserId.keys)
            return
        }

        RetrofitClient.friendApi.getPendingRequests(userId)
            .enqueue(object : Callback<List<com.app.dto.model.FriendDto>> {
                override fun onResponse(
                    call: Call<List<com.app.dto.model.FriendDto>>,
                    response: Response<List<com.app.dto.model.FriendDto>>
                ) {
                    if (response.isSuccessful) {
                        val incoming = response.body() ?: emptyList()
                        pendingIncomingByUserId = incoming.associate { it.requesterId to it.id }
                        userAdapter.setPendingIncomingUserIds(pendingIncomingByUserId.keys)
                    }
                }

                override fun onFailure(call: Call<List<com.app.dto.model.FriendDto>>, t: Throwable) {}
            })

        RetrofitClient.friendApi.getPendingSent(userId)
            .enqueue(object : Callback<List<com.app.dto.model.FriendDto>> {
                override fun onResponse(
                    call: Call<List<com.app.dto.model.FriendDto>>,
                    response: Response<List<com.app.dto.model.FriendDto>>
                ) {
                    if (response.isSuccessful) {
                        val outgoing = response.body() ?: emptyList()
                        pendingOutgoingByUserId = outgoing.associate { it.receiverId to it.id }
                        userAdapter.setPendingOutgoingUserIds(pendingOutgoingByUserId.keys)
                    }
                }

                override fun onFailure(call: Call<List<com.app.dto.model.FriendDto>>, t: Throwable) {}
            })

        RetrofitClient.friendApi.getAcceptedFriends(userId)
            .enqueue(object : Callback<List<com.app.dto.model.FriendDto>> {
                override fun onResponse(
                    call: Call<List<com.app.dto.model.FriendDto>>,
                    response: Response<List<com.app.dto.model.FriendDto>>
                ) {
                    if (response.isSuccessful) {
                        val friends = response.body() ?: emptyList()
                        acceptedFriendUserIds = friends.mapNotNull { f ->
                            when (userId) {
                                f.requesterId -> f.receiverId
                                f.receiverId -> f.requesterId
                                else -> null
                            }
                        }.toSet()
                        userAdapter.setAcceptedFriendUserIds(acceptedFriendUserIds)
                    }
                }

                override fun onFailure(call: Call<List<com.app.dto.model.FriendDto>>, t: Throwable) {
                    // silenciar: la búsqueda de usuarios sigue funcionando
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
