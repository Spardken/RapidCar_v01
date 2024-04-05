package com.example.rapidcar_v01.view.dashboardButton

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rapidcar_v01.api.ApiInterface
import com.example.rapidcar_v01.databinding.ActivityAgregarActualizarAutoBinding
import com.example.rapidcar_v01.tokenmanager.SharedPreferencesManager
import com.example.rapidcar_v01.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Tag
import java.io.ByteArrayOutputStream
import java.io.File

class AgregarActualizarAutoActivity : AppCompatActivity() {

    private val TAG = "AgregarActualizarAutoActivity"


    private lateinit var binding: ActivityAgregarActualizarAutoBinding
    private var selectedImageUris = arrayOfNulls<Uri>(4)

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private val resultLauncher1 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleImageResult(result, 0, binding.imageViewFotoNewAuto1)
        }

    private val resultLauncher2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleImageResult(result, 1, binding.imageViewFotoNewAuto2)
        }

    private val resultLauncher3 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleImageResult(result, 2, binding.imageViewFotoNewAuto3)
        }

    private val resultLauncher4 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleImageResult(result, 3, binding.imageViewFotoNewAuto4)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarActualizarAutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesManager = SharedPreferencesManager(this)

        //setup botones y spinners
        setupSpinners()
        setupButtons()
    }

    private fun setupSpinners() {
        // Spinner para el estado del vehículo
        val spinnerEstadoVehiculo = binding.spinnerEstadoVehiculo
        val opcionesEstado = arrayOf("usado", "semi usado", "nuevo")
        val adapterEstado = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesEstado)
        spinnerEstadoVehiculo.adapter = adapterEstado

        // Spinner para la categoría del vehículo
        val spinnerCategoriaVehiculoVenta = binding.spinnerCategoriaVehiculoVenta
        // Recupera las categorías y los idCategoria de GlobalData
        val categoriasNombres = HomeFragment.GlobalData.categoriasNombres
        val categoriasIds = HomeFragment.GlobalData.categoriasIds

        val adapterCategoria =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriasNombres)
        spinnerCategoriaVehiculoVenta.adapter = adapterCategoria
        adapterCategoria.notifyDataSetChanged() // Forzar la actualización del Spinner
        spinnerCategoriaVehiculoVenta.invalidate() // Forzar un nuevo dibujo del Spinner

        spinnerCategoriaVehiculoVenta.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCategoryName = parent?.getItemAtPosition(position)
                    val selectedCategoryId = categoriasIds[position]
                    Log.d(
                        "AgregarActualizarAutoActivity",
                        "Categoría seleccionada: $selectedCategoryName, id: $selectedCategoryId"
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No hacer nada
                }
            }
    }

    private fun setupButtons() {
        // Configuración de botones para seleccionar imágenes
        binding.buttonSelectImageNewAuto1.setOnClickListener {
            openImagePicker(1, resultLauncher1)
        }

        binding.buttonSelectImageNewAuto2.setOnClickListener {
            openImagePicker(2, resultLauncher2)
        }

        binding.buttonSelectImageNewAuto3.setOnClickListener {
            openImagePicker(3, resultLauncher3)
        }

        binding.buttonSelectImageNewAuto4.setOnClickListener {
            openImagePicker(4, resultLauncher4)
        }

        // Botón para registrar el vehículo
        binding.btnRegistrarAuto.setOnClickListener {
            registrarAuto()
        }
    }

    private fun openImagePicker(imageIndex: Int, resultLauncher: ActivityResultLauncher<Intent>) {
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
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra("imageIndex", imageIndex)
            resultLauncher.launch(intent)
        }
    }

    private fun handleImageResult(result: ActivityResult, index: Int, imageView: ImageView) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val imageUri: Uri? = data.data
                if (imageUri != null) {
                    selectedImageUris[index] = imageUri
                    imageView.setImageURI(imageUri)
                    Log.d("handleImageResult", "Imagen seleccionada correctamente: $imageUri")
                }
            }
        } else {
            Log.e("handleImageResult", "Error al seleccionar la imagen")
        }
    }

    private fun registrarAuto() {
        val motor =
            binding.editTextMotor.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val kilometraje = binding.editTextKilometraje.text.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val estado = binding.spinnerEstadoVehiculo.selectedItem.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val marca =
            binding.editTextMarca.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val pais =
            binding.editTextPais.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val descripcion = binding.editTextDescripcion.text.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val precio =
            binding.editTextPrecio.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val selectedCategoryIndex = binding.spinnerCategoriaVehiculoVenta.selectedItemPosition
        val idCategoriaString =
            HomeFragment.GlobalData.categoriasIds[selectedCategoryIndex].toString()
        val idCategoria = idCategoriaString.toRequestBody("text/plain".toMediaTypeOrNull())
        val modelo =
            binding.editTextModelo.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val estatus = "true".toRequestBody("text/plain".toMediaTypeOrNull())



        val (img1Part, img1Bytes) = createImagePart(binding.imageViewFotoNewAuto1, "img1")
        val (img2Part, img2Bytes) = createImagePart(binding.imageViewFotoNewAuto2, "img2")
        val (img3Part, img3Bytes) = createImagePart(binding.imageViewFotoNewAuto3, "img3")
        val (img4Part, img4Bytes) = createImagePart(binding.imageViewFotoNewAuto4, "img4")



        //val sharedPreferencesManager = SharedPreferencesManager(this)
        val token = sharedPreferencesManager.fetchAuthToken()
        Log.d(TAG, ".............Token actual es..........: $token")


        if (token != null) {
            val api = RetrofitInstance.api

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    // Log antes de hacer la solicitud
                    Log.d(TAG, "Solicitando registro de auto...")

                    val response = api.registrarAutoVenta(
                        motor,
                        kilometraje,
                        estado,
                        marca,
                        pais,
                        img1Part,
                        img2Part,
                        img3Part,
                        img4Part,
                        descripcion,
                        precio,
                        idCategoria,
                        modelo,
                        estatus

                    )

                    //Log después de hacer la solicitud
                    Log.d(TAG, "Solicitud de registro de auto completada.")

                    if (response.isSuccessful) {
                        val autoResponse = response.body()
                        if (autoResponse != null && autoResponse.estado == "OK") {
                            showToast("Auto registrado con éxito")
                        } else {
                            showToast("..........Error al registrar el auto..........: ${response.message()}")
                        }
                    } else {
                        Log.e(
                            TAG,
                            "..........Error al registrar el auto...........: ${response.message()}"
                        )
                        showToast("..........Error al registrar el auto..........: ${response.message()}")
                    }
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        ".............Error al registrar el auto..............>>>>>: ${e.message}",
                        e
                    )
                    showToast("Error: ${e.message}")
                }
            }
        } else {
            showToast("Token no disponible. Inicie sesión.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun createImagePart(imageView: ImageView, imageName: String): Pair<MultipartBody.Part?, ByteArray?> {
    val drawable = imageView.drawable
    var bitmap: Bitmap? = null
    if (drawable is BitmapDrawable) {
        bitmap = (drawable as BitmapDrawable).bitmap
    } else if (drawable is VectorDrawable) {
        bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
    }
    if (bitmap != null) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val imageInByte: ByteArray = byteArrayOutputStream.toByteArray()
        val imageRequestBody = imageInByte.toRequestBody("image/*".toMediaTypeOrNull())
        return Pair(MultipartBody.Part.createFormData(imageName, "$imageName.jpg", imageRequestBody), imageInByte)
    } else {
        return Pair(null, null)
    }
}


    companion object {
        private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1
    }
}
