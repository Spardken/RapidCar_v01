package com.example.rapidcar_v01.modelo

import com.google.gson.annotations.SerializedName

data class IdCategoria(
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("idCategoria") val idCategoria: Int,
    @SerializedName("img") val img: Any
)
