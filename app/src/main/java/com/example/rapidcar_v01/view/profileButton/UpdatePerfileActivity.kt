package com.example.rapidcar_v01.view.profileButton

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.rapidcar_v01.R
import com.example.rapidcar_v01.databinding.ActivityUpdatePerfileBinding
import com.example.rapidcar_v01.modelo.AutoResponses
import com.example.rapidcar_v01.modelo.Usuario
import com.example.rapidcar_v01.modelo.UsuarioUpdate
import com.example.rapidcar_v01.tokenmanager.SharedPreferencesManager
import com.example.rapidcar_v01.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.ByteArrayOutputStream

class UpdatePerfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePerfileBinding
    private var selectedImageUri: Uri? = null

    private lateinit var img: ImageView
    private lateinit var nombreupdate: EditText
    private lateinit var apellido_paterno: EditText
    private lateinit var apellido_materno: EditText
    private lateinit var correo_electronico: EditText
    private lateinit var celular: EditText
    private lateinit var password: EditText
    private lateinit var username: EditText
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var usuario: UsuarioUpdate
    private lateinit var animacion : ImageView

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val imageUri: Uri? = data.data
                    if (imageUri != null) {
                        //Image selected successfully, you can use 'imageUri' as needed
                        selectedImageUri = imageUri
                        //You can also update the image view if necessary
                        val imageViewFotoPerfil =
                            findViewById<ImageView>(R.id.ImgUserUpdate)
                        imageViewFotoPerfil.setImageURI(imageUri)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePerfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa RetrofitInstance aquí
        RetrofitInstance.initialize(this)

        binding.btnSelecionarImgUpdate.setOnClickListener{
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

        binding.btnUpdateUser.setOnClickListener{
            updateUser()
        }

        sharedPreferencesManager = SharedPreferencesManager(this)


        img = binding.ImgUserUpdate
        nombreupdate = binding.txtNombreUpdate
        apellido_paterno = binding.txtPaternoUpdate
        apellido_materno = binding.txtMaternoUpdate
        correo_electronico = binding.txtEmailUpdate
        celular = binding.txtPhoneUpdate
        password = binding.txtPasswordUpdate
        username = binding.txtUsernameUpdate
        animacion = findViewById(R.id.imageViewCarga)

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e("UserPerfile", "Error en la coroutine", exception)
        }

        //Obtener el token de SharedPreferencesManager
        val token = sharedPreferencesManager.fetchAuthToken()

        if (token != null) {
            val api = RetrofitInstance.api

            CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
                Glide.with(applicationContext)
                    .asGif()
                    .load(R.drawable.loading)
                    .into(animacion)
                try {
                    //Hacer la llamada a la API con el token en el encabezado de autorización
                    val response: Response<AutoResponses<Usuario>> =
                        api.getusuario()

                    if (response.isSuccessful) {
                        val userResponses: AutoResponses<Usuario>? = response.body()

                        if (userResponses != null && userResponses.estado == "OK") {
                            val selectedUser: Usuario? = userResponses.data

                            if (selectedUser != null) {
                                //Imprimir la respuesta completa de la API en la consola
                                Log.d("UserPerfilFragment", "Respuesta de la API: $userResponses")

                                //Imprimir los datos del usuario en la consola
                                Log.d("UserPerfilFragment", "Datos del Usuario: $selectedUser")

                                fillUserDetails(selectedUser)
                                setupViewPager(selectedUser)
                            } else {
                            }
                        } else {
                            Log.e(
                                "UserPerfileFragment",
                                "Error en la respuesta: ${userResponses?.mensaje ?: "Error desconocido"}"
                            )
                        }
                    } else {
                        Log.e(
                            "UserPerfileFragment",
                            "Error al obtener los detalles del usuario: ${response.message()}"
                        )
                    }
                } catch (e: Exception) {
                    Log.e("UserPerfileFragment", "Error al realizar la llamada a la API", e)
                } finally {
                    //Ocultar animación de carga después de completar la operación
                    animacion.visibility = View.GONE
                }
            }
        }
    }

    private fun fillUserDetails(user: Usuario) {

        nombreupdate.text = Editable.Factory.getInstance().newEditable(user.nombre)
        apellido_paterno.text = Editable.Factory.getInstance().newEditable(user.apellidoPaterno)
        apellido_materno.text = Editable.Factory.getInstance().newEditable(user.apellidoMaterno)
        correo_electronico.text = Editable.Factory.getInstance().newEditable(user.correoElectronico)
        celular.text = Editable.Factory.getInstance().newEditable(user.celular.toString())
        password.text = Editable.Factory.getInstance().newEditable(user.contrasena)
        username.text = Editable.Factory.getInstance().newEditable(user.username)

    }

    private fun setupViewPager(user: Usuario) {
        //Decodificar la imagen en Base64
        val decodedImageBytes = Base64.decode(user.img, Base64.DEFAULT)
        val decodedBitmap =
            BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.size)

        //Establecer la imagen decodificada en el ImageView
        img.setImageBitmap(decodedBitmap)
    }

    private fun updateUser() {

        val celularupdate = findViewById<EditText>(R.id.txtPhoneUpdate).text.toString()


        usuario = UsuarioUpdate(
            apellido_materno.text.toString(),
            apellido_paterno.text.toString(),
            celularupdate.trim().toInt(),
            password.text.toString(),
            correo_electronico.text.toString().trim(),
            "",
            nombreupdate.text.toString(),
            username.text.toString()
        )



        if (selectedImageUri != null) {
            //Get the bitmap of the selected file
            var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)

            //Reduce the size of the image
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
            updateuser(usuario, imagePart)
        } else {
            // If no new image selected, use the current user image obtained from setupViewPager
            val currentUserImageBitmap = (img.drawable as BitmapDrawable).bitmap
            val outputStream = ByteArrayOutputStream()
            currentUserImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val imageBytes = outputStream.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            // Create RequestBody instance from byte array
            val imageRequestBody = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())

            // Create MultipartBody.Part from RequestBody
            val imagePart = MultipartBody.Part.createFormData("img", "avatar.jpg", imageRequestBody)

            // Call the function that makes the API call
            updateuser(usuario, imagePart)
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



    private fun updateuser(usuario: UsuarioUpdate, img: MultipartBody.Part?) {
        animacion.visibility = View.VISIBLE
        Log.d("API_REQUEST", "Usuario: $usuario")
        Log.d("API_REQUEST", "Nombre: ${usuario.nombre}")
        Log.d("API_REQUEST", "Apellido Paterno: ${usuario.apellido_Paterno}")
        Log.d("API_REQUEST", "Apellido Materno: ${usuario.apellido_Materno}")
        Log.d("API_REQUEST", "Correo Electrónico: ${usuario.correoElectronico}")
        Log.d("API_REQUEST", "Celular: ${usuario.celular}")
        Log.d("API_REQUEST", "Username: ${usuario.username}")
        Log.d("API_REQUEST", "Contraseña: ${usuario.contrasena}")


        //Obtener el token de SharedPreferencesManager
        val token = sharedPreferencesManager.fetchAuthToken()

        if (token != null) {
            val api = RetrofitInstance.api

            val call = api.ActualizarUsuario(
                img,
                usuario.nombre.toRequestBody("text/pain".toMediaTypeOrNull()),
                usuario.apellido_Paterno.toRequestBody("text/pain".toMediaTypeOrNull()),
                usuario.apellido_Materno.toRequestBody("text/pain".toMediaTypeOrNull()),
                usuario.correoElectronico.toRequestBody("text/pain".toMediaTypeOrNull()),
                usuario.contrasena.toRequestBody("text/pain".toMediaTypeOrNull()),
                usuario.celular.toString().toRequestBody("text/pain".toMediaTypeOrNull()),
                usuario.username.toRequestBody("text/pain".toMediaTypeOrNull()),
            )

            Log.d("UserPerfileFragment", "Datos del usuario a enviar: $usuario")

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@UpdatePerfileActivity,
                            "Usuario actualizado exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        animacion.visibility = View.GONE
                    } else {
                        // Capture the response body when the status code is 403
                        if (response.code() == 403) {
                            val errorBody = response.errorBody()?.string()
                            Log.e("API_CALL_ERROR", "Error 403: $errorBody")
                        }
                        Toast.makeText(
                            this@UpdatePerfileActivity,
                            "Error en el actualizado: ${response.message()}",
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
                        this@UpdatePerfileActivity,
                        "Error al enviar la petición: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }
    companion object {
        private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1
    }
}

