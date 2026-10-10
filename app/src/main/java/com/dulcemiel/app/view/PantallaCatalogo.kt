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
internal fun PantallaCatalogo(products: List<Producto>, open: (Producto) -> Unit, add: (Producto) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { TituloSeccion("Elige de manera informada", "Precios y existencias de demostración") }
        items(products) { product ->
            Card(Modifier.fillMaxWidth().clickable { open(product) }, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(72.dp).clip(RoundedCornerShape(15.dp)).background(Color(0xFFFFF0C2)), contentAlignment = Alignment.Center) {
                        Icon(iconoProducto(product), contentDescription = null, tint = HoneyDark, modifier = Modifier.size(38.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(product.nombre, fontWeight = FontWeight.Bold)
                        Text("${product.existencias} disponibles", color = Leaf, fontSize = 12.sp)
                        Text(formatoMoneda(product.precioCentimos), fontWeight = FontWeight.ExtraBold, color = HoneyDark)
                    }
                    FilledIconButton(onClick = { add(product) }, colors = IconButtonDefaults.filledIconButtonColors(containerColor = Honey)) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = "Agregar ${product.nombre}", tint = HoneyDark)
                    }
                }
            }
        }
    }
}
