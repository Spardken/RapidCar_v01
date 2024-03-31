import Adapter_Auto
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rapidcar_v01.databinding.FragmentHomeBinding
import com.example.rapidcar_v01.modelo.DataAuto
import com.example.rapidcar_v01.modelo.categoria_auto.Data
import com.example.rapidcar_v01.utils.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterAuto: Adapter_Auto
    private var dataList: List<DataAuto> = emptyList()
    private var categorias: List<Data> = emptyList()
    private var selectedCategoryId: Int = -1

    object GlobalData {
        var categoriasNombres: Array<String> = arrayOf()
        var categoriasIds: List<Int> = listOf()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val rootView = binding.root



        if (categorias.isEmpty()) {
            obtenerCategorias()
        }

        configurarSpinnerCategorias()
        setupRecyclerView() // Aquí se llama a la función para configurar el RecyclerView

        binding.spinnerCategoriaAuto.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val newCategoryId = categorias[position].idCategoria
                    if (newCategoryId != selectedCategoryId) {
                        selectedCategoryId = newCategoryId
                        obtenerAutosPorCategoria(selectedCategoryId)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No hacer nada
                }
            }

        return rootView
    }


    private fun setupRecyclerView() {
        binding.rvListAutos.layoutManager = LinearLayoutManager(requireContext())
        adapterAuto = Adapter_Auto(emptyList(), requireContext()) // Aquí se pasa el contexto
        binding.rvListAutos.adapter = adapterAuto
    }


    /*private fun configurarRecyclerView() {
        binding.rvListAutos.layoutManager = LinearLayoutManager(requireContext())
        adapterAuto = Adapter_Auto(emptyList())
        binding.rvListAutos.adapter = adapterAuto
    }*/

    private fun obtenerCategorias() {
    lifecycleScope.launch {
        try {
            val responseCategorias = RetrofitInstance.api.getCategorias()
            if (responseCategorias.isSuccessful) {
                categorias = responseCategorias.body()?.data ?: emptyList()
                Log.d("HomeFragment", "Cantidad de categorías recibidas: ${categorias.size}")
                configurarSpinnerCategorias()

                // Guarda las categorías en SharedPreferences
                val categoriasNombres = categorias.map { it.descripcion }
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return@launch
                with (sharedPref.edit()) {
                    putStringSet("categoriasNombres", categoriasNombres.toSet())
                    putStringSet("categoriasIds", categorias.map { it.idCategoria.toString() }.toSet())
                    apply()
                }

                // Guarda las categorías en GlobalData
                GlobalData.categoriasNombres = categoriasNombres.toTypedArray()
                GlobalData.categoriasIds = categorias.map { it.idCategoria }

                Log.d("HomeFragment", "Categorías guardadas en SharedPreferences: $categoriasNombres")

            } else {
                mostrarToast("Error obteniendo categorías: ${responseCategorias.message()}")
                Log.e("HomeFragment", "Error en la respuesta de categorías: ${responseCategorias.code()}")
            }
        } catch (e: Exception) {
            mostrarToast("Error: ${e.message}")
        }
    }
}

    private fun obtenerAutosPorCategoria(idCategoria: Int) {
        lifecycleScope.launch {
            try {
                val responseAutos = RetrofitInstance.api.getAutosPorCategoria(idCategoria)
                if (responseAutos.isSuccessful) {
                    val autoResponse = responseAutos.body()
                    if (autoResponse != null) {
                        dataList = autoResponse.data ?: emptyList()
                        Log.d("HomeFragment", "Cantidad de datos recibidos: ${dataList.size}")
                        actualizarRecyclerView()
                    } else {
                        mostrarToast("Error en la respuesta de autos: Body nulo")
                        Log.e("HomeFragment", "Error en la respuesta de autos: Body nulo")
                    }
                } else {
                    mostrarToast("Error obteniendo datos de autos: ${responseAutos.message()}")
                    Log.e("HomeFragment", "Error en la respuesta de autos: ${responseAutos.code()}")
                }
            } catch (e: Exception) {
                mostrarToast("Error: ${e.message}")
            }
        }
    }

    private fun actualizarRecyclerView() {
        adapterAuto.updateData(dataList)
    }

    private fun configurarSpinnerCategorias() {
        val categoriasNombres = categorias.map { it.descripcion }
        val categoriasAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categoriasNombres
        )
        binding.spinnerCategoriaAuto.adapter = categoriasAdapter
    }

    private fun mostrarToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
