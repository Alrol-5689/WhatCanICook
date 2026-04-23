package com.app.ui.users.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.dto.model.UserDto
import com.app.network.RetrofitClient
import com.app.repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserSearchViewModel : ViewModel() {

    private val userRepository = UserRepository(RetrofitClient.userApi)

    private val _users = MutableLiveData<List<UserDto>>()
    val users: LiveData<List<UserDto>> = _users

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun searchUsers(query: String) {
        val clean = query.trim()
        if (clean.isBlank()) {
            _users.value = emptyList()
            return
        }

        userRepository.searchUsers(clean).enqueue(object : Callback<List<UserDto>> {
            override fun onResponse(call: Call<List<UserDto>>, response: Response<List<UserDto>>) {
                if (response.isSuccessful) {
                    _users.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al buscar usuarios"
                }
            }

            override fun onFailure(call: Call<List<UserDto>>, t: Throwable) {
                _error.value = t.message ?: "Error de conexión"
            }
        })
    }
}

