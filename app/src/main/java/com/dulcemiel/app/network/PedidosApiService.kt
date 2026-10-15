package com.dulcemiel.app.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/** Endpoints de pedidos del cliente autenticado. */
interface PedidosApiService {
    @GET("pedidos") suspend fun pedidos(): PedidoRespuesta
    @GET("pedidos/{id}") suspend fun detallePedido(@Path("id") id: Long): DetallePedidoRespuesta
    @POST("pedidos") suspend fun crearPedido(@Body body: EntregaBody): Respuesta<Any>
}
