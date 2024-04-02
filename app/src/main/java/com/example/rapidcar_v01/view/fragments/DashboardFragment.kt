package com.example.rapidcar_v01.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rapidcar_v01.adapters.Adapter_List_Auto_Venta
import com.example.rapidcar_v01.databinding.FragmentDashboardBinding
import com.example.rapidcar_v01.modelo.DataAuto
import com.example.rapidcar_v01.view.dashboardButton.AgregarActualizarAutoActivity
import com.example.rapidcar_v01.view.dashboardButton.MensajesLeadsActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapterAutoVenta: Adapter_List_Auto_Venta

    private var dataList: List<DataAuto> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        setupRecyclerViewVenta()
        return binding.root
    }

    private fun setupRecyclerViewVenta() {
        binding.rvAutoVenta.layoutManager = LinearLayoutManager(requireContext())
        adapterAutoVenta = Adapter_List_Auto_Venta(requireContext()) // Aquí se pasa el contexto
        binding.rvAutoVenta.adapter = adapterAutoVenta
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVentaAuto.setOnClickListener {
            val intent = Intent(activity, AgregarActualizarAutoActivity::class.java)
            startActivity(intent)
        }

        binding.btnMensajesVenta.setOnClickListener {
            val intent = Intent(activity, MensajesLeadsActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun actualizarRecyclerViewVenta() {
        adapterAutoVenta.updateDataVenta(dataList)
    }
}