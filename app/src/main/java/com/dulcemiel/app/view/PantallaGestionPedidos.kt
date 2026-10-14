package com.dulcemiel.app.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dulcemiel.app.model.PedidoAdministrativo
import com.dulcemiel.app.ui.theme.Hoja as Leaf
import com.dulcemiel.app.ui.theme.MielOscura as HoneyDark

@Composable
internal fun PantallaGestionPedidos(pedidos: List<PedidoAdministrativo>, abrirDetalle: (Long) -> Unit, actualizarEstado: (Long, String) -> Unit) {
    val estados = listOf("pendiente", "procesando", "enviado", "entregado", "cancelado")
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { TituloSeccion("Pedidos", "Administra el estado y consulta el detalle") }
        if (pedidos.isEmpty()) item { Text("No hay pedidos para mostrar.") }
        items(pedidos) { pedido ->
            var menuAbierto by remember(pedido.id) { mutableStateOf(false) }
            Card(Modifier.fillMaxWidth().clickable { abrirDetalle(pedido.id) }, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(pedido.numero, fontWeight = FontWeight.Bold)
                            Text(pedido.cliente.ifBlank { pedido.email }, fontSize = 13.sp)
                        }
                        Text(formatoMoneda(pedido.totalCentimos), fontWeight = FontWeight.Bold, color = HoneyDark)
                    }
                    pedido.fechaPedido?.let { Text(it, fontSize = 12.sp) }
                    Box {
                        TextButton(onClick = { menuAbierto = true }) { Text("Estado: ${pedido.estado}", color = Leaf) }
                        DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                            estados.forEach { estado -> DropdownMenuItem(text = { Text(estado) }, onClick = { menuAbierto = false; if (estado != pedido.estado) actualizarEstado(pedido.id, estado) }) }
                        }
                    }
                    Text("Toca la tarjeta para ver el detalle", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
