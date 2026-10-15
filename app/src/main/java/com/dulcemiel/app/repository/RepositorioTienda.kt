package com.dulcemiel.app.repository

import com.dulcemiel.app.model.Pedido
import com.dulcemiel.app.model.DetallePedido
import com.dulcemiel.app.model.LineaPedido
import com.dulcemiel.app.model.Producto
import com.dulcemiel.app.model.InicioTienda
import com.dulcemiel.app.model.Promocion
import com.dulcemiel.app.model.SlideInicio
import com.dulcemiel.app.model.ResumenDashboard
import com.dulcemiel.app.model.EstadoPedido
import com.dulcemiel.app.model.PedidoReciente
import com.dulcemiel.app.model.ProductoEdicion
import com.dulcemiel.app.model.PedidoAdministrativo
import com.dulcemiel.app.model.UsuarioAdministrativo
import com.dulcemiel.app.model.ArchivoAdjunto
import com.dulcemiel.app.model.ConfiguracionSitioEdicion
import com.dulcemiel.app.model.CarruselEdicion
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.dulcemiel.app.network.*
import java.math.RoundingMode

class RepositorioTienda(private val servicios: ServiciosDulceMiel, private val sesion: Sesion) {
    suspend fun productos(): List<Producto> = servicios.catalogo.productos().data.orEmpty().map { it.aProducto() }
    suspend fun producto(id: Int): Producto = servicios.catalogo.producto(id).data?.aProducto()
        ?: throw IllegalStateException("No se pudo cargar el producto")
    suspend fun login(email: String, password: String): String? = servicios.autenticacion.login(LoginBody(email, password)).also {
        if (it.success && it.token != null && it.usuario != null) sesion.iniciar(it.token, it.usuario.nombre, it.usuario.email, it.usuario.rol)
    }.message
    suspend fun registrar(nombre: String, email: String, password: String): String? = servicios.autenticacion.registrar(RegistroBody(nombre, email, password, password)).message
    fun autenticado() = sesion.token != null
    fun nombreUsuario() = sesion.nombre
    fun emailUsuario() = sesion.email
    fun rolUsuario() = sesion.rol
    fun puedeAdministrar() = sesion.rol in setOf("admin", "empleado")
    fun cerrarSesion() = sesion.cerrar()
    suspend fun perfil(): UsuarioApi? = servicios.autenticacion.perfil().let { respuesta ->
        respuesta.usuario?.also { sesion.actualizarPerfil(it.nombre, it.email, it.rol) }
    }
    suspend fun resumenDashboard(): ResumenDashboard {
        val respuesta = servicios.administracion.resumen()
        val resumen = respuesta.resumen ?: throw IllegalStateException(respuesta.message ?: "No se pudo cargar el dashboard")
        return ResumenDashboard(
            ventasCentimos = (resumen.ventas * 100).toInt(), pedidos = resumen.pedidos,
            clientes = resumen.clientes, stockBajo = resumen.stockBajo, pedidosPendientes = resumen.pedidosPendientes,
            porEstado = respuesta.porEstado.map { EstadoPedido(it.estado, it.total) },
            recientes = respuesta.recientes.map { PedidoReciente(it.id, it.numero, (it.total * 100).toInt(), it.estado, it.nombre ?: it.email.orEmpty()) }
        )
    }
    suspend fun productosAdministrativos(): List<Producto> = servicios.administracion.todosProductos().data.map { it.aProducto() }
    suspend fun guardarProducto(producto: ProductoEdicion): String? {
        val campos = campos(
            "nombre" to producto.nombre, "descripcion" to producto.descripcion, "precio" to producto.precio,
            "stock" to producto.stock, "categoria" to producto.categoria, "estado" to producto.estado
        )
        val imagen = parteArchivo("imagen", producto.imagen)
        val respuesta = producto.id?.let { servicios.administracion.editarProducto(it, campos, imagen) } ?: servicios.administracion.crearProducto(campos, imagen)
        return respuesta.message
    }
    suspend fun eliminarProducto(id: Int): String? = servicios.administracion.eliminarProducto(id).message
    suspend fun pedidosAdministrativos(): List<PedidoAdministrativo> = servicios.administracion.todosPedidos().data.map {
        PedidoAdministrativo(it.id, it.numero, (it.total * 100).toInt(), it.estado, it.fechaPedido, it.nombre.orEmpty(), it.email.orEmpty())
    }
    suspend fun actualizarEstadoPedido(id: Long, estado: String): String? = servicios.administracion.actualizarPedido(id, EstadoPedidoBody(estado)).message
    suspend fun promocionesAdministrativas(): List<Promocion> = servicios.promociones.promociones().data.map { it.aPromocion() }
    suspend fun guardarPromocion(promocion: Promocion): String? {
        val body = PromocionBody(promocion.titulo, promocion.descripcion, promocion.descuento, promocion.fechaInicio, promocion.fechaFin, promocion.activa)
        return promocion.id.takeIf { it > 0 }?.let { servicios.promociones.editarPromocion(it, body).message } ?: servicios.promociones.crearPromocion(body).message
    }
    suspend fun eliminarPromocion(id: Int): String? = servicios.promociones.eliminarPromocion(id).message
    suspend fun usuariosAdministrativos(): List<UsuarioAdministrativo> = servicios.autenticacion.usuarios().data.map { UsuarioAdministrativo(it.id, it.nombre, it.email, it.rol, it.estado) }
    suspend fun crearEmpleado(nombre: String, email: String, password: String, rol: String): String? = servicios.autenticacion.crearEmpleado(EmpleadoBody(nombre, email, password, rol)).message
    suspend fun actualizarUsuario(usuario: UsuarioAdministrativo): String? = servicios.autenticacion.editarUsuario(usuario.id, UsuarioActualizacionBody(usuario.nombre, usuario.email, usuario.rol, usuario.estado)).message
    suspend fun guardarConfiguracion(configuracion: ConfiguracionSitioEdicion): String? = servicios.sitio.editarSitio(
        campos("nombre_empresa" to configuracion.nombre, "descripcion_inicio" to configuracion.descripcion, "mision" to configuracion.mision, "vision" to configuracion.vision),
        parteArchivo("logo", configuracion.logo)
    ).message
    suspend fun guardarCarrusel(carrusel: CarruselEdicion): String? {
        val campos = campos("titulo" to carrusel.titulo, "descripcion" to carrusel.descripcion, "orden" to carrusel.orden, "activo" to carrusel.activo.toString())
        val imagen = parteArchivo("imagen", carrusel.imagen)
        return carrusel.id?.let { servicios.sitio.editarCarrusel(it, campos, imagen).message } ?: servicios.sitio.crearCarrusel(campos, imagen).message
    }
    suspend fun eliminarCarrusel(id: Int): String? = servicios.sitio.eliminarCarrusel(id).message
    suspend fun carrito(): Triple<List<Producto>, Map<Int, Int>, Int> = servicios.carrito.carrito().let { respuesta ->
        val productos = respuesta.data.map {
            Producto(it.productoId, it.nombre, "", (it.precio * 100).toInt(), it.stock ?: Int.MAX_VALUE, "", it.id)
        }
        val cantidades = respuesta.data.associate { it.productoId to it.cantidad }
        val totalCentimos = respuesta.total.toBigDecimalOrNull()
            ?.movePointRight(2)
            ?.setScale(0, RoundingMode.HALF_UP)
            ?.toInt()
            ?: productos.sumOf { producto -> producto.precioCentimos * (cantidades[producto.id] ?: 0) }
        Triple(productos, cantidades, totalCentimos)
    }
    suspend fun agregar(id: Int) = servicios.carrito.agregar(CantidadBody(id, 1)).message
    suspend fun actualizar(carritoId: Int, cantidad: Int) = if (cantidad <= 0) servicios.carrito.quitar(carritoId).message else servicios.carrito.actualizar(ActualizarCarritoBody(carritoId, cantidad)).message
    suspend fun quitar(carritoId: Int) = servicios.carrito.quitar(carritoId).message
    suspend fun vaciarCarrito() = servicios.carrito.vaciar().message
    suspend fun pedidos(): List<Pedido> = servicios.pedidos.pedidos().data.map { it.aPedido() }
    suspend fun detallePedido(id: Long): DetallePedido {
        val respuesta = servicios.pedidos.detallePedido(id)
        val pedido = respuesta.pedido ?: throw IllegalStateException(respuesta.message ?: "No se pudo cargar el pedido")
        return DetallePedido(
            pedido.aPedido(),
            respuesta.detalles.map { LineaPedido(it.id, it.productoId, it.nombre, it.cantidad, (it.precioUnitario * 100).toInt(), (it.subtotal * 100).toInt()) }
        )
    }
    suspend fun crearPedido(entrega: EntregaBody) = servicios.pedidos.crearPedido(entrega)
    suspend fun inicio(): InicioTienda = servicios.sitio.inicio().let { respuesta ->
        val config = respuesta.configuracion
        InicioTienda(
            config?.nombreEmpresa ?: "Dulce Miel",
            config?.descripcion ?: "Conoce nuestros productos naturales.",
            config?.mision.orEmpty(), config?.vision.orEmpty(), config?.logoUrl,
            respuesta.carrusel.map { SlideInicio(it.id, it.titulo, it.descripcion, it.imagenUrl, it.orden, it.activo) },
            respuesta.promociones.map { Promocion(it.id, it.titulo, it.descripcion, it.descuento, it.fechaInicio, it.fechaFin, it.activo) }
        )
    }
    private fun ProductoApi.aProducto() = Producto(id, nombre, descripcion.orEmpty(), (precio * 100).toInt(), stock, categoria.orEmpty(), imagenUrl = imagenUrl, estado = estado)
    private fun PedidoApi.aPedido() = Pedido(
        id, totalCentimos = (total * 100).toInt(), estado = estado, numero = numero,
        fechaPedido = fechaPedido, cantidadUnidadesApi = cantidadUnidades,
        contactoNombre = contactoNombre, contactoTelefono = contactoTelefono,
        direccionEntrega = direccionEntrega, distrito = distrito, referencia = referencia, notas = notas
    )
    private fun PromocionApi.aPromocion() = Promocion(id, titulo, descripcion, descuento, fechaInicio, fechaFin, activo)
    private fun campos(vararg valores: Pair<String, String>): Map<String, RequestBody> = valores.associate { (clave, valor) ->
        clave to valor.toRequestBody("text/plain".toMediaTypeOrNull())
    }
    private fun parteArchivo(nombreCampo: String, archivo: ArchivoAdjunto?): MultipartBody.Part? = archivo?.let {
        MultipartBody.Part.createFormData(nombreCampo, it.nombre, it.contenido.toRequestBody(it.mime.toMediaTypeOrNull()))
    }
}
