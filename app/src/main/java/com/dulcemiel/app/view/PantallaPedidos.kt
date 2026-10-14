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
internal fun PantallaPedidos(pedidos: List<Pedido>, abrir: (Long) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { TituloSeccion("Mis pedidos", "Consulta el estado de tus compras") }
        if (pedidos.isEmpty()) item { Text("Aún no tienes pedidos registrados.") }
        items(pedidos) { pedido ->
            Card(Modifier.fillMaxWidth().clickable { abrir(pedido.id) }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(18.dp)) {
                    Text(pedido.numero.ifBlank { "Pedido #${pedido.id}" }, fontWeight = FontWeight.Bold)
                    pedido.cantidadUnidadesApi?.let { Text("$it unidades · ${formatoMoneda(pedido.totalCentimos)}") }
                        ?: Text(formatoMoneda(pedido.totalCentimos))
                    Text(pedido.estado, color = Leaf)
                    pedido.fechaPedido?.let { Text("Registrado: $it", fontSize = 12.sp) }
                    Text("Ver detalle", color = HoneyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
