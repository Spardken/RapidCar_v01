package com.example.rapidcar_v01.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rapidcar_v01.R
import com.example.rapidcar_v01.modelo.ClienteData

class Adapter_List_Mensajes_Leads(private val list: List<ClienteData>) :
    RecyclerView.Adapter<Adapter_List_Mensajes_Leads.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenComprador: ImageView = itemView.findViewById(R.id.imagenComprador)
        val tvUsernameComprador: TextView = itemView.findViewById(R.id.tvUsernameComprador)
        val tvCelularComprador: TextView = itemView.findViewById(R.id.tvCelularComprador)
        val tvEmailComprador: TextView = itemView.findViewById(R.id.tvEmailComprador)
        val idAutoInteresadoTextView: TextView =
            itemView.findViewById(R.id.idAutoInteresadoTextView)
        val mensajeCompradorTextView: TextView =
            itemView.findViewById(R.id.mensajeCompradorTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mensajes_leads, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val imageBytes = Base64.decode(item.usuario.img, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        Glide.with(holder.itemView.context).load(decodedImage).into(holder.imagenComprador)
        holder.tvUsernameComprador.text = "Username: " + item.usuario.username
        holder.tvCelularComprador.text = "Celular: " + item.usuario.celular.toString()
        holder.tvEmailComprador.text = "Email: " + item.usuario.correoElectronico
        holder.idAutoInteresadoTextView.text = "ID Auto de interes: " + item.auto.idAuto.toString()
        holder.mensajeCompradorTextView.text = "Mensaje del interesado: " + item.descripcion
    }

    override fun getItemCount() = list.size
}