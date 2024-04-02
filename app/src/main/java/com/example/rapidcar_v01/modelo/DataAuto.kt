/*
package com.example.rapidcar_v01.modelo

import com.squareup.moshi.Json

data class DataAuto(

    @Json(name="descripcion")
    val descripcion: String,

    @Json(name="estado")
    val estado: String,

    @Json(name="estatus")
    val estatus: Boolean,

    @Json(name="IdAuto")
    val IdAuto: Int,

    @Json(name="idCategoria")
    val idCategoria: IdCategoria,

    @Json(name="idusuario")
    val idusuario: Idusuario,

    @Json(name="img1")
    val img1: String,

    @Json(name="img2")
    val img2: String,

    @Json(name="img3")
    val img3: String,

    @Json(name="img4")
    val img4: String,

    @Json(name="kilometraje")
    val kilometraje: String,

    @Json(name="marca")
    val marca: String,

    @Json(name="modelo")
    val modelo: String,

    @Json(name="motor")
    val motor: String,

    @Json(name="pais")
    val pais: String,

    @Json(name="precio")
    val precio: Double

)
*/

package com.example.rapidcar_v01.modelo

import com.google.gson.annotations.SerializedName

data class AutoResponses<T>(
    val estado: String,
    val mensaje: String,
    val data: T
)



//changed
data class AutoResponsesList<T>(
    val estado: String,
    val mensaje: String,
    @SerializedName("data")
    val data: List<T>
)

data class DataAuto(
    @SerializedName("idAuto")
    val idAuto: Int,
    @SerializedName("modelo")
    val modelo: String?,
    @SerializedName("motor")
    val motor: String?,
    @SerializedName("kilometraje")
    val kilometraje: String?,
    @SerializedName("estado")
    val estado: String?,
    @SerializedName("marca")
    val marca: String?,
    @SerializedName("pais")
    val pais: String?,
    @SerializedName("descripcion")
    val descripcion: String?,
    @SerializedName("img1")
    val img1: String?,
    @SerializedName("img2")
    val img2: String?,
    @SerializedName("img3")
    val img3: String?,
    @SerializedName("img4")
    val img4: String?,
    @SerializedName("precio")
    val precio: Double?,
    @SerializedName("estatus")
    val estatus: Boolean?,
    @SerializedName("idusuario")
    val idUsuario: Usuario?,
    @SerializedName("idCategoria")
    val idCategoria: Categoria?
)

data class Usuario(
    @SerializedName("id_usuario")
    val idUsuario: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("apellido_Paterno")
    val apellidoPaterno: String,
    @SerializedName("apellido_Materno")
    val apellidoMaterno: String,
    @SerializedName("correoElectronico")
    val correoElectronico: String,
    @SerializedName("contrasena")
    val contrasena: String,
    @SerializedName("celular")
    val celular: Int,
    @SerializedName("pais")
    val pais: String?,
    @SerializedName("idRol")
    val idRol: Rol,
    @SerializedName("estado")
    val estado: String,
    @SerializedName("img")
    val img: String
)

data class Categoria(
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("img")
    val img: String,
    @SerializedName("idCategoria")
    val idCategoria: Int
)

data class Rol(
    @SerializedName("idRol")
    val idRol: Int,
    @SerializedName("descripcion")
    val descripcion: String
)

