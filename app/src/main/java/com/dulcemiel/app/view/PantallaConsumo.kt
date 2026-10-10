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
internal fun PantallaConsumo() {
    val tips = listOf(
        Triple(Icons.Default.Inventory2, "Planifica tu compra", "Revisa el stock y compra una cantidad acorde con tu consumo."),
        Triple(Icons.Default.WbSunny, "Conserva correctamente", "Mantén la miel cerrada, lejos de humedad y calor excesivo."),
        Triple(Icons.Default.Recycling, "Reutiliza con información", "Limpia y reutiliza el frasco solo cuando el material y su estado lo permitan."),
        Triple(Icons.Default.NotificationsOff, "Controla tus avisos", "Decide si deseas promociones; los avisos de pedido se mantienen separados.")
    )
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Leaf), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(22.dp)) {
                    Icon(Icons.Default.Eco, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                    Text("Consumo", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Compra, conservación y reutilización", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        item { Text("Pequeñas decisiones, mejor aprovechamiento", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        items(tips) { (icon, title, text) ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE7F4E8)), contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = Leaf)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column { Text(title, fontWeight = FontWeight.Bold); Text(text, fontSize = 13.sp, lineHeight = 18.sp) }
                }
            }
        }
    }
}
