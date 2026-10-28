package com.dulcemiel.app.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dulcemiel.app.model.Producto
import com.dulcemiel.app.model.UbicacionEntrega
import com.dulcemiel.app.network.EntregaBody
import com.dulcemiel.app.network.NetworkConnectivityHelper
import com.dulcemiel.app.repository.RepositorioTienda
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import retrofit2.HttpException

class ModeloVistaTienda(
    private val repo: RepositorioTienda,
    private val conectividad: NetworkConnectivityHelper
) : ViewModel() {
    private var pedidoIdActual: Long? = null
    var productos by mutableStateOf<List<Producto>>(emptyList()); private set
    var estado by mutableStateOf(EstadoAplicacion(
        sesionIniciada = repo.autenticado(),
        nombreUsuario = repo.nombreUsuario(), emailUsuario = repo.emailUsuario(), rolUsuario = repo.rolUsuario()
    )); private set
    init { cargarProductos(); cargarInicio(); if (repo.autenticado()) { cargarPerfil(); refrescarPrivado() } }
    fun cargarProductos() = ejecutar(RecursoUi.CATALOGO) {
        productos = repo.productos()
        estado = estado.copy(catalogoCargado = true)
    }
    private fun cargarInicio() = ejecutar(RecursoUi.INICIO) {
        val inicio = repo.inicio()
        estado=estado.copy(
            nombreEmpresa=inicio.nombreEmpresa, descripcionInicio=inicio.descripcion,
            mision=inicio.mision, vision=inicio.vision, logoUrl=inicio.logoUrl,
            carrusel=inicio.carrusel, promociones=inicio.promociones.filter { it.estaVigente() }
        )
    }
    fun irA(p: Pantalla) {
        val requiereSesion = p == Pantalla.CARRITO || p == Pantalla.PEDIDOS || p == Pantalla.CHECKOUT || p == Pantalla.PERFIL || p == Pantalla.DASHBOARD || p in setOf(Pantalla.ADMIN_PRODUCTOS, Pantalla.ADMIN_PEDIDOS, Pantalla.ADMIN_PROMOCIONES, Pantalla.ADMIN_USUARIOS, Pantalla.ADMIN_SITIO)
        estado = if (requiereSesion && !repo.autenticado()) {
            estado.copy(pantalla=Pantalla.LOGIN, productoSeleccionado=null, detallePedido=null, mensaje="Inicia sesión para acceder a esta sección")
        } else if (p in setOf(Pantalla.DASHBOARD, Pantalla.ADMIN_PRODUCTOS, Pantalla.ADMIN_PEDIDOS, Pantalla.ADMIN_PROMOCIONES, Pantalla.ADMIN_USUARIOS, Pantalla.ADMIN_SITIO) && !repo.puedeAdministrar()) {
            estado.copy(pantalla=Pantalla.INICIO, productoSeleccionado=null, detallePedido=null, mensaje="No tienes permisos para acceder al panel administrativo")
        } else estado.copy(pantalla=p, productoSeleccionado=null, detallePedido=null, mensaje=null)
    }
    fun abrir(p: Producto) {
        estado = estado.copy(pantalla=Pantalla.DETALLE, productoSeleccionado=p)
        ejecutar(RecursoUi.DETALLE_PRODUCTO) { estado = estado.copy(productoSeleccionado=repo.producto(p.id)) }
    }
    fun abrirPedido(pedidoId: Long) {
        pedidoIdActual = pedidoId
        estado = estado.copy(pantalla=Pantalla.DETALLE_PEDIDO, detallePedido=null, mensaje=null)
        ejecutar(RecursoUi.DETALLE_PEDIDO) { estado = estado.copy(detallePedido=repo.detallePedido(pedidoId)) }
    }
    fun reintentar(recurso: RecursoUi) {
        when (recurso) {
            RecursoUi.INICIO -> cargarInicio()
            RecursoUi.CATALOGO -> cargarProductos()
            RecursoUi.DETALLE_PRODUCTO -> if (estado.pantalla == Pantalla.DETALLE) {
                estado.productoSeleccionado?.let { producto -> ejecutar(RecursoUi.DETALLE_PRODUCTO) { estado=estado.copy(productoSeleccionado=repo.producto(producto.id)) } }
                    ?: cargarProductos()
            }
            RecursoUi.PERFIL -> cargarPerfil()
            RecursoUi.CARRITO -> cargarCarrito()
            RecursoUi.PEDIDOS -> cargarPedidos()
            RecursoUi.DETALLE_PEDIDO -> pedidoIdActual?.let(::abrirPedido)
            RecursoUi.DASHBOARD -> abrirDashboard()
            RecursoUi.ADMIN_PRODUCTOS -> cargarProductosAdministrativos()
            RecursoUi.ADMIN_PEDIDOS -> cargarPedidosAdministrativos()
            RecursoUi.ADMIN_PROMOCIONES -> cargarPromocionesAdministrativas()
            RecursoUi.ADMIN_USUARIOS -> cargarUsuariosAdministrativos()
        }
    }
    fun abrirPerfil() { irA(Pantalla.PERFIL); if (repo.autenticado()) cargarPerfil() }
    fun abrirDashboard() {
        irA(Pantalla.DASHBOARD)
        if (estado.pantalla == Pantalla.DASHBOARD) ejecutar(RecursoUi.DASHBOARD) { estado=estado.copy(resumenDashboard=repo.resumenDashboard()) }
    }
    fun abrirProductosAdministrativos() {
        irA(Pantalla.ADMIN_PRODUCTOS)
        if (estado.pantalla == Pantalla.ADMIN_PRODUCTOS) cargarProductosAdministrativos()
    }
    fun abrirPedidosAdministrativos() {
        irA(Pantalla.ADMIN_PEDIDOS)
        if (estado.pantalla == Pantalla.ADMIN_PEDIDOS) cargarPedidosAdministrativos()
    }
    fun abrirPromocionesAdministrativas() {
        irA(Pantalla.ADMIN_PROMOCIONES)
        if (estado.pantalla == Pantalla.ADMIN_PROMOCIONES) cargarPromocionesAdministrativas()
    }
    fun abrirUsuariosAdministrativos() {
        irA(Pantalla.ADMIN_USUARIOS)
        if (estado.pantalla == Pantalla.ADMIN_USUARIOS) cargarUsuariosAdministrativos()
    }
    fun abrirAdministracionSitio() { irA(Pantalla.ADMIN_SITIO) }
    private fun cargarProductosAdministrativos() = ejecutar(RecursoUi.ADMIN_PRODUCTOS) { estado=estado.copy(productosAdministrativos=repo.productosAdministrativos()) }
    private fun cargarPedidosAdministrativos() = ejecutar(RecursoUi.ADMIN_PEDIDOS) { estado=estado.copy(pedidosAdministrativos=repo.pedidosAdministrativos()) }
    private fun cargarPromocionesAdministrativas() = ejecutar(RecursoUi.ADMIN_PROMOCIONES) { estado=estado.copy(promocionesAdministrativas=repo.promocionesAdministrativas()) }
    private fun cargarUsuariosAdministrativos() = ejecutar(RecursoUi.ADMIN_USUARIOS) { estado=estado.copy(usuariosAdministrativos=repo.usuariosAdministrativos()) }
    fun guardarProducto(producto: com.dulcemiel.app.model.ProductoEdicion) {
        if (producto.nombre.isBlank() || producto.precio.toDoubleOrNull()?.let { it >= 0 } != true || producto.stock.toIntOrNull()?.let { it >= 0 } != true) {
            estado=estado.copy(mensaje="Completa nombre, precio y stock con valores válidos")
            return
        }
        ejecutar {
            repo.guardarProducto(producto)
            cargarProductosAdministrativos()
            cargarProductos()
            estado=estado.copy(mensaje=if (producto.id == null) "Producto creado" else "Producto actualizado")
        }
    }
    fun eliminarProducto(id: Int) = ejecutar {
        repo.eliminarProducto(id)
        cargarProductosAdministrativos()
        cargarProductos()
        estado=estado.copy(mensaje="Producto eliminado")
    }
    fun actualizarEstadoPedido(id: Long, nuevoEstado: String) = ejecutar {
        repo.actualizarEstadoPedido(id, nuevoEstado)
        cargarPedidosAdministrativos()
        estado=estado.copy(mensaje="Estado del pedido actualizado")
    }
    fun guardarPromocion(promocion: com.dulcemiel.app.model.Promocion) {
        if (promocion.titulo.isBlank() || promocion.fechaInicio.isBlank() || promocion.descuento !in 0.0..100.0) {
            estado=estado.copy(mensaje="Completa título, descuento válido y fecha de inicio")
            return
        }
        ejecutar { repo.guardarPromocion(promocion); cargarPromocionesAdministrativas(); estado=estado.copy(mensaje="Promoción guardada") }
    }
    fun eliminarPromocion(id: Int) = ejecutar { repo.eliminarPromocion(id); cargarPromocionesAdministrativas(); estado=estado.copy(mensaje="Promoción eliminada") }
    fun crearEmpleado(nombre: String, email: String, password: String, rol: String) {
        if (estado.rolUsuario != "admin") { estado=estado.copy(mensaje="Solo el administrador principal puede crear empleados"); return }
        if (nombre.isBlank() || email.isBlank() || password.length < 6) { estado=estado.copy(mensaje="Completa nombre, correo y contraseña de al menos 6 caracteres"); return }
        ejecutar { repo.crearEmpleado(nombre, email, password, rol); cargarUsuariosAdministrativos(); estado=estado.copy(mensaje="Empleado creado") }
    }
    fun actualizarUsuario(usuario: com.dulcemiel.app.model.UsuarioAdministrativo) {
        if (estado.rolUsuario != "admin") { estado=estado.copy(mensaje="Solo el administrador principal puede editar usuarios"); return }
        ejecutar { repo.actualizarUsuario(usuario); cargarUsuariosAdministrativos(); estado=estado.copy(mensaje="Usuario actualizado") }
    }
    fun guardarConfiguracionSitio(configuracion: com.dulcemiel.app.model.ConfiguracionSitioEdicion) {
        if (configuracion.nombre.isBlank() || configuracion.descripcion.isBlank() || configuracion.mision.isBlank() || configuracion.vision.isBlank()) { estado=estado.copy(mensaje="Completa todos los datos de la marca"); return }
        ejecutar { repo.guardarConfiguracion(configuracion); cargarInicio(); estado=estado.copy(mensaje="Configuración actualizada") }
    }
    fun guardarCarrusel(carrusel: com.dulcemiel.app.model.CarruselEdicion) {
        if (carrusel.titulo.isBlank() || carrusel.orden.toIntOrNull() == null || (carrusel.id == null && carrusel.imagen == null)) { estado=estado.copy(mensaje="Título, orden e imagen son requeridos para crear un carrusel"); return }
        ejecutar { repo.guardarCarrusel(carrusel); cargarInicio(); estado=estado.copy(mensaje="Carrusel actualizado") }
    }
    fun eliminarCarrusel(id: Int) = ejecutar { repo.eliminarCarrusel(id); cargarInicio(); estado=estado.copy(mensaje="Imagen de carrusel eliminada") }
    private fun cargarPerfil() = ejecutar(RecursoUi.PERFIL) {
        repo.perfil()?.let { usuario ->
            estado=estado.copy(nombreUsuario=usuario.nombre,emailUsuario=usuario.email,rolUsuario=usuario.rol,sesionIniciada=true)
        }
    }
    private fun cargarPedidos() = ejecutar(RecursoUi.PEDIDOS) { estado=estado.copy(pedidos=repo.pedidos()) }
    fun login(e:String,p:String) = ejecutar(cerrarSesionAlRecibir401 = false) {
        val mensaje=repo.login(e,p)
        if(repo.autenticado()) {
            refrescarPrivado()
            estado=estado.copy(sesionIniciada=true,nombreUsuario=repo.nombreUsuario(),emailUsuario=repo.emailUsuario(),rolUsuario=repo.rolUsuario(),mensaje="Sesión iniciada",pantalla=Pantalla.INICIO)
        } else estado=estado.copy(mensaje=mensaje ?: "No se pudo iniciar sesión")
    }
    fun registrar(n:String,e:String,p:String) = ejecutar { estado=estado.copy(mensaje=repo.registrar(n,e,p) ?: "Cuenta creada. Ahora inicia sesión.") }
    fun cerrarSesion() {
        repo.cerrarSesion()
        estado=EstadoAplicacion(nombreEmpresa=estado.nombreEmpresa, descripcionInicio=estado.descripcionInicio,
            mision=estado.mision, vision=estado.vision, logoUrl=estado.logoUrl, carrusel=estado.carrusel, promociones=estado.promociones,
            pantalla=Pantalla.INICIO, mensaje="Sesión cerrada correctamente")
    }
    fun limpiarMensaje() { estado = estado.copy(mensaje=null) }
    fun agregar(p:Producto) = ejecutar {
        if(!repo.autenticado()){ irA(Pantalla.LOGIN); estado=estado.copy(mensaje="Inicia sesión para agregar productos al carrito"); return@ejecutar }
        if ((estado.carrito[p.id] ?: 0) >= p.existencias) {
            estado=estado.copy(mensaje="No hay más existencias disponibles de ${p.nombre}")
            return@ejecutar
        }
        repo.agregar(p.id)
        cargarCarrito()
    }
    fun quitar(p:Producto) = ejecutar(RecursoUi.CARRITO) { p.carritoId?.let { repo.actualizar(it,(estado.carrito[p.id]?:1)-1) }; cargarCarrito() }
    fun eliminarDelCarrito(p: Producto) = ejecutar(RecursoUi.CARRITO) {
        p.carritoId?.let { repo.quitar(it) }
        cargarCarrito()
    }
    fun vaciarCarrito() = ejecutar(RecursoUi.CARRITO) {
        repo.vaciarCarrito()
        estado=estado.copy(carrito=emptyMap(), lineasCarrito=emptyList(), totalCarritoCentimos=0, mensaje="Carrito vaciado")
    }
    fun registrarPedido(n:String,t:String,e:String?,d:String,di:String,r:String?,notas:String?,ubicacion:UbicacionEntrega?) = ejecutar {
        val referenciaMapa = ubicacion?.let { "Ubicación de entrega: ${it.enlaceMapa()}" }
        val referenciaCompleta = listOfNotNull(r?.trim()?.takeIf { it.isNotBlank() }, referenciaMapa)
            .joinToString(" | ")
            .takeIf { it.isNotBlank() }
        val respuesta=repo.crearPedido(EntregaBody(n,t,e,d,di,referenciaCompleta,notas))
        if (!respuesta.success) {
            estado=estado.copy(mensaje=respuesta.message ?: "No se pudo enviar el pedido")
            return@ejecutar
        }
        refrescarPrivado()
        estado=estado.copy(pantalla=Pantalla.PEDIDOS,mensaje="Pedido enviado a Dulce Miel")
    }
    fun cantidadCarrito()=estado.carrito.values.sum()
    private fun refrescarPrivado(){ cargarCarrito(); cargarPedidos() }
    private fun cargarCarrito()=ejecutar(RecursoUi.CARRITO){
        val (c, m, totalServidor) = repo.carrito()
        val lineas = c.map { productoCarrito ->
            val productoCatalogo = productos.firstOrNull { it.id == productoCarrito.id }
            val producto = productoCatalogo?.let {
                productoCarrito.copy(descripcion=it.descripcion, existencias=it.existencias, etiqueta=it.etiqueta)
            } ?: productoCarrito
            LineaCarrito(producto, m[producto.id] ?: 0)
        }
        estado=estado.copy(carrito=m,lineasCarrito=lineas,totalCarritoCentimos=totalServidor)
    }
    private fun ejecutar(
        recurso: RecursoUi? = null,
        cerrarSesionAlRecibir401: Boolean = true,
        b:suspend()->Unit
    )=viewModelScope.launch{
        estado = estado.copy(operacionesActivas = estado.operacionesActivas + 1)
        recurso?.let { actualizarEstadoRecurso(it, estado.estadoRecurso(it).iniciarCarga()) }
        try {
            if (!conectividad.hayConexionInternet()) throw SinConexionException()
            b()
            recurso?.let { actualizarEstadoRecurso(it, estado.estadoRecurso(it).completarCarga(recursoTieneDatos(it))) }
        }
        catch(e: HttpException) {
            if (e.code() == 401 && cerrarSesionAlRecibir401) {
                repo.cerrarSesion()
                estado=estado.copy(sesionIniciada=false,nombreUsuario=null,emailUsuario=null,rolUsuario=null,pantalla=Pantalla.LOGIN,mensaje="Tu sesión venció. Ingresa nuevamente.")
                recurso?.let { actualizarEstadoRecurso(it, estado.estadoRecurso(it).fallarCarga("Tu sesión venció. Ingresa nuevamente.")) }
            } else registrarError(recurso, mensajeHttpParaUsuario(e, cerrarSesionAlRecibir401))
        }
        catch(e: SinConexionException) { registrarError(recurso, "Sin conexión a Internet. Revisa tu red e inténtalo nuevamente.", sinConexion = true) }
        catch(e: SocketTimeoutException) { registrarError(recurso, "Dulce Miel tardó demasiado en responder. Inténtalo nuevamente.") }
        catch(e: UnknownHostException) { registrarError(recurso, "No se pudo encontrar el servidor. Revisa tu conexión a Internet.", sinConexion = true) }
        catch(e: IOException) { registrarError(recurso, "No se pudo conectar con Dulce Miel. Revisa tu conexión e inténtalo nuevamente.", sinConexion = true) }
        catch(e: Exception) { registrarError(recurso, "No se pudo completar la acción. Inténtalo nuevamente.") }
        finally { estado=estado.copy(operacionesActivas=(estado.operacionesActivas - 1).coerceAtLeast(0)) }
    }
    private fun registrarError(recurso: RecursoUi?, mensaje: String, sinConexion: Boolean = false) {
        if (recurso == null) estado = estado.copy(mensaje = mensaje)
        else actualizarEstadoRecurso(recurso, estado.estadoRecurso(recurso).fallarCarga(mensaje, sinConexion))
    }
    private fun actualizarEstadoRecurso(recurso: RecursoUi, valor: EstadoRecurso) {
        estado = estado.copy(estadosRecursos = estado.estadosRecursos + (recurso to valor))
    }
    private fun EstadoAplicacion.estadoRecurso(recurso: RecursoUi): EstadoRecurso =
        estadosRecursos[recurso] ?: EstadoRecurso.Inicial
    private fun recursoTieneDatos(recurso: RecursoUi): Boolean = when (recurso) {
        RecursoUi.INICIO -> true
        RecursoUi.CATALOGO -> productos.isNotEmpty()
        RecursoUi.DETALLE_PRODUCTO -> estado.productoSeleccionado != null
        RecursoUi.PERFIL -> estado.nombreUsuario != null
        RecursoUi.CARRITO -> estado.lineasCarrito.isNotEmpty()
        RecursoUi.PEDIDOS -> estado.pedidos.isNotEmpty()
        RecursoUi.DETALLE_PEDIDO -> estado.detallePedido != null
        RecursoUi.DASHBOARD -> estado.resumenDashboard != null
        RecursoUi.ADMIN_PRODUCTOS -> estado.productosAdministrativos.isNotEmpty()
        RecursoUi.ADMIN_PEDIDOS -> estado.pedidosAdministrativos.isNotEmpty()
        RecursoUi.ADMIN_PROMOCIONES -> estado.promocionesAdministrativas.isNotEmpty()
        RecursoUi.ADMIN_USUARIOS -> estado.usuariosAdministrativos.isNotEmpty()
    }
    private class SinConexionException : Exception()
    private fun com.dulcemiel.app.model.Promocion.estaVigente(hoy: LocalDate = LocalDate.now()): Boolean {
        fun fecha(valor: String?): LocalDate? = valor?.take(10)?.let { runCatching { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }.getOrNull() }
        return activa && (fecha(fechaInicio)?.let { !it.isAfter(hoy) } ?: true) && (fecha(fechaFin)?.let { !it.isBefore(hoy) } ?: true)
    }
}

internal fun mensajeHttpParaUsuario(error: HttpException, cerrarSesionAlRecibir401: Boolean): String {
    val mensajeApi = runCatching {
        error.response()?.errorBody()?.string()?.let { cuerpo ->
            JsonParser.parseString(cuerpo).asJsonObject.get("message")?.asString
        }
    }.getOrNull()?.takeIf { it.isNotBlank() }
    val predeterminado = when (error.code()) {
        400 -> "Los datos enviados no son válidos."
        401 -> if (cerrarSesionAlRecibir401) "Tu sesión venció. Ingresa nuevamente." else "Correo o contraseña incorrectos."
        403 -> "No tienes permisos para realizar esta acción."
        404 -> "No se encontró el recurso solicitado."
        in 500..599 -> "El servidor presentó un problema. Inténtalo más tarde."
        else -> "No se pudo completar la acción."
    }
    return mensajeApi ?: "$predeterminado (HTTP ${error.code()})"
}

class FabricaModeloVistaTienda(
    private val repositorio: RepositorioTienda,
    private val conectividad: NetworkConnectivityHelper
):ViewModelProvider.Factory{ @Suppress("UNCHECKED_CAST") override fun<T:ViewModel> create(modelClass:Class<T>):T=ModeloVistaTienda(repositorio, conectividad) as T }
