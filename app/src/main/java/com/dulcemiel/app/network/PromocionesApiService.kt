package com.dulcemiel.app.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Endpoints de promociones. */
interface PromocionesApiService {
    @GET("promociones") suspend fun promociones(): PromocionRespuesta
    @POST("promociones") suspend fun crearPromocion(@Body body: PromocionBody): Respuesta<Any>
    @PUT("promociones/{id}") suspend fun editarPromocion(@Path("id") id: Int, @Body body: PromocionBody): Respuesta<Any>
    @DELETE("promociones/{id}") suspend fun eliminarPromocion(@Path("id") id: Int): Respuesta<Any>
}
