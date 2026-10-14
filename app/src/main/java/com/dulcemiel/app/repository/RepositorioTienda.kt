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

class RepositorioTienda(private val api: ApiDulceMiel, private val sesion: Sesion) {
    suspend fun productos(): List<Producto> = api.productos().data.orEmpty().map { it.aProducto() }
    suspend fun producto(id: Int): Producto = api.producto(id).data?.aProducto()
        ?: throw IllegalStateException("No se pudo cargar el producto")
    suspend fun login(email: String, password: String): String? = api.login(LoginBody(email, password)).also {
        if (it.success && it.token != null && it.usuario != null) sesion.iniciar(it.token, it.usuario.nombre, it.usuario.email, it.usuario.rol)
    }.message
    suspend fun registrar(nombre: String, email: String, password: String): String? = api.registrar(RegistroBody(nombre, email, password, password)).message
    fun autenticado() = sesion.token != null
    fun nombreUsuario() = sesion.nombre
    fun emailUsuario() = sesion.email
    fun rolUsuario() = sesion.rol
    fun puedeAdministrar() = sesion.rol in setOf("admin", "empleado")
    fun cerrarSesion() = sesion.cerrar()
    suspend fun perfil(): UsuarioApi? = api.perfil().let { respuesta ->
        respuesta.usuario?.also { sesion.actualizarPerfil(it.nombre, it.email, it.rol) }
    }
    suspend fun resumenDashboard(): ResumenDashboard {
        val respuesta = api.resumen()
        val resumen = respuesta.resumen ?: throw IllegalStateException(respuesta.message ?: "No se pudo cargar el dashboard")
        return ResumenDashboard(
            ventasCentimos = (resumen.ventas * 100).toInt(), pedidos = resumen.pedidos,
            clientes = resumen.clientes, stockBajo = resumen.stockBajo, pedidosPendientes = resumen.pedidosPendientes,
            porEstado = respuesta.porEstado.map { EstadoPedido(it.estado, it.total) },
            recientes = respuesta.recientes.map { PedidoReciente(it.id, it.numero, (it.total * 100).toInt(), it.estado, it.nombre ?: it.email.orEmpty()) }
        )
    }
    suspend fun productosAdministrativos(): List<Producto> = api.todosProductos().data.map { it.aProducto() }
    suspend fun guardarProducto(producto: ProductoEdicion): String? {
        val campos = campos(
            "nombre" to producto.nombre, "descripcion" to producto.descripcion, "precio" to producto.precio,
            "stock" to producto.stock, "categoria" to producto.categoria, "estado" to producto.estado
        )
        val imagen = parteArchivo("imagen", producto.imagen)
        val respuesta = producto.id?.let { api.editarProducto(it, campos, imagen) } ?: api.crearProducto(campos, imagen)
        return respuesta.message
    }
    suspend fun eliminarProducto(id: Int): String? = api.eliminarProducto(id).message
    suspend fun pedidosAdministrativos(): List<PedidoAdministrativo> = api.todosPedidos().data.map {
        PedidoAdministrativo(it.id, it.numero, (it.total * 100).toInt(), it.estado, it.fechaPedido, it.nombre.orEmpty(), it.email.orEmpty())
    }
    suspend fun actualizarEstadoPedido(id: Long, estado: String): String? = api.actualizarPedido(id, EstadoPedidoBody(estado)).message
    suspend fun promocionesAdministrativas(): List<Promocion> = api.promociones().data.map { it.aPromocion() }
    suspend fun guardarPromocion(promocion: Promocion): String? {
        val body = PromocionBody(promocion.titulo, promocion.descripcion, promocion.descuento, promocion.fechaInicio, promocion.fechaFin, promocion.activa)
        return promocion.id.takeIf { it > 0 }?.let { api.editarPromocion(it, body).message } ?: api.crearPromocion(body).message
    }
    suspend fun eliminarPromocion(id: Int): String? = api.eliminarPromocion(id).message
    suspend fun usuariosAdministrativos(): List<UsuarioAdministrativo> = api.usuarios().data.map { UsuarioAdministrativo(it.id, it.nombre, it.email, it.rol, it.estado) }
    suspend fun crearEmpleado(nombre: String, email: String, password: String, rol: String): String? = api.crearEmpleado(EmpleadoBody(nombre, email, password, rol)).message
    suspend fun actualizarUsuario(usuario: UsuarioAdministrativo): String? = api.editarUsuario(usuario.id, UsuarioActualizacionBody(usuario.nombre, usuario.email, usuario.rol, usuario.estado)).message
    suspend fun guardarConfiguracion(configuracion: ConfiguracionSitioEdicion): String? = api.editarSitio(
        campos("nombre_empresa" to configuracion.nombre, "descripcion_inicio" to configuracion.descripcion, "mision" to configuracion.mision, "vision" to configuracion.vision),
        parteArchivo("logo", configuracion.logo)
    ).message
    suspend fun guardarCarrusel(carrusel: CarruselEdicion): String? {
        val campos = campos("titulo" to carrusel.titulo, "descripcion" to carrusel.descripcion, "orden" to carrusel.orden, "activo" to carrusel.activo.toString())
        val imagen = parteArchivo("imagen", carrusel.imagen)
        return carrusel.id?.let { api.editarCarrusel(it, campos, imagen).message } ?: api.crearCarrusel(campos, imagen).message
    }
    suspend fun eliminarCarrusel(id: Int): String? = api.eliminarCarrusel(id).message
    suspend fun carrito(): Pair<List<Producto>, Map<Int, Int>> = api.carrito().let { r -> r.data.map { Producto(it.productoId, it.nombre, "", (it.precio * 100).toInt(), it.stock ?: Int.MAX_VALUE, "", it.id) } to r.data.associate { it.productoId to it.cantidad } }
    suspend fun agregar(id: Int) = api.agregar(CantidadBody(id, 1)).message
    suspend fun actualizar(carritoId: Int, cantidad: Int) = if (cantidad <= 0) api.quitar(carritoId).message else api.actualizar(ActualizarCarritoBody(carritoId, cantidad)).message
    suspend fun quitar(carritoId: Int) = api.quitar(carritoId).message
    suspend fun vaciarCarrito() = api.vaciar().message
    suspend fun pedidos(): List<Pedido> = api.pedidos().data.map { it.aPedido() }
    suspend fun detallePedido(id: Long): DetallePedido {
        val respuesta = api.detallePedido(id)
        val pedido = respuesta.pedido ?: throw IllegalStateException(respuesta.message ?: "No se pudo cargar el pedido")
        return DetallePedido(
            pedido.aPedido(),
            respuesta.detalles.map { LineaPedido(it.id, it.productoId, it.nombre, it.cantidad, (it.precioUnitario * 100).toInt(), (it.subtotal * 100).toInt()) }
        )
    }
    suspend fun crearPedido(entrega: EntregaBody) = api.crearPedido(entrega)
    suspend fun inicio(): InicioTienda = api.inicio().let { respuesta ->
        val config = respuesta.configuracion
        InicioTienda(
            config?.nombreEmpresa ?: "Dulce Miel",
            config?.descripcion ?: "Conoce nuestros productos naturales.",
            config?.mision.orEmpty(), config?.vision.orEmpty(), config?.logoUrl,
            respuesta.carrusel.map { SlideInicio(it.id, it.titulo, it.descripcion, it.imagenUrl, it.orden, it.activo) },
            respuesta.promociones.map { Promocion(it.id, it.titulo, it.descripcion, it.descuento) }
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
