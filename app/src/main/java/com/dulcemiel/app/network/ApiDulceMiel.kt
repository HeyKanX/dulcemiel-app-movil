package com.dulcemiel.app.network

import com.dulcemiel.app.BuildConfig
import com.google.gson.annotations.SerializedName
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

private val BASE_URL = BuildConfig.API_BASE_URL

data class Respuesta<T>(val success: Boolean, val message: String? = null, val data: T? = null)
data class ProductoApi(val id: Int, val nombre: String, val descripcion: String? = null, val precio: Double, val stock: Int, val categoria: String? = null, @SerializedName("imagen_url") val imagenUrl: String? = null, val estado: String = "disponible")
data class CarritoApi(val id: Int, @SerializedName("producto_id") val productoId: Int, val cantidad: Int, val nombre: String, val precio: Double, val stock: Int? = null)
data class CarritoRespuesta(val success: Boolean, val message: String? = null, val data: List<CarritoApi> = emptyList(), val total: String = "0")
data class UsuarioApi(val id: Int, val nombre: String, val email: String, val rol: String)
data class PerfilRespuesta(val success: Boolean, val message: String? = null, val usuario: UsuarioApi? = null)
data class LoginRespuesta(val success: Boolean, val message: String? = null, val token: String? = null, val usuario: UsuarioApi? = null)
data class PedidoApi(
    val id: Long,
    @SerializedName("numero_pedido") val numero: String,
    val total: Double,
    val estado: String,
    @SerializedName("fecha_pedido") val fechaPedido: String? = null,
    @SerializedName("cantidad_unidades") val cantidadUnidades: Int? = null,
    @SerializedName("contacto_nombre") val contactoNombre: String? = null,
    @SerializedName("contacto_telefono") val contactoTelefono: String? = null,
    @SerializedName("direccion_entrega") val direccionEntrega: String? = null,
    val distrito: String? = null,
    val referencia: String? = null,
    val notas: String? = null
)
data class PedidoRespuesta(val success: Boolean, val message: String? = null, val data: List<PedidoApi> = emptyList())
data class LineaPedidoApi(
    val id: Long,
    @SerializedName("producto_id") val productoId: Int,
    val nombre: String,
    val cantidad: Int,
    @SerializedName("precio_unitario") val precioUnitario: Double,
    val subtotal: Double
)
data class DetallePedidoRespuesta(val success: Boolean, val message: String? = null, val pedido: PedidoApi? = null, val detalles: List<LineaPedidoApi> = emptyList())
data class ConfiguracionSitioApi(
    @SerializedName("nombre_empresa") val nombreEmpresa: String,
    @SerializedName("descripcion_inicio") val descripcion: String,
    val mision: String,
    val vision: String,
    @SerializedName("logo_url") val logoUrl: String? = null
)
data class CarruselApi(val id: Int, val titulo: String, val descripcion: String? = null, @SerializedName("imagen_url") val imagenUrl: String? = null, val orden: Int = 0, val activo: Boolean = true)
data class PromocionApi(val id: Int, val titulo: String, val descripcion: String? = null, @SerializedName("descuento_porcentaje") val descuento: Double, @SerializedName("fecha_inicio") val fechaInicio: String = "", @SerializedName("fecha_fin") val fechaFin: String? = null, @SerializedName("activo") val activo: Boolean = true)
data class InicioRespuesta(val success: Boolean, val message: String? = null, val configuracion: ConfiguracionSitioApi? = null, val carrusel: List<CarruselApi> = emptyList(), val promociones: List<PromocionApi> = emptyList())
data class ResumenApi(val ventas: Double = 0.0, val pedidos: Int = 0, val clientes: Int = 0, @SerializedName("stock_bajo") val stockBajo: Int = 0, @SerializedName("pedidos_pendientes") val pedidosPendientes: Int = 0)
data class EstadoPedidoApi(val estado: String, val total: Int)
data class PedidoRecienteApi(val id: Long, @SerializedName("numero_pedido") val numero: String, val total: Double, val estado: String, val nombre: String? = null, val email: String? = null)
data class DashboardRespuesta(val success: Boolean, val message: String? = null, val resumen: ResumenApi? = null, val porEstado: List<EstadoPedidoApi> = emptyList(), val recientes: List<PedidoRecienteApi> = emptyList())
data class ProductosAdminRespuesta(val success: Boolean, val message: String? = null, val data: List<ProductoApi> = emptyList())
data class PedidoAdministrativoApi(val id: Long, @SerializedName("numero_pedido") val numero: String, val total: Double, val estado: String, @SerializedName("fecha_pedido") val fechaPedido: String? = null, val nombre: String? = null, val email: String? = null)
data class PedidosAdminRespuesta(val success: Boolean, val message: String? = null, val data: List<PedidoAdministrativoApi> = emptyList())
data class EstadoPedidoBody(val estado: String)
data class PromocionRespuesta(val success: Boolean, val message: String? = null, val data: List<PromocionApi> = emptyList())
data class PromocionBody(@SerializedName("titulo") val titulo: String, @SerializedName("descripcion") val descripcion: String?, @SerializedName("descuento_porcentaje") val descuento: Double, @SerializedName("fecha_inicio") val fechaInicio: String, @SerializedName("fecha_fin") val fechaFin: String?, val activo: Boolean)
data class UsuarioAdministrativoApi(val id: Int, val nombre: String, val email: String, val rol: String, val estado: String)
data class UsuariosRespuesta(val success: Boolean, val message: String? = null, val data: List<UsuarioAdministrativoApi> = emptyList())
data class EmpleadoBody(val nombre: String, val email: String, val password: String, val rol: String)
data class UsuarioActualizacionBody(val nombre: String, val email: String, val rol: String, val estado: String)
data class LoginBody(val email: String, val password: String)
data class RegistroBody(val nombre: String, val email: String, val password: String, val passwordConfirm: String)
data class CantidadBody(@SerializedName("producto_id") val productoId: Int, val cantidad: Int)
data class ActualizarCarritoBody(@SerializedName("carrito_id") val carritoId: Int, val cantidad: Int)
data class EntregaBody(@SerializedName("contacto_nombre") val nombre: String, @SerializedName("contacto_telefono") val telefono: String, @SerializedName("contacto_email") val email: String?, @SerializedName("direccion_entrega") val direccion: String, val distrito: String, val referencia: String? = null, val notas: String? = null)

interface ApiDulceMiel {
    @GET("productos") suspend fun productos(): Respuesta<List<ProductoApi>>
    @GET("productos/{id}") suspend fun producto(@Path("id") id: Int): Respuesta<ProductoApi>
    @POST("auth/login") suspend fun login(@Body body: LoginBody): LoginRespuesta
    @POST("auth/register") suspend fun registrar(@Body body: RegistroBody): Respuesta<Any>
    @GET("auth/perfil") suspend fun perfil(): PerfilRespuesta
    @GET("carrito") suspend fun carrito(): CarritoRespuesta
    @POST("carrito/agregar") suspend fun agregar(@Body body: CantidadBody): Respuesta<Any>
    @PUT("carrito/actualizar") suspend fun actualizar(@Body body: ActualizarCarritoBody): Respuesta<Any>
    @DELETE("carrito/{id}") suspend fun quitar(@Path("id") id: Int): Respuesta<Any>
    @DELETE("carrito") suspend fun vaciar(): Respuesta<Any>
    @GET("pedidos") suspend fun pedidos(): PedidoRespuesta
    @GET("pedidos/{id}") suspend fun detallePedido(@Path("id") id: Long): DetallePedidoRespuesta
    @POST("pedidos") suspend fun crearPedido(@Body body: EntregaBody): Respuesta<Any>
    // Administración: las pantallas de gestión consumen estos mismos contratos que la web.
    @GET("dashboard/resumen") suspend fun resumen(): DashboardRespuesta
    @GET("productos/admin/todos") suspend fun todosProductos(): ProductosAdminRespuesta
    @Multipart @POST("productos") suspend fun crearProducto(@PartMap body: Map<String, @JvmSuppressWildcards okhttp3.RequestBody>, @Part imagen: okhttp3.MultipartBody.Part?): Respuesta<Any>
    @Multipart @PUT("productos/{id}") suspend fun editarProducto(@Path("id") id: Int, @PartMap body: Map<String, @JvmSuppressWildcards okhttp3.RequestBody>, @Part imagen: okhttp3.MultipartBody.Part?): Respuesta<Any>
    @DELETE("productos/{id}") suspend fun eliminarProducto(@Path("id") id: Int): Respuesta<Any>
    @GET("pedidos/admin/todos") suspend fun todosPedidos(): PedidosAdminRespuesta
    @PUT("pedidos/{id}") suspend fun actualizarPedido(@Path("id") id: Long, @Body body: EstadoPedidoBody): Respuesta<Any>
    @GET("auth/usuarios") suspend fun usuarios(): UsuariosRespuesta
    @POST("auth/empleados") suspend fun crearEmpleado(@Body body: EmpleadoBody): Respuesta<Any>
    @PUT("auth/usuarios/{id}") suspend fun editarUsuario(@Path("id") id: Int, @Body body: UsuarioActualizacionBody): Respuesta<Any>
    @GET("promociones") suspend fun promociones(): PromocionRespuesta
    @POST("promociones") suspend fun crearPromocion(@Body body: PromocionBody): Respuesta<Any>
    @PUT("promociones/{id}") suspend fun editarPromocion(@Path("id") id: Int, @Body body: PromocionBody): Respuesta<Any>
    @DELETE("promociones/{id}") suspend fun eliminarPromocion(@Path("id") id: Int): Respuesta<Any>
    @GET("sitio/inicio") suspend fun inicio(): InicioRespuesta
    @Multipart @PUT("sitio/configuracion") suspend fun editarSitio(@PartMap body: Map<String, @JvmSuppressWildcards okhttp3.RequestBody>, @Part logo: okhttp3.MultipartBody.Part?): Respuesta<Any>
    @Multipart @POST("sitio/carrusel") suspend fun crearCarrusel(@PartMap body: Map<String, @JvmSuppressWildcards okhttp3.RequestBody>, @Part imagen: okhttp3.MultipartBody.Part?): Respuesta<Any>
    @Multipart @PUT("sitio/carrusel/{id}") suspend fun editarCarrusel(@Path("id") id: Int, @PartMap body: Map<String, @JvmSuppressWildcards okhttp3.RequestBody>, @Part imagen: okhttp3.MultipartBody.Part?): Respuesta<Any>
    @DELETE("sitio/carrusel/{id}") suspend fun eliminarCarrusel(@Path("id") id: Int): Respuesta<Any>
}

class TokenInterceptor(private val token: () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(
        chain.request().newBuilder().apply { token()?.let { header("Authorization", "Bearer $it") } }.build()
    )
}

fun crearApi(token: () -> String?): ApiDulceMiel = Retrofit.Builder().baseUrl(BASE_URL)
    .client(
        OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(token))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(35, TimeUnit.SECONDS)
            .build()
    )
    .addConverterFactory(GsonConverterFactory.create()).build().create(ApiDulceMiel::class.java)
