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
import androidx.compose.ui.layout.ContentScale
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
import com.dulcemiel.app.viewmodel.EstadoRecurso
import coil3.compose.AsyncImage

@Composable
internal fun TituloSeccion(title: String, subtitle: String) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 21.sp)
        Text(subtitle, fontSize = 13.sp, color = Color(0xFF766B5E))
    }
}

@Composable
internal fun ContenidoConEstado(
    estado: EstadoRecurso,
    mensajeVacio: String,
    reintentar: () -> Unit,
    mostrarContenidoSinDatos: Boolean = false,
    contenido: @Composable () -> Unit
) {
    when (estado) {
        EstadoRecurso.Inicial, EstadoRecurso.Cargando -> Box(
            Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = HoneyDark)
        }
        EstadoRecurso.Vacio -> if (mostrarContenidoSinDatos) {
            Column(Modifier.fillMaxSize()) {
                Text(mensajeVacio, Modifier.align(Alignment.CenterHorizontally).padding(16.dp), color = Color(0xFF766B5E))
                Box(Modifier.weight(1f).fillMaxWidth()) { contenido() }
            }
        } else Box(
            Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) { Text(mensajeVacio, color = Color(0xFF766B5E)) }
        is EstadoRecurso.Error -> Column(
            Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(estado.mensaje, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = reintentar) { Text("Reintentar") }
        }
        EstadoRecurso.Contenido -> contenido()
    }
}

@Composable
internal fun TarjetaProducto(product: Producto, open: () -> Unit) {
    Card(
        Modifier.width(190.dp).clickable(onClick = open),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            ImagenProducto(product, Modifier.fillMaxWidth().height(92.dp).clip(RoundedCornerShape(16.dp)))
            Spacer(Modifier.height(12.dp))
            Text(product.etiqueta, color = Leaf, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(product.nombre, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(formatoMoneda(product.precioCentimos), color = HoneyDark, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
internal fun ImagenProducto(producto: Producto, modifier: Modifier) {
    ImagenRemota(producto.imagenUrl, producto.nombre, modifier, iconoProducto(producto))
}

@Composable
internal fun ImagenRemota(url: String?, descripcion: String?, modifier: Modifier, fallback: ImageVector = Icons.Default.Image) {
    if (url.isNullOrBlank()) {
        Box(modifier.background(Color(0xFFFFF0C2)), contentAlignment = Alignment.Center) {
            Icon(fallback, contentDescription = descripcion, tint = HoneyDark, modifier = Modifier.size(52.dp))
        }
    } else {
        AsyncImage(model = url, contentDescription = descripcion, contentScale = ContentScale.Crop, modifier = modifier)
    }
}

internal fun formatoMoneda(centimos: Int): String = "S/ %.2f".format(java.util.Locale.US, centimos / 100.0)

internal fun iconoProducto(producto: Producto): ImageVector = when (producto.id) {
    1 -> Icons.Default.WaterDrop
    2 -> Icons.Default.Spa
    3 -> Icons.Default.Grain
    else -> Icons.Default.Inventory2
}
