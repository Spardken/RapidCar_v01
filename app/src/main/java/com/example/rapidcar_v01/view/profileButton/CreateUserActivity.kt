package com.example.rapidcar_v01.view.profileButton

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rapidcar_v01.R
import com.example.rapidcar_v01.api.ApiInterface
import com.example.rapidcar_v01.modelo.IdRol
import com.example.rapidcar_v01.modelo.Idusuario
import com.example.rapidcar_v01.utils.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

import android.util.Base64
import java.io.ByteArrayOutputStream

class CreateUserActivity : AppCompatActivity() {

    private lateinit var apiInterface: ApiInterface
    private var selectedImageUri: Uri? = null
    private lateinit var usuario: Idusuario

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val imageUri: Uri? = data.data
                    if (imageUri != null) {
                        // Image selected successfully, you can use 'imageUri' as needed
                        selectedImageUri = imageUri
                        // You can also update the image view if necessary
                        val imageViewFotoPerfil =
                            findViewById<ImageView>(R.id.imageViewFotoPerfilNewUser)
                        imageViewFotoPerfil.setImageURI(imageUri)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        apiInterface = RetrofitInstance.api

        val buttonSelectImage = findViewById<Button>(R.id.buttonSelectImageNewUser)
        buttonSelectImage.setOnClickListener {
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

        val buttonCrearUsuario = findViewById<Button>(R.id.buttonCrearUsuarioNewUser)
        buttonCrearUsuario.setOnClickListener {
            createUser()
        }
    }

    private fun createUser() {
        // Obtain values from EditText and other fields
        val nombre = findViewById<EditText>(R.id.editTextNombreNewUser).text.toString()
        val apellidoPaterno =
            findViewById<EditText>(R.id.editTextApellidoPaternoNewUser).text.toString()
        val apellidoMaterno =
            findViewById<EditText>(R.id.editTextApellidoMaternoNewUser).text.toString()
        val correoElectronico =
            findViewById<EditText>(R.id.editTextCorreoElectronicoNewUser).text.toString()
        val celular = findViewById<EditText>(R.id.editTextCelularNewUser).text.toString()
        val pais = findViewById<EditText>(R.id.editTextPaisNewUser).text.toString()
        val username = findViewById<EditText>(R.id.editTextUsernameNewUser).text.toString()
        val contrasena =
            findViewById<EditText>(R.id.editTextContrasenaNewUser).text.toString()

        // Create an IdRol object with the default value
        val idRol = IdRol("Rol Predeterminado", 3)

        // Initialize the usuario variable with the collected information
        usuario = Idusuario(
            apellidoMaterno,
            apellidoPaterno,
            celular.toInt(),
            contrasena,
            correoElectronico,
            "Activo",
            idRol,
            0,
            "",
            nombre,
            pais,
            username
        )

        // Check if an image has been selected
        // Check if an image has been selected
        if (selectedImageUri != null) {
            // Get the bitmap of the selected file
            var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)

            // Reduce the size of the image
            val maxImageSize =
                1024 // Maximum dimension (height or width) of the resized image in pixels
            val scaleFactor = maxImageSize.toDouble() / maxOf(bitmap.width, bitmap.height)
            val newWidth = (bitmap.width * scaleFactor).toInt()
            val newHeight = (bitmap.height * scaleFactor).toInt()
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                90,
                byteArrayOutputStream
            ) // Changed to JPEG and quality set to 90
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

            // Convert Base64 string back to byte array
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)

            // Create RequestBody instance from byte array
            val imageRequestBody = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())

            // Create MultipartBody.Part from RequestBody
            val imagePart = MultipartBody.Part.createFormData("img", "avatar.jpg", imageRequestBody)

            // Call the function that makes the API call
            registrarUsuario(usuario, imagePart)
        } else {
            // If no image selected, create a multipart body without image
            // Call the function that makes the API call
            registrarUsuario(usuario, null)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker()
                } else {
                    Toast.makeText(
                        this,
                        "Permiso denegado. Necesitamos el permiso para seleccionar una imagen de la galería.",
                        Toast.LENGTH_LONG
                    ).show()
                    // Disable the button if the permission is denied
                    val buttonSelectImage = findViewById<Button>(R.id.buttonSelectImageNewUser)
                    buttonSelectImage.isEnabled = false
                }
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    /*private fun createMultipartBody(img: MultipartBody.Part?): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("nombre", usuario.nombre)
            .addFormDataPart("apellido_Paterno", usuario.apellido_Paterno)
            .addFormDataPart("apellido_Materno", usuario.apellido_Materno)
            .addFormDataPart("correo_electronico", usuario.correoElectronico)
            .addFormDataPart("celular", usuario.celular.toString())
            .addFormDataPart("pais", usuario.pais)
            .addFormDataPart("username", usuario.username)
            .addFormDataPart("contrasena", usuario.contrasena)

        // Check if an image was selected
        if (img != null) {
            builder.addPart(img)
        }

        return builder.build()
    }*/

    /*private fun getRealPathFromURI(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
        val filePath = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return filePath
    }*/

    private fun registrarUsuario(usuario: Idusuario, img: MultipartBody.Part?) {
        // Log user fields
        Log.d("API_REQUEST", "Usuario: $usuario")
        Log.d("API_REQUEST", "Nombre: ${usuario.nombre}")
        Log.d("API_REQUEST", "Apellido Paterno: ${usuario.apellido_Paterno}")
        Log.d("API_REQUEST", "Apellido Materno: ${usuario.apellido_Materno}")
        Log.d("API_REQUEST", "Correo Electrónico: ${usuario.correoElectronico}")
        Log.d("API_REQUEST", "Celular: ${usuario.celular}")
        Log.d("API_REQUEST", "Pais: ${usuario.pais}")
        Log.d("API_REQUEST", "Username: ${usuario.username}")
        Log.d("API_REQUEST", "Contraseña: ${usuario.contrasena}")

        // Create the multipart body
        //val multipartBody = createMultipartBody(img)

        // Make the API call
        val call = apiInterface.registrarUsuario(
            img,
            usuario.nombre.toRequestBody("text/plain".toMediaTypeOrNull()),
            usuario.apellido_Paterno.toRequestBody("text/plain".toMediaTypeOrNull()),
            usuario.apellido_Materno.toRequestBody("text/plain".toMediaTypeOrNull()),
            usuario.correoElectronico.toRequestBody("text/plain".toMediaTypeOrNull()),
            usuario.contrasena.toRequestBody("text/plain".toMediaTypeOrNull()),
            usuario.celular.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            usuario.username.toRequestBody("text/plain".toMediaTypeOrNull()),
            usuario.pais.toRequestBody("text/plain".toMediaTypeOrNull())
        )

        // Log URL, method, headers, and request body
        Log.d("API_REQUEST", "URL: ${call.request().url}")
        Log.d("API_REQUEST", "Method: ${call.request().method}")
        Log.d("API_REQUEST", "Headers: ${call.request().headers}")
        Log.d("API_REQUEST", "Body: ${call.request().body}")

        // Handle API response
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@CreateUserActivity,
                        "Usuario registrado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Capture the response body when the status code is 403
                    if (response.code() == 403) {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_CALL_ERROR", "Error 403: $errorBody")
                    }
                    Toast.makeText(
                        this@CreateUserActivity,
                        "Error en el registro: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API_CALL_ERROR", "Error de conexión: ${t.message}")

                if (t is HttpException) {
                    Log.e("API_CALL_ERROR", "Código de respuesta: ${t.code()}")
                }

                // Show a Toast with the error message
                Toast.makeText(
                    this@CreateUserActivity,
                    "Error al enviar la petición: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /*private fun createImagePart(imagenRequestBody: okhttp3.RequestBody?): MultipartBody.Part? {
        if (imagenRequestBody != null) {
            return MultipartBody.Part.createFormData("img", "avatar.jpg", imagenRequestBody)
        }
        return null
    }*/

    companion object {
        private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1
    }
}
