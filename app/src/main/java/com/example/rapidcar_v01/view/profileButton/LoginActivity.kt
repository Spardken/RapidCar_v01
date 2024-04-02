package com.example.rapidcar_v01.view.profileButton

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configura el OnClickListener para el botón de inicio de sesión
        binding.btnLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            //Realiza el inicio de sesión
            loginUser(username, password)
        }

        //Configura el OnClickListener para el TextView para abrir la actividad de registro
        binding.textViewRegister.setOnClickListener {
            val createUserActivity = Intent(this, CreateUserActivity::class.java)
            startActivity(createUserActivity)
        }

    }

    private fun loginUser(username: String, password: String) {
        val api = RetrofitInstance.api
        val loginRequest = LoginRequest(username, password)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = api.login(loginRequest)
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        val sharedPreferencesManager = SharedPreferencesManager(this@LoginActivity)
                        sharedPreferencesManager.saveAuthToken(token)
                        showToast("Usuario logeado con éxito")

                        // Llamar al método para registrar el auto
                        // registrarAuto()
                    } else {
                        showToast("Token de acceso vacío")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    showToast("Error en el inicio de sesión: $errorBody")
                }
            } catch (e: HttpException) {
                showToast("Error en la red o del servidor: ${e.message}")
            } catch (e: Throwable) {
                showToast("Error inesperado: ${e.message}")
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

   /* private fun registrarAuto() {
        // Llamar a la actividad para agregar o actualizar un vehículo
        val intent = Intent(this, AgregarActualizarAutoActivity::class.java)
        startActivity(intent)
    }*/
}