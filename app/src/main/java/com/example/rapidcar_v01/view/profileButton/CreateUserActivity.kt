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
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

import android.util.Base64
import android.view.View
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream

class CreateUserActivity : AppCompatActivity() {

    private lateinit var apiInterface: ApiInterface
    private var selectedImageUri: Uri? = null
    private lateinit var usuario: Idusuario
    private lateinit var animacion : ImageView

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val imageUri: Uri? = data.data
                    if (imageUri != null) {

                        selectedImageUri = imageUri

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

        animacion = findViewById(R.id.imageViewCarga)

        RetrofitInstance.initialize(this)

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

        Glide.with(applicationContext)
            .asGif()
            .load(R.drawable.loading)
            .into(animacion)

        val buttonCrearUsuario = findViewById<Button>(R.id.buttonCrearUsuarioNewUser)
        buttonCrearUsuario.setOnClickListener {
            createUser()
        }
    }

    private fun createUser() {

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

        val idRol = IdRol("Rol Predeterminado", 3)

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


        if (selectedImageUri != null) {
            var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)

            val maxImageSize =
                1024
            val scaleFactor = maxImageSize.toDouble() / maxOf(bitmap.width, bitmap.height)
            val newWidth = (bitmap.width * scaleFactor).toInt()
            val newHeight = (bitmap.height * scaleFactor).toInt()
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                90,
                byteArrayOutputStream
            ) //Changed to JPEG and quality set to 90
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

            //Convert Base64 string back to byte array
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)

            //Create RequestBody instance from byte array
            val imageRequestBody = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())

            //Create MultipartBody.Part from RequestBody
            val imagePart = MultipartBody.Part.createFormData("img", "avatar.jpg", imageRequestBody)

            //Call the function that makes the API call
            registrarUsuario(usuario, imagePart)
        } else {

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
                    //Disable the button if the permission is denied
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



    private fun registrarUsuario(usuario: Idusuario, img: MultipartBody.Part?) {
        animacion.visibility = View.VISIBLE
        Log.d("API_REQUEST", "Usuario: $usuario")
        Log.d("API_REQUEST", "Nombre: ${usuario.nombre}")
        Log.d("API_REQUEST", "Apellido Paterno: ${usuario.apellido_Paterno}")
        Log.d("API_REQUEST", "Apellido Materno: ${usuario.apellido_Materno}")
        Log.d("API_REQUEST", "Correo Electrónico: ${usuario.correoElectronico}")
        Log.d("API_REQUEST", "Celular: ${usuario.celular}")
        Log.d("API_REQUEST", "Pais: ${usuario.pais}")
        Log.d("API_REQUEST", "Username: ${usuario.username}")
        Log.d("API_REQUEST", "Contraseña: ${usuario.contrasena}")



        //API call
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

        Log.d("API_REQUEST", "URL: ${call.request().url}")
        Log.d("API_REQUEST", "Method: ${call.request().method}")
        Log.d("API_REQUEST", "Headers: ${call.request().headers}")
        Log.d("API_REQUEST", "Body: ${call.request().body}")

        //Handle API response
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@CreateUserActivity,
                        "Usuario registrado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    animacion.visibility = View.GONE
                } else {
                    // Capture the response body when the status code is 403
                    if (response.code() == 403) {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_CALL_ERROR", "Error 403: $errorBody")
                        animacion.visibility = View.GONE
                    }
                    Toast.makeText(
                        this@CreateUserActivity,
                        "Error en el registro: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    animacion.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API_CALL_ERROR", "Error de conexión: ${t.message}")
                animacion.visibility = View.GONE

                if (t is HttpException) {
                    Log.e("API_CALL_ERROR", "Código de respuesta: ${t.code()}")
                    animacion.visibility = View.GONE
                }

                //Show a Toast with the error message
                Toast.makeText(
                    this@CreateUserActivity,
                    "Error al enviar la petición: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
                animacion.visibility = View.GONE
            }
        })
    }



    companion object {
        private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1
    }
}
