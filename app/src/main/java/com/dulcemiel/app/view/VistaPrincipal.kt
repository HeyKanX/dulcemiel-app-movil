package com.dulcemiel.app.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dulcemiel.app.model.Pedido
import com.dulcemiel.app.model.Producto
import com.dulcemiel.app.ui.theme.Miel as Honey
import com.dulcemiel.app.ui.theme.MielOscura as HoneyDark
import com.dulcemiel.app.ui.theme.Crema as Cream
import com.dulcemiel.app.ui.theme.Hoja as Leaf
import com.dulcemiel.app.ui.theme.Tinta as Ink
import com.dulcemiel.app.viewmodel.ModeloVistaTienda
import com.dulcemiel.app.viewmodel.Pantalla
import com.dulcemiel.app.viewmodel.RecursoUi
import com.dulcemiel.app.viewmodel.EstadoRecurso

@Composable
fun DulceMielApp(vm: ModeloVistaTienda) {
    val state = vm.estado
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }
    Scaffold(
        containerColor = Cream,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            BarraSuperior(
                state.pantalla,
                state.sesionIniciada,
                state.nombreUsuario,
                { vm.irA(Pantalla.INICIO) },
                vm::abrirPerfil,
                vm::cerrarSesion
            )
        },
        bottomBar = {
            if (state.pantalla !in setOf(Pantalla.DETALLE, Pantalla.DETALLE_PEDIDO, Pantalla.PERFIL, Pantalla.DASHBOARD, Pantalla.ADMIN_PRODUCTOS, Pantalla.ADMIN_PEDIDOS, Pantalla.ADMIN_PROMOCIONES, Pantalla.ADMIN_USUARIOS, Pantalla.ADMIN_SITIO)) NavegacionInferior(state.pantalla, vm.cantidadCarrito(), vm::irA)
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (state.pantalla) {
                Pantalla.INICIO -> ContenidoConEstado(state.estado(RecursoUi.INICIO), "No se pudo cargar el inicio.", { vm.reintentar(RecursoUi.INICIO) }) {
                    PantallaInicio(
                        vm.productos, state.nombreEmpresa, state.descripcionInicio, state.logoUrl,
                        state.carrusel, state.promociones, state.mision, state.vision, vm::abrir, vm::irA
                    )
                }
                Pantalla.CATALOGO -> ContenidoConEstado(state.estado(RecursoUi.CATALOGO), "No hay productos disponibles.", { vm.reintentar(RecursoUi.CATALOGO) }) {
                    PantallaCatalogo(
                        vm.productos,
                        state.catalogoCargado,
                        state.cargando,
                        vm::abrir,
                        vm::agregar,
                        vm::cargarProductos
                    )
                }
                Pantalla.CARRITO -> ContenidoConEstado(state.estado(RecursoUi.CARRITO), "Tu carrito está vacío.", { vm.reintentar(RecursoUi.CARRITO) }) {
                    PantallaCarrito(state.lineasCarrito, state.totalCarritoCentimos, vm::agregar, vm::quitar, vm::eliminarDelCarrito, vm::vaciarCarrito) { vm.irA(Pantalla.CHECKOUT) }
                }
                Pantalla.PEDIDOS -> ContenidoConEstado(state.estado(RecursoUi.PEDIDOS), "Aún no tienes pedidos registrados.", { vm.reintentar(RecursoUi.PEDIDOS) }) {
                    PantallaPedidos(state.pedidos, vm::abrirPedido)
                }
                Pantalla.DETALLE_PEDIDO -> ContenidoConEstado(state.estado(RecursoUi.DETALLE_PEDIDO), "El pedido no contiene información para mostrar.", { vm.reintentar(RecursoUi.DETALLE_PEDIDO) }) {
                    PantallaDetallePedido(state.detallePedido)
                }
                Pantalla.CONSUMO -> PantallaConsumo()
                Pantalla.DETALLE -> ContenidoConEstado(state.estado(RecursoUi.DETALLE_PRODUCTO), "No se pudo cargar el producto.", { vm.reintentar(RecursoUi.DETALLE_PRODUCTO) }) {
                    state.productoSeleccionado?.let { PantallaDetalle(it, vm::agregar) }
                }
                Pantalla.LOGIN -> PantallaLogin(vm::login, vm::registrar)
                Pantalla.PERFIL -> ContenidoConEstado(state.estado(RecursoUi.PERFIL), "No se pudo cargar tu perfil.", { vm.reintentar(RecursoUi.PERFIL) }) {
                    PantallaPerfil(state.nombreUsuario, state.emailUsuario, state.rolUsuario, state.rolUsuario in setOf("admin", "empleado"), vm::abrirDashboard)
                }
                Pantalla.DASHBOARD -> ContenidoConEstado(state.estado(RecursoUi.DASHBOARD), "No hay actividad para resumir todavía.", { vm.reintentar(RecursoUi.DASHBOARD) }) {
                    PantallaDashboard(state.resumenDashboard, vm::abrirProductosAdministrativos, vm::abrirPedidosAdministrativos, vm::abrirPromocionesAdministrativas, vm::abrirUsuariosAdministrativos, vm::abrirAdministracionSitio)
                }
                Pantalla.ADMIN_PRODUCTOS -> ContenidoConEstado(state.estado(RecursoUi.ADMIN_PRODUCTOS), "No hay productos registrados.", { vm.reintentar(RecursoUi.ADMIN_PRODUCTOS) }, mostrarContenidoSinDatos = true) {
                    PantallaGestionProductos(state.productosAdministrativos, vm::guardarProducto, vm::eliminarProducto)
                }
                Pantalla.ADMIN_PEDIDOS -> ContenidoConEstado(state.estado(RecursoUi.ADMIN_PEDIDOS), "No hay pedidos para mostrar.", { vm.reintentar(RecursoUi.ADMIN_PEDIDOS) }) {
                    PantallaGestionPedidos(state.pedidosAdministrativos, vm::abrirPedido, vm::actualizarEstadoPedido)
                }
                Pantalla.ADMIN_PROMOCIONES -> ContenidoConEstado(state.estado(RecursoUi.ADMIN_PROMOCIONES), "No hay promociones registradas.", { vm.reintentar(RecursoUi.ADMIN_PROMOCIONES) }, mostrarContenidoSinDatos = true) {
                    PantallaGestionPromociones(state.promocionesAdministrativas, vm::guardarPromocion, vm::eliminarPromocion)
                }
                Pantalla.ADMIN_USUARIOS -> ContenidoConEstado(state.estado(RecursoUi.ADMIN_USUARIOS), "No hay usuarios para mostrar.", { vm.reintentar(RecursoUi.ADMIN_USUARIOS) }, mostrarContenidoSinDatos = true) {
                    PantallaGestionUsuarios(state.usuariosAdministrativos, state.rolUsuario == "admin", vm::crearEmpleado, vm::actualizarUsuario)
                }
                Pantalla.ADMIN_SITIO -> ContenidoConEstado(state.estado(RecursoUi.INICIO), "No se pudo cargar la configuración del sitio.", { vm.reintentar(RecursoUi.INICIO) }) {
                    PantallaGestionSitio(state.nombreEmpresa, state.descripcionInicio, state.mision, state.vision, state.logoUrl, state.carrusel, vm::guardarConfiguracionSitio, vm::guardarCarrusel, vm::eliminarCarrusel)
                }
                Pantalla.CHECKOUT -> PantallaCheckout(vm::registrarPedido)
            }
            if (state.cargando) {
                Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .12f)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HoneyDark)
                }
            }
        }
    }
}

private fun com.dulcemiel.app.viewmodel.EstadoAplicacion.estado(recurso: RecursoUi): EstadoRecurso =
    estadosRecursos[recurso] ?: EstadoRecurso.Inicial
