package com.example.rapidcar_v01.modelo

data class Idusuario(
    val apellido_Materno: String,
    val apellido_Paterno: String,
    val celular: Int,
    val contrasena: String,
    val correoElectronico: String,
    val estado: String,
    val idRol: IdRol,
    val id_usuario: Int,
    val img: String?,
    val nombre: String,
    val pais: String,
    val username: String
)