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
internal fun PantallaDetalle(product: Producto, add: (Producto) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(22.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {
            ImagenProducto(product, Modifier.fillMaxWidth().height(245.dp).clip(RoundedCornerShape(30.dp)))
        }
        item {
            Text(product.etiqueta.uppercase(), color = Leaf, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(product.nombre, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Text(formatoMoneda(product.precioCentimos), fontSize = 24.sp, color = HoneyDark, fontWeight = FontWeight.Bold)
            Text("Existencias: ${product.existencias}", color = Leaf)
        }
        item { Text(product.descripcion, lineHeight = 23.sp) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F4E8))) {
                Row(Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Eco, contentDescription = null, tint = Leaf)
                    Spacer(Modifier.width(12.dp))
                    Text("Conserva el frasco cerrado, en un lugar fresco y seco. Compra la cantidad que consumirás.", fontSize = 13.sp)
                }
            }
        }
        item {
            Button(onClick = { add(product) }, enabled = product.existencias > 0, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = HoneyDark)) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (product.existencias > 0) "Agregar al carrito" else "Producto agotado")
            }
        }
    }
}
