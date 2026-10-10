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

@Composable
fun DulceMielApp(vm: ModeloVistaTienda) {
    val state = vm.estado
    Scaffold(
        containerColor = Cream,
        topBar = { BarraSuperior(state.pantalla) { vm.irA(Pantalla.INICIO) } },
        bottomBar = {
            if (state.pantalla != Pantalla.DETALLE) NavegacionInferior(state.pantalla, vm.cantidadCarrito(), vm::irA)
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (state.pantalla) {
                Pantalla.INICIO -> PantallaInicio(vm.productos, vm::abrir, vm::irA)
                Pantalla.CATALOGO -> PantallaCatalogo(vm.productos, vm::abrir, vm::agregar)
                Pantalla.CARRITO -> PantallaCarrito(state.lineasCarrito, state.totalCarritoCentimos, vm::agregar, vm::quitar, vm::registrarPedido)
                Pantalla.PEDIDOS -> PantallaPedidos(state.pedidos, state.mensaje)
                Pantalla.CONSUMO -> PantallaConsumo()
                Pantalla.DETALLE -> state.productoSeleccionado?.let { PantallaDetalle(it, vm::agregar) }
            }
        }
    }
}
