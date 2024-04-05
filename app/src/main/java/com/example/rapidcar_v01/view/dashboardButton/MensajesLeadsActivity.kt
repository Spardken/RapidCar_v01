package com.example.rapidcar_v01.view.dashboardButton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rapidcar_v01.adapters.Adapter_List_Mensajes_Leads
import com.example.rapidcar_v01.api.ApiInterface
import com.example.rapidcar_v01.databinding.ActivityMensajesLeadsBinding
import com.example.rapidcar_v01.modelo.ClienteData
import com.example.rapidcar_v01.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MensajesLeadsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMensajesLeadsBinding
    private lateinit var adapter: Adapter_List_Mensajes_Leads
    private lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMensajesLeadsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiInterface = RetrofitInstance.api
        fetchDataFromApi()
    }

    private fun fetchDataFromApi() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface.getClientes()
            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) {
                    withContext(Dispatchers.Main) {
                        setupRecyclerView(data)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(list: List<ClienteData>) {
        adapter = Adapter_List_Mensajes_Leads(list)
        binding.rvMensajesLeads.layoutManager = LinearLayoutManager(this)
        binding.rvMensajesLeads.adapter = adapter
    }
}