package com.dulcemiel.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dulcemiel.app.model.DetallePedido
import com.dulcemiel.app.ui.theme.Hoja as Leaf
import com.dulcemiel.app.ui.theme.MielOscura as HoneyDark

@Composable
internal fun PantallaDetallePedido(detalle: DetallePedido?) {
    if (detalle == null) {
        Column(Modifier.fillMaxWidth().padding(32.dp)) { Text("Cargando detalle del pedido…") }
        return
    }
    val pedido = detalle.pedido
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text(pedido.numero.ifBlank { "Pedido #${pedido.id}" }, fontWeight = FontWeight.ExtraBold, fontSize = 25.sp)
            Text(pedido.estado.replaceFirstChar { it.uppercase() }, color = Leaf, fontWeight = FontWeight.Bold)
            pedido.fechaPedido?.let { Text("Registrado: $it", fontSize = 13.sp) }
        }
        item { TituloSeccion("Productos", "Detalle de tu compra") }
        items(detalle.lineas) { linea ->
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(linea.nombre, fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${linea.cantidad} × ${formatoMoneda(linea.precioUnitarioCentimos)}")
                        Text(formatoMoneda(linea.subtotalCentimos), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            HorizontalDivider()
            Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(formatoMoneda(pedido.totalCentimos), fontWeight = FontWeight.ExtraBold, color = HoneyDark, fontSize = 21.sp)
            }
        }
        if (pedido.direccionEntrega != null || pedido.distrito != null) {
            item {
                TituloSeccion("Entrega", "Datos registrados para este pedido")
                Card(colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        pedido.contactoNombre?.let { Text(it, fontWeight = FontWeight.Bold) }
                        pedido.contactoTelefono?.let { Text(it) }
                        pedido.direccionEntrega?.let { Text(it) }
                        pedido.distrito?.let { Text(it) }
                        pedido.referencia?.let { Text("Referencia: $it", fontSize = 13.sp) }
                        pedido.notas?.let { Text("Notas: $it", fontSize = 13.sp) }
                    }
                }
            }
        }
    }
}
