package com.example.rapidcar_v01.view.homeButton

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.rapidcar_v01.R
import com.example.rapidcar_v01.adapters.ImageCarruselAdapterList
import com.example.rapidcar_v01.api.ApiInterface
import com.example.rapidcar_v01.modelo.AutoResponses
import com.example.rapidcar_v01.modelo.DataAuto
import com.example.rapidcar_v01.tokenmanager.SharedPreferencesManager
import com.example.rapidcar_v01.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class DetalleAutoActivity : AppCompatActivity() {

    // Declaración de las vistas
    private lateinit var descripcionTextView: TextView
    private lateinit var estadoTextView: TextView
    private lateinit var kilometrajeTextView: TextView
    private lateinit var marcaTextView: TextView
    private lateinit var modeloTextView: TextView
    private lateinit var motorTextView: TextView
    private lateinit var paisTextView: TextView
    private lateinit var precioTextView: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var idAutoTextView: TextView
    private lateinit var usernameSellerTextView: TextView

    private lateinit var animacion: ImageView

    private lateinit var edtSendMessage: EditText
    private lateinit var btnSendMessage: Button

    private lateinit var apiInterface: ApiInterface

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_auto)

        // Inicializar SharedPreferencesManager
        sharedPreferencesManager = SharedPreferencesManager(this)


        // Inicializar la instancia de la interfaz API utilizando RetrofitInstance
        apiInterface = RetrofitInstance.api

        // Inicializar las vistas
        idAutoTextView = findViewById(R.id.textViewIdAuto)
        usernameSellerTextView = findViewById(R.id.textViewUsernameSeller)
        descripcionTextView = findViewById(R.id.textViewDescripcion)
        estadoTextView = findViewById(R.id.textViewEstado)
        kilometrajeTextView = findViewById(R.id.textViewKilometraje)
        marcaTextView = findViewById(R.id.textViewMarca)
        modeloTextView = findViewById(R.id.textViewModelo)
        motorTextView = findViewById(R.id.textViewMotor)
        paisTextView = findViewById(R.id.textViewPais)
        precioTextView = findViewById(R.id.textViewPrecio)
        viewPager = findViewById(R.id.viewPager)
        animacion = findViewById(R.id.imageViewCarga)

        edtSendMessage = findViewById(R.id.edtSendMessage)
        btnSendMessage = findViewById(R.id.btnSendMessage)


        val idAuto = Adapter_Auto.getSelectedAutoId()
        Log.e("idAuto", "idAuto traido es: " + idAuto.toString())

        Log.d("DetalleAutoActivity", "ID del auto seleccionado: $idAuto")

        //Definir un manejador de excepciones para manejar los errores de la coroutine
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e("DetalleAutoActivity", "Error en la coroutine", exception)
        }

        //Llamar al método getAutoDetails dentro de una coroutine
        CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
            // Mostrar animación de carga al inicio de la operación
            Glide.with(applicationContext)
                .asGif()
                .load(R.drawable.loading)
                .into(animacion)
            try {
                val response: Response<AutoResponses<DataAuto>> =
                    apiInterface.getAutoDetails(idAuto)

                if (response.isSuccessful) {
                    val autoResponse: AutoResponses<DataAuto>? = response.body()

                    if (autoResponse != null && autoResponse.estado == "Ok") {

                        //changed
                        val selectedAuto: DataAuto? = autoResponse.data
                        //val selectedAuto: DataAuto? = autoResponse.data.firstOrNull()

                        if (selectedAuto != null) {
                            Log.d(
                                "DetalleAutoActivity",
                                "Datos del auto recibidos correctamente: $selectedAuto"
                            )

                            fillAutoDetails(selectedAuto)
                            setupViewPager(selectedAuto)
                            printAutoDetails(selectedAuto)
                        } else {
                            Log.e(
                                "DetalleAutoActivity",
                                "No se encontraron datos de auto en la respuesta"
                            )
                        }
                    } else {
                        Log.e(
                            "DetalleAutoActivity",
                            "Error en la respuesta: ${autoResponse?.mensaje ?: "Error desconocido"}"
                        )
                    }
                } else {
                    Log.e(
                        "DetalleAutoActivity",
                        "Error al obtener los detalles del auto: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("DetalleAutoActivity", "Error al realizar la llamada a la API", e)
            } finally {
                // Ocultar animación de carga después de completar la operación
                animacion.visibility = View.GONE
            }
        }




        btnSendMessage.setOnClickListener {
    val descripcion = edtSendMessage.text.toString()
    if (descripcion.isNotEmpty()) {
        val idAuto = Adapter_Auto.getSelectedAutoId()
        CoroutineScope(Dispatchers.IO).launch {
            val requestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), descripcion)
            val response = apiInterface.MensajeCompra(idAuto, requestBody).execute()
            if (response.isSuccessful) {
                Log.d("DetalleAutoActivity", "Mensaje enviado correctamente")
                runOnUiThread {
                    Toast.makeText(this@DetalleAutoActivity,
                        "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e(
                    "DetalleAutoActivity",
                    "Error al enviar el mensaje: ${response.errorBody()?.string()}"
                )
                runOnUiThread {
                    Toast.makeText(this@DetalleAutoActivity,
                        "Error al enviar el mensaje", Toast.LENGTH_SHORT).show()
                }
            }
        }
    } else {
        Log.e("DetalleAutoActivity", "El campo de mensaje está vacío")
        Toast.makeText(this@DetalleAutoActivity,
            "El campo de mensaje está vacío", Toast.LENGTH_SHORT).show()
    }
}


    }

    private fun printAutoDetails(auto: DataAuto?) {
        auto ?: return //Verificar si el objeto DataAuto es nulo

        Log.d("DetalleAutoActivity", "Descripción: ${auto.descripcion ?: "No disponible"}")
        Log.d("DetalleAutoActivity", "Estado: ${auto.estado}")
        Log.d("DetalleAutoActivity", "Kilometraje: ${auto.kilometraje ?: "No disponible"}")
        Log.d("DetalleAutoActivity", "Marca: ${auto.marca ?: "No disponible"}")
        Log.d("DetalleAutoActivity", "Modelo: ${auto.modelo ?: "No disponible"}")
        Log.d("DetalleAutoActivity", "Motor: ${auto.motor ?: "No disponible"}")
        Log.d("DetalleAutoActivity", "País: ${auto.pais ?: "No disponible"}")
        Log.d("DetalleAutoActivity", "Precio: ${auto.precio ?: "No disponible"}")
    }

    private fun fillAutoDetails(auto: DataAuto?) {
        auto ?: return //Verificar si el objeto DataAuto es nulo

        // Llenar las vistas con los datos del auto

        idAutoTextView.text = "ID Auto: ${auto.idAuto}"
        usernameSellerTextView.text = "Vendedor Username: ${auto.idUsuario?.username}"
        descripcionTextView.text = "Descripción: ${auto.descripcion}"
        estadoTextView.text = "Estado: ${auto.estado}"
        kilometrajeTextView.text = "Kilometraje: ${auto.kilometraje}"
        marcaTextView.text = "Marca: ${auto.marca}"
        modeloTextView.text = "Modelo: ${auto.modelo}"
        motorTextView.text = "Motor: ${auto.motor}"
        paisTextView.text = "País: ${auto.pais}"
        precioTextView.text = "Precio: ${auto.precio}"
    }

    private fun setupViewPager(auto: DataAuto) {
        //Crear una lista de cadenas Base64 a partir de las imágenes del auto
        val imageBase64Strings = listOfNotNull(auto.img1, auto.img2, auto.img3, auto.img4)

        //Configurar el adaptador del ViewPager2 con las imágenes
        val adapter = ImageCarruselAdapterList(this, imageBase64Strings)
        viewPager.adapter = adapter
    }


}
