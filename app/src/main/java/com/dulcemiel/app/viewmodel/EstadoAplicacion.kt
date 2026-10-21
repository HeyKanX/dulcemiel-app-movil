package com.dulcemiel.app.viewmodel

import com.dulcemiel.app.model.Pedido
import com.dulcemiel.app.model.DetallePedido
import com.dulcemiel.app.model.Producto
import com.dulcemiel.app.model.SlideInicio
import com.dulcemiel.app.model.Promocion
import com.dulcemiel.app.model.ResumenDashboard
import com.dulcemiel.app.model.PedidoAdministrativo
import com.dulcemiel.app.model.UsuarioAdministrativo

enum class RecursoUi {
    INICIO,
    CATALOGO,
    DETALLE_PRODUCTO,
    PERFIL,
    CARRITO,
    PEDIDOS,
    DETALLE_PEDIDO,
    DASHBOARD,
    ADMIN_PRODUCTOS,
    ADMIN_PEDIDOS,
    ADMIN_PROMOCIONES,
    ADMIN_USUARIOS
}

sealed interface EstadoRecurso {
    data object Inicial : EstadoRecurso
    data object Cargando : EstadoRecurso
    data object Contenido : EstadoRecurso
    data object Vacio : EstadoRecurso
    data class Error(val mensaje: String, val sinConexion: Boolean = false) : EstadoRecurso
}

fun EstadoRecurso.iniciarCarga(): EstadoRecurso = EstadoRecurso.Cargando

fun EstadoRecurso.completarCarga(tieneDatos: Boolean): EstadoRecurso =
    if (tieneDatos) EstadoRecurso.Contenido else EstadoRecurso.Vacio

fun EstadoRecurso.fallarCarga(mensaje: String, sinConexion: Boolean = false): EstadoRecurso =
    EstadoRecurso.Error(mensaje, sinConexion)

data class EstadoAplicacion(
    val pantalla: Pantalla = Pantalla.INICIO,
    val productoSeleccionado: Producto? = null,
    val carrito: Map<Int, Int> = emptyMap(),
    val lineasCarrito: List<LineaCarrito> = emptyList(),
    val totalCarritoCentimos: Int = 0,
    val pedidos: List<Pedido> = emptyList(),
    val detallePedido: DetallePedido? = null,
    val nombreEmpresa: String = "Dulce Miel",
    val catalogoCargado: Boolean = false,
    val descripcionInicio: String = "Conoce nuestros productos naturales.",
    val mision: String = "",
    val vision: String = "",
    val logoUrl: String? = null,
    val carrusel: List<SlideInicio> = emptyList(),
    val promociones: List<Promocion> = emptyList(),
    val sesionIniciada: Boolean = false,
    val nombreUsuario: String? = null,
    val emailUsuario: String? = null,
    val rolUsuario: String? = null,
    val resumenDashboard: ResumenDashboard? = null,
    val productosAdministrativos: List<Producto> = emptyList(),
    val pedidosAdministrativos: List<PedidoAdministrativo> = emptyList(),
    val promocionesAdministrativas: List<Promocion> = emptyList(),
    val usuariosAdministrativos: List<UsuarioAdministrativo> = emptyList(),
    val operacionesActivas: Int = 0,
    val estadosRecursos: Map<RecursoUi, EstadoRecurso> = emptyMap(),
    val mensaje: String? = null
) {
    val cargando: Boolean get() = operacionesActivas > 0
}
