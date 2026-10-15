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

class TokenInterceptor(private val token: () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(
        chain.request().newBuilder().apply { token()?.let { header("Authorization", "Bearer $it") } }.build()
    )
}

/** Servicios Retrofit separados por dominio, creados con el mismo cliente autenticado. */
data class ServiciosDulceMiel(
    val autenticacion: AutenticacionApiService,
    val catalogo: CatalogoApiService,
    val carrito: CarritoApiService,
    val pedidos: PedidosApiService,
    val administracion: AdministracionApiService,
    val promociones: PromocionesApiService,
    val sitio: SitioApiService
)

fun crearServicios(token: () -> String?): ServiciosDulceMiel {
    val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(TokenInterceptor(token))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .callTimeout(35, TimeUnit.SECONDS)
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return ServiciosDulceMiel(
        autenticacion = retrofit.create(AutenticacionApiService::class.java),
        catalogo = retrofit.create(CatalogoApiService::class.java),
        carrito = retrofit.create(CarritoApiService::class.java),
        pedidos = retrofit.create(PedidosApiService::class.java),
        administracion = retrofit.create(AdministracionApiService::class.java),
        promociones = retrofit.create(PromocionesApiService::class.java),
        sitio = retrofit.create(SitioApiService::class.java)
    )
}
