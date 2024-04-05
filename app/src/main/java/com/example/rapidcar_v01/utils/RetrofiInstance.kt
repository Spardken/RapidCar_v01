package com.example.rapidcar_v01.utils

import android.content.Context
import com.example.rapidcar_v01.api.ApiInterface
import com.example.rapidcar_v01.tokenmanager.SharedPreferencesManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private var retrofit: Retrofit? = null

    //Método para inicializar Retrofit
    fun initialize(context: Context) {
        sharedPreferencesManager = SharedPreferencesManager(context)

        //Configurar el interceptor para agregar el token de autorización y realizar el logging
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = sharedPreferencesManager.fetchAuthToken() ?: ""
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        //Configurar Retrofit con el cliente personalizado
        retrofit = Retrofit.Builder()
            .baseUrl(Utils.BASE)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Proporcionar la instancia de la interfaz de la API
    val api: ApiInterface by lazy {
        retrofit?.create(ApiInterface::class.java)
            ?: throw IllegalStateException("Retrofit instance must be initialized before usage")
    }
}


