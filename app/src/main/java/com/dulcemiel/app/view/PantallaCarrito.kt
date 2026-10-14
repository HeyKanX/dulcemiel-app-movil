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
import com.dulcemiel.app.viewmodel.LineaCarrito

@Composable
internal fun PantallaCarrito(lineas: List<LineaCarrito>, totalCentimos: Int, add: (Producto) -> Unit, remove: (Producto) -> Unit, eliminar: (Producto) -> Unit, vaciar: () -> Unit, registrar: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                TituloSeccion("Compra sin sorpresas", "Revisa tus cantidades antes de confirmar")
                if (lineas.isNotEmpty()) TextButton(onClick = vaciar) { Text("Vaciar") }
            }
        }
        if (lineas.isEmpty()) {
            item {
                Column(Modifier.fillParentMaxSize().padding(top = 70.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingBasket, contentDescription = null, modifier = Modifier.size(82.dp), tint = Honey)
                    Text("Tu carrito está vacío", fontWeight = FontWeight.Bold, fontSize = 21.sp)
                    Text("Agrega solo lo que necesitas", color = Color.Gray)
                }
            }
        } else {
            items(lineas) { linea ->
                val product = linea.producto
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(iconoProducto(product), contentDescription = null, tint = HoneyDark, modifier = Modifier.size(42.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(product.nombre, fontWeight = FontWeight.Bold)
                            Text("${formatoMoneda(product.precioCentimos)} c/u", fontSize = 12.sp)
                            if (product.existencias != Int.MAX_VALUE) Text("${product.existencias} disponibles", fontSize = 12.sp, color = Leaf)
                        }
                        IconButton(onClick = { remove(product) }) { Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Quitar uno") }
                        Text("${linea.cantidad}", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { add(product) }, enabled = product.existencias == Int.MAX_VALUE || linea.cantidad < product.existencias) { Icon(Icons.Default.AddCircleOutline, contentDescription = "Agregar uno") }
                        IconButton(onClick = { eliminar(product) }) { Icon(Icons.Default.DeleteOutline, contentDescription = "Eliminar ${product.nombre}") }
                    }
                }
            }
            item {
                HorizontalDivider()
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total visible", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(formatoMoneda(totalCentimos), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = HoneyDark)
                }
                Button(onClick = registrar, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = HoneyDark)) { Text("Continuar con datos de entrega") }
            }
        }
    }
}
