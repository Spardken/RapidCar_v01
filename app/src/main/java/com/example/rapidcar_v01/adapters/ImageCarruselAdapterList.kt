package com.example.rapidcar_v01.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.rapidcar_v01.R

class ImageCarruselAdapterList(private val context: Context, private val imageBase64Strings: List<String>) :
    RecyclerView.Adapter<ImageCarruselAdapterList.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageBase64String = imageBase64Strings[position]
        val decodedBytes = Base64.decode(imageBase64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        holder.imageView.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int {
        return imageBase64Strings.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}