package com.dulcemiel.app.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

/** Endpoints de contenido público y configuración visual del sitio. */
interface SitioApiService {
    @GET("sitio/inicio") suspend fun inicio(): InicioRespuesta
    @Multipart @PUT("sitio/configuracion") suspend fun editarSitio(@PartMap body: Map<String, @JvmSuppressWildcards RequestBody>, @Part logo: MultipartBody.Part?): Respuesta<Any>
    @Multipart @POST("sitio/carrusel") suspend fun crearCarrusel(@PartMap body: Map<String, @JvmSuppressWildcards RequestBody>, @Part imagen: MultipartBody.Part?): Respuesta<Any>
    @Multipart @PUT("sitio/carrusel/{id}") suspend fun editarCarrusel(@Path("id") id: Int, @PartMap body: Map<String, @JvmSuppressWildcards RequestBody>, @Part imagen: MultipartBody.Part?): Respuesta<Any>
    @DELETE("sitio/carrusel/{id}") suspend fun eliminarCarrusel(@Path("id") id: Int): Respuesta<Any>
}
