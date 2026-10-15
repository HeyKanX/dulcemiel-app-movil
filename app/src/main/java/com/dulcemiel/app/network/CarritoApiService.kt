package com.dulcemiel.app.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Endpoints del carrito de la cuenta autenticada. */
interface CarritoApiService {
    @GET("carrito") suspend fun carrito(): CarritoRespuesta
    @POST("carrito/agregar") suspend fun agregar(@Body body: CantidadBody): Respuesta<Any>
    @PUT("carrito/actualizar") suspend fun actualizar(@Body body: ActualizarCarritoBody): Respuesta<Any>
    @DELETE("carrito/{id}") suspend fun quitar(@Path("id") id: Int): Respuesta<Any>
    @DELETE("carrito") suspend fun vaciar(): Respuesta<Any>
}
