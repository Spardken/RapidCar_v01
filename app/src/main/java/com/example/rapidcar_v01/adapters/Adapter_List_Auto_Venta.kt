package com.example.rapidcar_v01.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.rapidcar_v01.R
import com.example.rapidcar_v01.databinding.ItemListAutoVentaBinding
import com.example.rapidcar_v01.modelo.DataAuto
import com.example.rapidcar_v01.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Adapter_List_Auto_Venta(private val context: Context) :
    RecyclerView.Adapter<Adapter_List_Auto_Venta.ViewHolder>() {
    private var dataList = mutableListOf<DataAuto>()

    init {
        loadData()
    }

    private fun loadData() {
    CoroutineScope(Dispatchers.Main).launch {
        // Cambiar la respuesta a AutoResponsesList<DataAuto>
        val response = RetrofitInstance.api.listarAutoUsuario()
        if (response.isSuccessful && response.body() != null) {
            // Como data es una lista, puedes asignarla directamente a dataList
            dataList = response.body()!!.data.toMutableList()
            notifyDataSetChanged()
        } else {
            // Manejar el error
        }
    }
}

    inner class ViewHolder(private val binding: ItemListAutoVentaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataAuto) {
            binding.apply {
                // Log de la cadena Base64 antes de decodificarla (para debugging)
                Log.d("Adapter_Auto_Venta", "Cadena Base64 antes de decodificar: ${item.img1}")

                // Decodificar la cadena Base64 en un Bitmap
                val decodedBytes: ByteArray = Base64.decode(item.img1, Base64.DEFAULT)
                val bitmap: Bitmap =
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                // Cargar la imagen con Glide utilizando el Bitmap decodificado
                Glide.with(binding.autoImageViewVenta.context)
                    .load(bitmap)
                    .placeholder(R.drawable.ic_picture_avatar)
                    .error(R.drawable.baseline_error_24)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("Adapter_Auto", "Error al cargar la imagen con Glide", e)
                            e?.logRootCauses("GlideException")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            // Imagen cargada exitosamente
                            return false
                        }
                    })
                    .into(binding.autoImageViewVenta)

                binding.modeloTextViewVenta.text = "Modelo: ${item.modelo}"
                binding.kilometrajeTextViewVenta.text = "Km: ${item.kilometraje}"
                binding.marcaTextViewVenta.text = "Marca: ${item.marca}"
                binding.precioTextViewVenta.text = "Precio: ${item.precio}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListAutoVentaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateDataVenta(newData: List<DataAuto>) {
        dataList = newData.toMutableList()
        notifyDataSetChanged()
    }
}

