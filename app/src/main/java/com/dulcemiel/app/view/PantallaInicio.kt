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
import com.dulcemiel.app.model.Promocion
import com.dulcemiel.app.model.SlideInicio
import com.dulcemiel.app.ui.theme.Miel as Honey
import com.dulcemiel.app.ui.theme.MielOscura as HoneyDark
import com.dulcemiel.app.ui.theme.Crema as Cream
import com.dulcemiel.app.ui.theme.Hoja as Leaf
import com.dulcemiel.app.ui.theme.Tinta as Ink
import com.dulcemiel.app.viewmodel.ModeloVistaTienda
import com.dulcemiel.app.viewmodel.Pantalla

@Composable
internal fun PantallaInicio(
    products: List<Producto>, empresa: String, descripcion: String, logoUrl: String?,
    carrusel: List<SlideInicio>, promociones: List<Promocion>, mision: String, vision: String,
    open: (Producto) -> Unit, go: (Pantalla) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(
                    Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Honey, Color(0xFFFFD66B)))).padding(22.dp)
                ) {
                    Column(Modifier.fillMaxWidth(.72f)) {
                        Text(empresa, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = HoneyDark)
                        Spacer(Modifier.height(8.dp))
                        Text(descripcion, color = HoneyDark, lineHeight = 21.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { go(Pantalla.CATALOGO) }, colors = ButtonDefaults.buttonColors(containerColor = HoneyDark)) {
                            Text("Ver catálogo")
                        }
                    }
                    ImagenRemota(logoUrl, "Logo de $empresa", Modifier.size(92.dp).clip(CircleShape).align(Alignment.CenterEnd), Icons.Default.Hive)
                }
            }
        }
        if (carrusel.isNotEmpty()) {
            item { TituloSeccion("Novedades", "Información de Dulce Miel") }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(carrusel) { slide ->
                        Card(Modifier.width(260.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column {
                                ImagenRemota(slide.imagenUrl, slide.titulo, Modifier.fillMaxWidth().height(125.dp).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)), Icons.Default.Image)
                                Column(Modifier.padding(14.dp)) {
                                    Text(slide.titulo, fontWeight = FontWeight.Bold)
                                    slide.descripcion?.takeIf { it.isNotBlank() }?.let { Text(it, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (promociones.isNotEmpty()) {
            item {
                val promocion = promociones.first()
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F4E8)), shape = RoundedCornerShape(20.dp)) {
                    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalOffer, contentDescription = null, tint = Leaf, modifier = Modifier.size(34.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("${promocion.descuento}% OFF · ${promocion.titulo}", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                            promocion.descripcion?.let { Text(it, fontSize = 13.sp) }
                            textoVigencia(promocion)?.let { Text(it, fontSize = 12.sp, color = Color(0xFF356B39)) }
                        }
                    }
                }
            }
        }
        item { TituloSeccion("Productos destacados", "Conoce nuestra selección") }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(products.take(3)) { TarjetaProducto(it, { open(it) }) }
            }
        }
        item {
            Card(
                Modifier.fillMaxWidth().clickable { go(Pantalla.CONSUMO) },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F4E8)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Eco, contentDescription = null, tint = Leaf, modifier = Modifier.size(42.dp))
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Consume con propósito", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        Text("Consejos para conservar la miel y reducir desperdicios.", fontSize = 13.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Abrir consejos")
                }
            }
        }
        if (mision.isNotBlank() || vision.isNotBlank()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (mision.isNotBlank()) { Text("Nuestra misión", fontWeight = FontWeight.Bold, color = HoneyDark); Text(mision, fontSize = 13.sp) }
                        if (vision.isNotBlank()) { Text("Nuestra visión", fontWeight = FontWeight.Bold, color = HoneyDark); Text(vision, fontSize = 13.sp) }
                    }
                }
            }
        }
    }
}

private fun textoVigencia(promocion: Promocion): String? = when {
    promocion.fechaInicio.isNotBlank() && !promocion.fechaFin.isNullOrBlank() -> "Válida del ${promocion.fechaInicio} al ${promocion.fechaFin}"
    promocion.fechaInicio.isNotBlank() -> "Válida desde ${promocion.fechaInicio}"
    !promocion.fechaFin.isNullOrBlank() -> "Válida hasta ${promocion.fechaFin}"
    else -> null
}
