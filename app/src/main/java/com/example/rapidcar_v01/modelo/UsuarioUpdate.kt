package com.example.rapidcar_v01.modelo

data class UsuarioUpdate(
    val apellido_Materno: String,
    val apellido_Paterno: String,
    val celular: Int,
    var contrasena: String,
    val correoElectronico: String,
    val img: String?,
    val nombre: String,
    val username: String
)