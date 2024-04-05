package com.example.rapidcar_v01.view.profileButton

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rapidcar_v01.R
import com.example.rapidcar_v01.api.ApiInterface
import com.example.rapidcar_v01.databinding.ActivityReclamosSugerenciasBinding
import com.example.rapidcar_v01.utils.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class ReclamosSugerenciasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReclamosSugerenciasBinding
    private lateinit var apiInterface: ApiInterface
    private var selectedImageUri: Uri? = null

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val imageUri: Uri? = data.data
                    if (imageUri != null) {
                        selectedImageUri = imageUri
                        binding.ImgReclamo.setImageURI(imageUri)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReclamosSugerenciasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiInterface = RetrofitInstance.api

        binding.btnSeleccionarImagenReclamo.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
                )
            } else {
                openImagePicker()
            }
        }

        binding.btnEnviarReclamoSugerencia.setOnClickListener {
            val mensaje = binding.edtReclamo.text.toString()
            val mensajeRequestBody = mensaje.toRequestBody("text/plain".toMediaTypeOrNull())

            var imgPart: MultipartBody.Part? = null
            if (selectedImageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                val imageData = baos.toByteArray()
                val imgRequestBody = imageData.toRequestBody("image/*".toMediaTypeOrNull())
                imgPart = MultipartBody.Part.createFormData("img", "image.jpg", imgRequestBody)
            }

            val call = apiInterface.ReclamoSugerencia(mensajeRequestBody, imgPart)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ReclamosSugerenciasActivity,
                            "Reclamo enviado con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ReclamosSugerenciasActivity,
                            "Error al enviar el reclamo", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ReclamosSugerenciasActivity,
                        "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    companion object {
        private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1
    }
}