package com.example.rapidcar_v01.view.profileButton

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.rapidcar_v01.R
import com.example.rapidcar_v01.databinding.ActivityLoginBinding
import com.example.rapidcar_v01.modelo.LoginRequest
import com.example.rapidcar_v01.tokenmanager.SharedPreferencesManager
import com.example.rapidcar_v01.utils.RetrofitInstance
import com.example.rapidcar_v01.view.dashboardButton.AgregarActualizarAutoActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var animacion : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animacion = findViewById(R.id.imageViewCarga)

        binding.btnLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            loginUser(username, password)
        }

        //Texto de registro new user
        binding.textViewRegister.setOnClickListener {
            val createUserActivity = Intent(this, CreateUserActivity::class.java)
            startActivity(createUserActivity)
        }

        Glide.with(applicationContext)
            .asGif()
            .load(R.drawable.loading)
            .into(animacion)

    }

    private fun loginUser(username: String, password: String) {
        val api = RetrofitInstance.api
        val loginRequest = LoginRequest(username, password)

        CoroutineScope(Dispatchers.Main).launch {
            binding.imageViewCarga.visibility = View.VISIBLE
            try {
                val response = api.login(loginRequest)
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        val sharedPreferencesManager = SharedPreferencesManager(this@LoginActivity)
                        sharedPreferencesManager.saveAuthToken(token)
                        showToast("Usuario logeado con éxito")
                        animacion.visibility = View.GONE
                        // Llamar al método para registrar el auto
                        // registrarAuto()
                    } else {
                        showToast("Token de acceso vacío")
                        animacion.visibility = View.GONE
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    showToast("Error en el inicio de sesión: $errorBody")
                    animacion.visibility = View.GONE
                }
            } catch (e: HttpException) {
                showToast("Error en la red o del servidor: ${e.message}")
                animacion.visibility = View.GONE
            } catch (e: Throwable) {
                showToast("Error inesperado: ${e.message}")
                animacion.visibility = View.GONE
            } finally {
                // Ocultar animación de carga después de completar la operación
                binding.imageViewCarga.visibility = View.GONE
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}