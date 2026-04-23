package com.app.ui.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.dto.model.FriendDto
import com.app.network.RetrofitClient
import com.app.repository.FriendRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsViewModel : ViewModel() {

    private val friendRepository = FriendRepository(RetrofitClient.friendApi)

    private val _pendingRequests = MutableLiveData<List<FriendDto>>()
    val pendingRequests: LiveData<List<FriendDto>> = _pendingRequests

    private val _acceptedFriends = MutableLiveData<List<FriendDto>>()
    val acceptedFriends: LiveData<List<FriendDto>> = _acceptedFriends

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _actionDone = MutableLiveData<Unit>()
    val actionDone: LiveData<Unit> = _actionDone

    fun loadFriends(userId: Long) {
        if (userId == -1L) {
            _error.value = "Usuario no válido"
            return
        }

        friendRepository.getPendingRequests(userId).enqueue(object : Callback<List<FriendDto>> {
            override fun onResponse(
                call: Call<List<FriendDto>>,
                response: Response<List<FriendDto>>
            ) {
                if (response.isSuccessful) {
                    _pendingRequests.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar solicitudes pendientes"
                }
            }

            override fun onFailure(call: Call<List<FriendDto>>, t: Throwable) {
                _error.value = t.message ?: "Error de conexión"
            }
        })

        friendRepository.getAcceptedFriends(userId).enqueue(object : Callback<List<FriendDto>> {
            override fun onResponse(
                call: Call<List<FriendDto>>,
                response: Response<List<FriendDto>>
            ) {
                if (response.isSuccessful) {
                    _acceptedFriends.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar amigos"
                }
            }

            override fun onFailure(call: Call<List<FriendDto>>, t: Throwable) {
                _error.value = t.message ?: "Error de conexión"
            }
        })
    }

    fun acceptFriend(friendId: Long) {
        friendRepository.acceptFriend(friendId).enqueue(object : Callback<FriendDto> {
            override fun onResponse(call: Call<FriendDto>, response: Response<FriendDto>) {
                if (response.isSuccessful) {
                    _actionDone.value = Unit
                } else {
                    _error.value = "Error al aceptar la solicitud"
                }
            }

            override fun onFailure(call: Call<FriendDto>, t: Throwable) {
                _error.value = t.message ?: "Error de conexión"
            }
        })
    }

    fun rejectFriend(friendId: Long) {
        friendRepository.rejectFriend(friendId).enqueue(object : Callback<FriendDto> {
            override fun onResponse(call: Call<FriendDto>, response: Response<FriendDto>) {
                if (response.isSuccessful) {
                    _actionDone.value = Unit
                } else {
                    _error.value = "Error al rechazar la solicitud"
                }
            }

            override fun onFailure(call: Call<FriendDto>, t: Throwable) {
                _error.value = t.message ?: "Error de conexión"
            }
        })
    }

    fun removeFriendship(userId: Long, friendUserId: Long) {
        friendRepository.removeFriendship(userId, friendUserId).enqueue(object : Callback<com.app.dto.response.ApiMessageResponse> {
            override fun onResponse(
                call: Call<com.app.dto.response.ApiMessageResponse>,
                response: Response<com.app.dto.response.ApiMessageResponse>
            ) {
                if (response.isSuccessful) {
                    _actionDone.value = Unit
                } else {
                    _error.value = "Error al eliminar la amistad"
                }
            }

            override fun onFailure(call: Call<com.app.dto.response.ApiMessageResponse>, t: Throwable) {
                _error.value = t.message ?: "Error de conexión"
            }
        })
    }
}
