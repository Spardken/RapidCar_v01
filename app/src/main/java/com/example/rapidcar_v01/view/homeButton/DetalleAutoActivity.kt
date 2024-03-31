package com.example.rapidcar_v01.view.homeButton

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.rapidcar_v01.R
import com.example.rapidcar_v01.adapters.ImageCarruselAdapterList
import com.example.rapidcar_v01.api.ApiInterface
import com.example.rapidcar_v01.modelo.AutoResponses
import com.example.rapidcar_v01.modelo.DataAuto
import com.example.rapidcar_v01.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

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

    private lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_auto)

        // Inicializar la instancia de la interfaz API utilizando RetrofitInstance
        apiInterface = RetrofitInstance.api

        // Inicializar las vistas
        descripcionTextView = findViewById(R.id.textViewDescripcion)
        estadoTextView = findViewById(R.id.textViewEstado)
        kilometrajeTextView = findViewById(R.id.textViewKilometraje)
        marcaTextView = findViewById(R.id.textViewMarca)
        modeloTextView = findViewById(R.id.textViewModelo)
        motorTextView = findViewById(R.id.textViewMotor)
        paisTextView = findViewById(R.id.textViewPais)
        precioTextView = findViewById(R.id.textViewPrecio)
        viewPager = findViewById(R.id.viewPager)

        val idAuto = Adapter_Auto.getSelectedAutoId()
        Log.e("idAuto", "idAuto traido es: " + idAuto.toString())

        Log.d("DetalleAutoActivity", "ID del auto seleccionado: $idAuto")

        // Definir un manejador de excepciones para manejar los errores de la coroutine
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e("DetalleAutoActivity", "Error en la coroutine", exception)
        }

        // Llamar al método getAutoDetails dentro de una coroutine
        CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
            try {
                val response: Response<AutoResponses<DataAuto>> =
                    apiInterface.getAutoDetails(idAuto)

                if (response.isSuccessful) {
                    val autoResponse: AutoResponses<DataAuto>? = response.body()

                    if (autoResponse != null && autoResponse.estado == "Ok") {
                        val selectedAuto: DataAuto? = autoResponse.data

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
            }
        }
    }

    private fun printAutoDetails(auto: DataAuto?) {
        auto ?: return // Verificar si el objeto DataAuto es nulo

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
        auto ?: return // Verificar si el objeto DataAuto es nulo

        // Llenar las vistas con los datos del auto
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
        // Crear una lista de cadenas Base64 a partir de las imágenes del auto
        val imageBase64Strings = listOfNotNull(auto.img1, auto.img2, auto.img3, auto.img4)

        // Configurar el adaptador del ViewPager2 con las imágenes
        val adapter = ImageCarruselAdapterList(this, imageBase64Strings)
        viewPager.adapter = adapter
    }


}
