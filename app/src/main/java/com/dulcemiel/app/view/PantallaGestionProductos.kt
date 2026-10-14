package com.dulcemiel.app.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
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
import com.dulcemiel.app.model.Producto
import com.dulcemiel.app.model.ProductoEdicion
import com.dulcemiel.app.model.ArchivoAdjunto
import com.dulcemiel.app.ui.theme.Hoja as Leaf
import com.dulcemiel.app.ui.theme.MielOscura as HoneyDark

@Composable
internal fun PantallaGestionProductos(productos: List<Producto>, guardar: (ProductoEdicion) -> Unit, eliminar: (Int) -> Unit) {
    var edicion by remember { mutableStateOf<Producto?>(null) }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("disponible") }
    var imagen by remember { mutableStateOf<ArchivoAdjunto?>(null) }
    fun cargar(producto: Producto?) {
        edicion = producto
        nombre = producto?.nombre.orEmpty(); descripcion = producto?.descripcion.orEmpty()
        precio = producto?.let { "%.2f".format(java.util.Locale.US, it.precioCentimos / 100.0) }.orEmpty()
        stock = producto?.existencias?.toString().orEmpty(); categoria = producto?.etiqueta.orEmpty(); estado = producto?.estado ?: "disponible"; imagen = null
    }
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { TituloSeccion(if (edicion == null) "Nuevo producto" else "Editar producto", "Los cambios se guardan en el catálogo publicado") }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(descripcion, { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    OutlinedTextField(precio, { precio = it }, label = { Text("Precio en soles") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(stock, { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(categoria, { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    SelectorImagen("Seleccionar imagen", imagen) { imagen = it }
                    Text("Estado", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("disponible", "agotado", "discontinuado").forEach { opcion ->
                            FilterChip(selected = estado == opcion, onClick = { estado = opcion }, label = { Text(opcion) })
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { guardar(ProductoEdicion(edicion?.id, nombre, descripcion, precio, stock, categoria, estado, imagen)); if (edicion != null) cargar(null) }) { Text(if (edicion == null) "Crear producto" else "Guardar cambios") }
                        if (edicion != null) TextButton(onClick = { cargar(null) }) { Text("Cancelar") }
                    }
                }
            }
        }
        item { HorizontalDivider() }
        item { TituloSeccion("Catálogo administrativo", "${productos.size} productos registrados") }
        items(productos) { producto ->
            Card(Modifier.fillMaxWidth().clickable { cargar(producto) }, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.padding(15.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(producto.nombre, fontWeight = FontWeight.Bold)
                        Text("${formatoMoneda(producto.precioCentimos)} · ${producto.existencias} unidades", fontSize = 13.sp)
                        Text(producto.estado, color = if (producto.estado == "disponible") Leaf else HoneyDark, fontSize = 12.sp)
                    }
                    Column {
                        TextButton(onClick = { cargar(producto) }) { Text("Editar") }
                        TextButton(onClick = { eliminar(producto.id) }) { Text("Eliminar") }
                    }
                }
            }
        }
    }
}
