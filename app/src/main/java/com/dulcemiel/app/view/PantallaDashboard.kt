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
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dulcemiel.app.model.ResumenDashboard
import com.dulcemiel.app.ui.theme.Hoja as Leaf
import com.dulcemiel.app.ui.theme.MielOscura as HoneyDark

@Composable
internal fun PantallaDashboard(resumen: ResumenDashboard?, abrirProductos: () -> Unit, abrirPedidos: () -> Unit, abrirPromociones: () -> Unit, abrirUsuarios: () -> Unit, abrirSitio: () -> Unit) {
    if (resumen == null) {
        Column(Modifier.fillMaxWidth().padding(32.dp)) { Text("Cargando dashboard…") }
        return
    }
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { TituloSeccion("Resumen operativo", "Información actual de Dulce Miel") }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = abrirProductos, modifier = Modifier.weight(1f)) { Text("Productos") }
                Button(onClick = abrirPedidos, modifier = Modifier.weight(1f)) { Text("Pedidos") }
            }
        }
        item { Button(onClick = abrirSitio, modifier = Modifier.fillMaxWidth()) { Text("Inicio y marca") } }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = abrirPromociones, modifier = Modifier.weight(1f)) { Text("Promociones") }
                Button(onClick = abrirUsuarios, modifier = Modifier.weight(1f)) { Text("Usuarios") }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FilaMetrica("Ventas entregadas", formatoMoneda(resumen.ventasCentimos))
                FilaMetrica("Pedidos", resumen.pedidos.toString())
                FilaMetrica("Clientes", resumen.clientes.toString())
                FilaMetrica("Stock bajo", resumen.stockBajo.toString())
                FilaMetrica("Pedidos pendientes", resumen.pedidosPendientes.toString())
            }
        }
        item { TituloSeccion("Pedidos por estado", "Distribución actual") }
        items(resumen.porEstado) { item -> FilaMetrica(item.estado.replaceFirstChar { it.uppercase() }, item.total.toString()) }
        item { TituloSeccion("Últimos pedidos", "Actividad reciente") }
        items(resumen.recientes) { pedido ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(pedido.numero, fontWeight = FontWeight.Bold)
                        Text(pedido.cliente, fontSize = 13.sp)
                        Text(pedido.estado, color = Leaf, fontSize = 13.sp)
                    }
                    Text(formatoMoneda(pedido.totalCentimos), fontWeight = FontWeight.Bold, color = HoneyDark)
                }
            }
        }
    }
}

@Composable
private fun FilaMetrica(etiqueta: String, valor: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(etiqueta)
            Text(valor, fontWeight = FontWeight.ExtraBold, color = HoneyDark)
        }
    }
}
