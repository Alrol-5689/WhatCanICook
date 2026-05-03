package com.app.ui.profile

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import coil.load
import com.app.R
import com.app.databinding.ActivityProfileBinding
import com.app.network.RetrofitClient
import com.app.ui.main.MainActivity
import com.app.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.appbar.MaterialToolbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.max

class ProfileFragment : Fragment() {

    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!

    private var pendingCameraUri: Uri? = null

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                launchCamera()
            } else {
                Toast.makeText(requireContext(), getString(R.string.permiso_camara_denegado), Toast.LENGTH_SHORT).show()
            }
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            val uri = pendingCameraUri
            pendingCameraUri = null
            if (success && uri != null) {
                uploadFromUri(uri)
            }
        }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            uploadFromUri(uri)
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
            showPhotoPicker()
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

    private fun showPhotoPicker() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.cambiar_foto))
            .setItems(arrayOf(getString(R.string.galeria), getString(R.string.camara))) { _, which ->
                when (which) {
                    0 -> pickImage.launch("image/*")
                    1 -> ensureCameraPermissionAndLaunch()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun ensureCameraPermissionAndLaunch() {
        if (!SessionManager.isLoggedIn()) {
            Toast.makeText(requireContext(), "Haz login para cambiar la foto", Toast.LENGTH_SHORT).show()
            return
        }

        val granted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (granted) {
            launchCamera()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val imagesDir = File(requireContext().cacheDir, "images").apply { mkdirs() }
        val file = File.createTempFile("profile_", ".jpg", imagesDir)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
        pendingCameraUri = uri
        takePicture.launch(uri)
    }

    private fun uploadFromUri(uri: Uri) {
        if (!SessionManager.isLoggedIn()) {
            Toast.makeText(requireContext(), "Haz login para cambiar la foto", Toast.LENGTH_SHORT).show()
            return
        }

        val bytes = getCompressedJpegBytes(uri)
        if (bytes == null || bytes.isEmpty()) {
            Toast.makeText(requireContext(), "No se pudo leer la imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", "profile.jpg", requestBody)

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

    private fun getCompressedJpegBytes(uri: Uri): ByteArray? {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        requireContext().contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }

        if (options.outWidth <= 0 || options.outHeight <= 0) return null

        val maxSize = 1024
        val sampleSize = calculateSampleSize(options.outWidth, options.outHeight, maxSize)
        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val decoded = requireContext().contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, decodeOptions)
        } ?: return null

        val scaled = scaleBitmap(decoded, maxSize)
        val output = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, output)

        if (scaled != decoded) scaled.recycle()
        decoded.recycle()

        return output.toByteArray()
    }

    private fun calculateSampleSize(width: Int, height: Int, maxSize: Int): Int {
        var sampleSize = 1
        while (width / sampleSize > maxSize * 2 || height / sampleSize > maxSize * 2) {
            sampleSize *= 2
        }
        return sampleSize
    }

    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val largestSide = max(bitmap.width, bitmap.height)
        if (largestSide <= maxSize) return bitmap

        val scale = maxSize.toFloat() / largestSide
        val width = (bitmap.width * scale).toInt()
        val height = (bitmap.height * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}
