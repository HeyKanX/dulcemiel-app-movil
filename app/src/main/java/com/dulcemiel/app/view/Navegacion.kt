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
import androidx.compose.material.icons.automirrored.filled.Logout
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BarraSuperior(screen: Pantalla, sesionIniciada: Boolean, nombreUsuario: String?, home: () -> Unit, abrirPerfil: () -> Unit, cerrarSesion: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(38.dp).clip(CircleShape).background(Honey), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Hive, contentDescription = null, tint = HoneyDark)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Dulce Miel", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(screen.title(), fontSize = 12.sp, color = Color(0xFF6E6254))
                }
            }
        },
        navigationIcon = {
            if (screen in setOf(Pantalla.DETALLE, Pantalla.DETALLE_PEDIDO, Pantalla.PERFIL, Pantalla.DASHBOARD, Pantalla.ADMIN_PRODUCTOS, Pantalla.ADMIN_PEDIDOS, Pantalla.ADMIN_PROMOCIONES, Pantalla.ADMIN_USUARIOS, Pantalla.ADMIN_SITIO)) IconButton(onClick = home) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al inicio")
            }
        },
        actions = {
            if (sesionIniciada) {
                TextButton(onClick = abrirPerfil, contentPadding = PaddingValues(horizontal = 4.dp)) {
                    Text(nombreUsuario.orEmpty(), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 90.dp))
                }
                IconButton(onClick = cerrarSesion) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
    )
}

private fun Pantalla.title() = when (this) {
    Pantalla.INICIO -> "Compra local y responsable"
    Pantalla.CATALOGO -> "Catálogo"
    Pantalla.CARRITO -> "Tu carrito"
    Pantalla.PEDIDOS -> "Seguimiento"
    Pantalla.DETALLE_PEDIDO -> "Detalle del pedido"
    Pantalla.CONSUMO -> "Consumo"
    Pantalla.DETALLE -> "Detalle del producto"
    Pantalla.LOGIN -> "Tu cuenta"
    Pantalla.PERFIL -> "Mi perfil"
    Pantalla.DASHBOARD -> "Administración"
    Pantalla.ADMIN_PRODUCTOS -> "Administrar productos"
    Pantalla.ADMIN_PEDIDOS -> "Administrar pedidos"
    Pantalla.ADMIN_PROMOCIONES -> "Administrar promociones"
    Pantalla.ADMIN_USUARIOS -> "Administrar usuarios"
    Pantalla.ADMIN_SITIO -> "Inicio y marca"
    Pantalla.CHECKOUT -> "Finalizar pedido"
}

@Composable
internal fun NavegacionInferior(current: Pantalla, count: Int, go: (Pantalla) -> Unit) {
    val items = listOf(
        Triple(Pantalla.INICIO, "Inicio", Icons.Default.Home),
        Triple(Pantalla.CATALOGO, "Catálogo", Icons.Default.Storefront),
        Triple(Pantalla.CARRITO, "Carrito", Icons.Default.ShoppingCart),
        Triple(Pantalla.PEDIDOS, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
        Triple(Pantalla.CONSUMO, "Consumo", Icons.Default.Eco)
    )
    NavigationBar(containerColor = Color.White) {
        items.forEach { (screen, label, icon) ->
            NavigationBarItem(
                selected = current == screen,
                onClick = { go(screen) },
                icon = {
                    if (screen == Pantalla.CARRITO && count > 0) BadgedBox(badge = { Badge { Text("$count") } }) { Icon(icon, contentDescription = label) }
                    else Icon(icon, contentDescription = label)
                },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = HoneyDark, indicatorColor = Color(0xFFFFE39A))
            )
        }
    }
}
