package com.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import com.app.R
import com.app.databinding.ActivityProfileBinding
import com.app.network.RetrofitClient
import com.app.ui.main.MainActivity
import com.app.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            if (!SessionManager.isLoggedIn()) {
                Toast.makeText(requireContext(), "Haz login para cambiar la foto", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val bytes = requireContext().contentResolver.openInputStream(uri)?.use { it.readBytes() }
            if (bytes == null || bytes.isEmpty()) {
                Toast.makeText(requireContext(), "No se pudo leer la imagen", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val mime = requireContext().contentResolver.getType(uri) ?: "image/*"
            val requestBody = bytes.toRequestBody(mime.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", "profile", requestBody)

            binding.changePhotoButton.isEnabled = false
            RetrofitClient.userApi.uploadProfileImage(SessionManager.userId, part)
                .enqueue(object : Callback<com.app.dto.model.UserDto> {
                    override fun onResponse(
                        call: Call<com.app.dto.model.UserDto>,
                        response: Response<com.app.dto.model.UserDto>
                    ) {
                        binding.changePhotoButton.isEnabled = true
                        if (response.isSuccessful && response.body() != null) {
                            val user = response.body()!!
                            SessionManager.profileImageUrl = user.profileImageUrl
                            renderProfileImage()
                            (requireActivity() as? MainActivity)?.refreshDrawerHeader()
                            Toast.makeText(requireContext(), "Foto actualizada", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Error subiendo foto", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<com.app.dto.model.UserDto>, t: Throwable) {
                        binding.changePhotoButton.isEnabled = true
                        Toast.makeText(requireContext(), t.message ?: "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                })
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.profileUsernameInput.setText(SessionManager.username ?: "")
        binding.profileEmailInput.setText(SessionManager.email ?: "")
        binding.profileUsernameInput.isEnabled = false
        binding.saveProfileButton.isEnabled = false

        renderProfileImage()

        binding.changePhotoButton.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderProfileImage() {
        binding.profileImageLarge.load(SessionManager.getAbsoluteProfileImageUrl()) {
            placeholder(R.drawable.ic_profile_placeholder)
            error(R.drawable.ic_profile_placeholder)
        }
    }
}
