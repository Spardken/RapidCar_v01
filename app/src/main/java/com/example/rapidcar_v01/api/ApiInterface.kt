package com.example.rapidcar_v01.api

import com.example.rapidcar_v01.modelo.AutoE
import com.example.rapidcar_v01.modelo.AutoResponse
import com.example.rapidcar_v01.modelo.AutoResponses
import com.example.rapidcar_v01.modelo.AutoResponsesList
import com.example.rapidcar_v01.modelo.DataAuto
import com.example.rapidcar_v01.modelo.IdCategoria
import com.example.rapidcar_v01.modelo.Idusuario
import com.example.rapidcar_v01.modelo.LoginRequest
import com.example.rapidcar_v01.modelo.LoginResponse
import com.example.rapidcar_v01.modelo.Usuario
import com.example.rapidcar_v01.modelo.categoria_auto.Categoria_auto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiInterface {

    //lista todos los autos disponibles
    @GET("auto/Filtro/Disponible")
    suspend fun getAutosDisponibles(): Response<AutoE>

    @Multipart
    @POST("usuario/registrar_usuario")
    fun registrarUsuario(
        @Part img: MultipartBody.Part?, // Cambia el tipo de parámetro a MultipartBody.Part
        @Part("nombre") nombre: RequestBody,
        @Part("apellido_Paterno") apellidoPaterno: RequestBody,
        @Part("apellido_Materno") apellidoMaterno: RequestBody,
        @Part("correo_electronico") correoElectronico: RequestBody,
        @Part("contrasena") contrasena: RequestBody,
        @Part("celular") celular: RequestBody,
        @Part("username") username: RequestBody,
        @Part("pais") pais: RequestBody,
    ): Call<Void>



    @Multipart
    @PUT("usuario/actualizar_usuario")
    fun ActualizarUsuario(
        @Part img: MultipartBody.Part?, // Cambia el tipo de parámetro a MultipartBody.Part
        @Part("nombre") nombre: RequestBody,
        @Part("apellido_Paterno") apellidoPaterno: RequestBody,
        @Part("apellido_Materno") apellidoMaterno: RequestBody,
        @Part("correo_electronico") correoElectronico: RequestBody,
        @Part("contrasena") contrasena: RequestBody,
        @Part("celular") celular: RequestBody,
        @Part("username") username: RequestBody
    ): Call<Void>


    //login de usuario
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>


    //lista todas las categorias para ser usadas en el spinner
    @GET("categoria")
    suspend fun getCategorias(): Response<Categoria_auto>
    @GET("usuario/buscar_usuario")
    suspend fun getusuario() : Response<AutoResponses<Usuario>>

    @GET("auto/busqueda/categoria")
    suspend fun getAutosPorCategoria(@Query("id") idCategoria: Int):
            Response<AutoResponse<DataAuto>>


    @GET("auto/listar-un-Auto")
    suspend fun getAutoDetails(@Query("id") idAuto: Int):
            Response<AutoResponses<DataAuto>>


    @Multipart
    @POST("auto/registrar")
    suspend fun registrarAutoVenta(
        @Part("motor") motor: RequestBody,
        @Part("kilometraje") kilometraje: RequestBody,
        @Part("estado") estado: RequestBody,
        @Part("marca") marca: RequestBody,
        @Part("pais") pais: RequestBody,
        @Part img1: MultipartBody.Part?,
        @Part img2: MultipartBody.Part?,
        @Part img3: MultipartBody.Part?,
        @Part img4: MultipartBody.Part?,
        @Part("descripcion") descripcion: RequestBody,
        @Part("precio") precio: RequestBody,
        @Part("idCategoria") idCategoria: RequestBody,
        @Part("modelo") modelo: RequestBody,
        @Part("estatus") estatus: RequestBody
        //@Header("Authorization") token: String  // Agrega este parámetro para el token
    ): Response<AutoResponses<DataAuto>>


    @GET("auto/listarAuto/usuario")
    suspend fun listarAutoUsuario(): Response<AutoResponsesList<DataAuto>>

    @DELETE("usuario/eliminar_usuario")
    suspend fun eliminarUsuario(): Response<AutoResponses<Usuario>>




}


