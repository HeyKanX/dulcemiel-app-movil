package com.dulcemiel.app.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

/** Endpoints administrativos de dashboard, productos y pedidos. */
interface AdministracionApiService {
    @GET("dashboard/resumen") suspend fun resumen(): DashboardRespuesta
    @GET("productos/admin/todos") suspend fun todosProductos(): ProductosAdminRespuesta
    @Multipart @POST("productos") suspend fun crearProducto(@PartMap body: Map<String, @JvmSuppressWildcards RequestBody>, @Part imagen: MultipartBody.Part?): Respuesta<Any>
    @Multipart @PUT("productos/{id}") suspend fun editarProducto(@Path("id") id: Int, @PartMap body: Map<String, @JvmSuppressWildcards RequestBody>, @Part imagen: MultipartBody.Part?): Respuesta<Any>
    @DELETE("productos/{id}") suspend fun eliminarProducto(@Path("id") id: Int): Respuesta<Any>
    @GET("pedidos/admin/todos") suspend fun todosPedidos(): PedidosAdminRespuesta
    @PUT("pedidos/{id}") suspend fun actualizarPedido(@Path("id") id: Long, @Body body: EstadoPedidoBody): Respuesta<Any>
}
