package com.dulcemiel.app.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dulcemiel.app.model.Producto
import com.dulcemiel.app.network.EntregaBody
import com.dulcemiel.app.repository.RepositorioTienda
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ModeloVistaTienda(private val repo: RepositorioTienda) : ViewModel() {
    var productos by mutableStateOf<List<Producto>>(emptyList()); private set
    var estado by mutableStateOf(EstadoAplicacion(
        sesionIniciada = repo.autenticado(),
        nombreUsuario = repo.nombreUsuario(), emailUsuario = repo.emailUsuario(), rolUsuario = repo.rolUsuario()
    )); private set
    init { cargarProductos(); cargarInicio(); if (repo.autenticado()) { cargarPerfil(); refrescarPrivado() } }
    fun cargarProductos() = ejecutar { productos = repo.productos() }
    private fun cargarInicio() = ejecutar {
        val inicio = repo.inicio()
        estado=estado.copy(
            nombreEmpresa=inicio.nombreEmpresa, descripcionInicio=inicio.descripcion,
            mision=inicio.mision, vision=inicio.vision, logoUrl=inicio.logoUrl,
            carrusel=inicio.carrusel, promociones=inicio.promociones
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
        ejecutar { estado = estado.copy(productoSeleccionado=repo.producto(p.id)) }
    }
    fun abrirPedido(pedidoId: Long) {
        estado = estado.copy(pantalla=Pantalla.DETALLE_PEDIDO, detallePedido=null, mensaje=null)
        ejecutar { estado = estado.copy(detallePedido=repo.detallePedido(pedidoId)) }
    }
    fun abrirPerfil() { irA(Pantalla.PERFIL); if (repo.autenticado()) cargarPerfil() }
    fun abrirDashboard() {
        irA(Pantalla.DASHBOARD)
        if (estado.pantalla == Pantalla.DASHBOARD) ejecutar { estado=estado.copy(resumenDashboard=repo.resumenDashboard()) }
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
    private fun cargarProductosAdministrativos() = ejecutar { estado=estado.copy(productosAdministrativos=repo.productosAdministrativos()) }
    private fun cargarPedidosAdministrativos() = ejecutar { estado=estado.copy(pedidosAdministrativos=repo.pedidosAdministrativos()) }
    private fun cargarPromocionesAdministrativas() = ejecutar { estado=estado.copy(promocionesAdministrativas=repo.promocionesAdministrativas()) }
    private fun cargarUsuariosAdministrativos() = ejecutar { estado=estado.copy(usuariosAdministrativos=repo.usuariosAdministrativos()) }
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
    private fun cargarPerfil() = ejecutar {
        repo.perfil()?.let { usuario ->
            estado=estado.copy(nombreUsuario=usuario.nombre,emailUsuario=usuario.email,rolUsuario=usuario.rol,sesionIniciada=true)
        }
    }
    fun login(e:String,p:String) = ejecutar {
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
    fun quitar(p:Producto) = ejecutar { p.carritoId?.let { repo.actualizar(it,(estado.carrito[p.id]?:1)-1) }; cargarCarrito() }
    fun eliminarDelCarrito(p: Producto) = ejecutar {
        p.carritoId?.let { repo.quitar(it) }
        cargarCarrito()
    }
    fun vaciarCarrito() = ejecutar {
        repo.vaciarCarrito()
        estado=estado.copy(carrito=emptyMap(), lineasCarrito=emptyList(), totalCarritoCentimos=0, mensaje="Carrito vaciado")
    }
    fun registrarPedido(n:String,t:String,e:String?,d:String,di:String,r:String?,notas:String?) = ejecutar {
        val respuesta=repo.crearPedido(EntregaBody(n,t,e,d,di,r,notas))
        if (!respuesta.success) {
            estado=estado.copy(mensaje=respuesta.message ?: "No se pudo enviar el pedido")
            return@ejecutar
        }
        refrescarPrivado()
        estado=estado.copy(pantalla=Pantalla.PEDIDOS,mensaje="Pedido enviado a Dulce Miel")
    }
    fun cantidadCarrito()=estado.carrito.values.sum()
    private fun refrescarPrivado(){ cargarCarrito(); ejecutar{ estado=estado.copy(pedidos=repo.pedidos()) } }
    private fun cargarCarrito()=ejecutar{
        val(c,m)=repo.carrito()
        val lineas = c.map { productoCarrito ->
            val productoCatalogo = productos.firstOrNull { it.id == productoCarrito.id }
            val producto = productoCatalogo?.let {
                productoCarrito.copy(descripcion=it.descripcion, existencias=it.existencias, etiqueta=it.etiqueta)
            } ?: productoCarrito
            LineaCarrito(producto, m[producto.id] ?: 0)
        }
        estado=estado.copy(carrito=m,lineasCarrito=lineas,totalCarritoCentimos=lineas.sumOf{it.producto.precioCentimos*it.cantidad})
    }
    private fun ejecutar(b:suspend()->Unit)=viewModelScope.launch{
        estado=estado.copy(cargando=true)
        try { b() }
        catch(e: HttpException) {
            if (e.code() == 401) {
                repo.cerrarSesion()
                estado=estado.copy(sesionIniciada=false,nombreUsuario=null,emailUsuario=null,rolUsuario=null,pantalla=Pantalla.LOGIN,mensaje="Tu sesión venció. Ingresa nuevamente.")
            } else estado=estado.copy(mensaje="No se pudo completar la acción. Inténtalo nuevamente.")
        }
        catch(e: Exception) { estado=estado.copy(mensaje="No se pudo conectar con Dulce Miel. Revisa tu conexión e inténtalo nuevamente.") }
        finally { estado=estado.copy(cargando=false) }
    }
}
class FabricaModeloVistaTienda(private val r:RepositorioTienda):ViewModelProvider.Factory{ @Suppress("UNCHECKED_CAST") override fun<T:ViewModel> create(modelClass:Class<T>):T=ModeloVistaTienda(r) as T }
