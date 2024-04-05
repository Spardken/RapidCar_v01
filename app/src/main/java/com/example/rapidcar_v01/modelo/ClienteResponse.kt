package com.example.rapidcar_v01.modelo

data class ClienteResponse(
    val estado: String,
    val mensaje: String,
    val data: List<ClienteData>
)

data class ClienteData(
    val id_cliente: Int,
    val usuario: Usuario,
    val auto: Auto,
    val descripcion: String
)

data class UsuarioCli(
    val id_usuario: Int,
    val username: String,
    val nombre: String,
    val apellido_Paterno: String,
    val apellido_Materno: String,
    val correoElectronico: String,
    val contrasena: String,
    val celular: Int,
    val pais: String,
    val idRol: IdRol,
    val estado: String,
    val img: String
)

data class Auto(
    val idAuto: Int,
    val modelo: String,
    val motor: String,
    val kilometraje: String,
    val estado: String,
    val marca: String,
    val pais: String,
    val descripcion: String,
    val img1: String,
    val img2: String,
    val img3: String,
    val img4: String,
    val precio: Double,
    val estatus: Boolean,
    val idusuario: Idusuario,
    val idCategoria: IdCategoria
)

data class IdRolCli(
    val idRol: Int,
    val descripcion: String
)

data class IdusuarioCli(
    val id_usuario: Int,
    val username: String,
    val nombre: String,
    val apellido_Paterno: String,
    val apellido_Materno: String,
    val correoElectronico: String,
    val contrasena: String,
    val celular: Int,
    val pais: String,
    val idRol: IdRol,
    val estado: String,
    val img: String
)

data class IdCategoriaCli(
    val descripcion: String,
    val img: String,
    val idCategoria: Int
)
