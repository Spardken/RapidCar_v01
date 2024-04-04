package com.example.rapidcar_v01.view.fragments

import HomeFragment
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.rapidcar_v01.R
import com.example.rapidcar_v01.api.ApiInterface
import com.example.rapidcar_v01.modelo.AutoResponses
import com.example.rapidcar_v01.modelo.Usuario
import com.example.rapidcar_v01.tokenmanager.SharedPreferencesManager
import com.example.rapidcar_v01.utils.RetrofitInstance
import com.example.rapidcar_v01.view.profileButton.LoginActivity
import com.example.rapidcar_v01.view.profileButton.UpdatePerfileActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


class UserPerfileFragment : AppCompatActivity() {

    private lateinit var img: ImageView
    private lateinit var nombreTextView: TextView
    private lateinit var apellidopaternoTextView: TextView
    private lateinit var apellidomaternoTextView: TextView
    private lateinit var correoelectronicoTextView: TextView
    private lateinit var celularTextView: TextView
    private lateinit var usernameTextView: TextView

    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_perfile_fragment)

        // Inicializa RetrofitInstance aquí
        RetrofitInstance.initialize(this)

        sharedPreferencesManager = SharedPreferencesManager(this)

        img = findViewById(R.id.imgFotoPerfil)
        nombreTextView = findViewById(R.id.editTextNombre)
        apellidopaternoTextView = findViewById(R.id.editTextApellidoPaterno)
        apellidomaternoTextView = findViewById(R.id.editTextApellidoMaterno)
        correoelectronicoTextView = findViewById(R.id.editTextCorreoElectronico)
        celularTextView = findViewById(R.id.editTextCelular)
        usernameTextView = findViewById(R.id.editTextUsername)
        btnEdit = findViewById(R.id.btnUpdatePerfile)
        btnDelete = findViewById(R.id.btnEliminarPerfile)

        btnEdit.setOnClickListener {
            val intent = Intent(applicationContext, UpdatePerfileActivity::class.java)
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            eliminar()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }


        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e("UserPerfile", "Error en la coroutine", exception)
        }

        // Obtener el token de SharedPreferencesManager
        val token = sharedPreferencesManager.fetchAuthToken()

        if (token != null) {
            val api = RetrofitInstance.api

            CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
                try {
                    // Hacer la llamada a la API con el token en el encabezado de autorización
                    val response: Response<AutoResponses<Usuario>> =
                        api.getusuario()



                    if (response.isSuccessful) {
                        val userResponses: AutoResponses<Usuario>? = response.body()

                        if (userResponses != null && userResponses.estado == "OK") {
                            val selectedUser: Usuario? = userResponses.data

                            if (selectedUser != null) {
                                // Imprimir la respuesta completa de la API en la consola
                                Log.d("UserPerfilFragment", "Respuesta de la API: $userResponses")

                                // Imprimir los datos del usuario en la consola
                                Log.d("UserPerfilFragment", "Datos del Usuario: $selectedUser")

                                fillUserDetails(selectedUser)
                                setupViewPager(selectedUser)
                            } else {
                                // Manejar el caso de datos nulos
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
                }
            }
        }
    }

    private fun printUserDetails(user: Usuario) {
        user ?: return
        Log.d("DetalleUsuario", "Nombre: ${user.nombre}")
        Log.d("DetalleUsuario", "Apellido Paterno: ${user.apellidoPaterno}")
        Log.d("DetalleUsuario", "Apellido Materno: ${user.apellidoMaterno}")
        Log.d("DetalleUsuario", "Correo Electrónico: ${user.correoElectronico}")
        Log.d("DetalleUsuario", "Celular: ${user.celular}")
        Log.d("DetalleUsuario", "Username: ${user.username}")
    }

    private fun fillUserDetails(user: Usuario) {
        nombreTextView.text = "Nombre: ${user.nombre}"
        apellidopaternoTextView.text = "Apellido Paterno: ${user.apellidoPaterno}"
        apellidomaternoTextView.text = "Apellido Materno: ${user.apellidoMaterno}"
        correoelectronicoTextView.text = "Correo Electrónico: ${user.correoElectronico}"
        celularTextView.text = "Celular: ${user.celular}"
        usernameTextView.text = "Username: ${user.username}"
    }

    private fun setupViewPager(user: Usuario) {
        // Decodificar la imagen en Base64
        val decodedImageBytes = Base64.decode(user.img, Base64.DEFAULT)
        val decodedBitmap =
            BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.size)

        // Establecer la imagen decodificada en el ImageView
        img.setImageBitmap(decodedBitmap)
    }

    private fun eliminar() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e("UserPerfile", "Error en la coroutine", exception)
        }
        // Obtener el token de SharedPreferencesManager
        val token = sharedPreferencesManager.fetchAuthToken()

        if (token != null) {
            val api = RetrofitInstance.api

            CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
                try {
                    // Hacer la llamada a la API con el token en el encabezado de autorización
                    val response: Response<AutoResponses<Usuario>> = api.eliminarUsuario()

                    if (response.isSuccessful) {
                        val userResponses: AutoResponses<Usuario>? = response.body()

                        if (userResponses != null && userResponses.estado == "OK") {
                            val selectedUser: Usuario? = userResponses.data
                            // Mostrar un mensaje de éxito
                            Toast.makeText(applicationContext, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Manejar caso en el que la respuesta no fue exitosa
                        Toast.makeText(applicationContext, "No se pudo eliminar el usuario", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Manejar cualquier excepción
                    Toast.makeText(applicationContext, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}



