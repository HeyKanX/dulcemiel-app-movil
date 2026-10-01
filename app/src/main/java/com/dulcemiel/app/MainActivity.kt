package com.dulcemiel.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

private val Honey = Color(0xFFF5A800)
private val HoneyDark = Color(0xFF6B4600)
private val Cream = Color(0xFFFFF8E8)
private val Leaf = Color(0xFF2E7D32)
private val Ink = Color(0xFF2A2116)

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val badge: String,
    val icon: ImageVector
)

enum class Screen { HOME, CATALOG, CART, ORDERS, RESPONSIBLE, DETAIL }

data class AppState(
    val screen: Screen = Screen.HOME,
    val selected: Product? = null,
    val cart: Map<Int, Int> = emptyMap()
)

class DulceMielViewModel : ViewModel() {
    val products = listOf(
        Product(1, "Miel pura", "Miel natural de sabor floral, ideal para el consumo diario.", 22.90, 12, "Más vendido", Icons.Default.WaterDrop),
        Product(2, "Miel con propóleo", "Combinación práctica de miel y propóleo en frasco de 350 g.", 28.50, 7, "Stock real", Icons.Default.Spa),
        Product(3, "Polen apícola", "Polen seleccionado para complementar tu alimentación.", 18.90, 5, "Últimas 5", Icons.Default.Grain),
        Product(4, "Pack familiar", "Dos frascos de miel para planificar el consumo del hogar.", 39.90, 8, "Ahorro responsable", Icons.Default.Inventory2)
    )

    var state by mutableStateOf(AppState())
        private set

    fun go(screen: Screen) { state = state.copy(screen = screen, selected = null) }
    fun open(product: Product) { state = state.copy(screen = Screen.DETAIL, selected = product) }
    fun add(product: Product) {
        val current = state.cart[product.id] ?: 0
        if (current < product.stock) state = state.copy(cart = state.cart + (product.id to current + 1))
    }
    fun remove(product: Product) {
        val current = state.cart[product.id] ?: return
        state = if (current <= 1) state.copy(cart = state.cart - product.id)
        else state.copy(cart = state.cart + (product.id to current - 1))
    }
    fun cartCount() = state.cart.values.sum()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = HoneyDark,
                    secondary = Leaf,
                    background = Cream,
                    surface = Color.White,
                    onBackground = Ink,
                    onSurface = Ink
                )
            ) {
                DulceMielApp()
            }
        }
    }
}

@Composable
fun DulceMielApp(vm: DulceMielViewModel = viewModel()) {
    val state = vm.state
    Scaffold(
        containerColor = Cream,
        topBar = { AppTopBar(state.screen) { vm.go(Screen.HOME) } },
        bottomBar = {
            if (state.screen != Screen.DETAIL) BottomNavigation(state.screen, vm.cartCount(), vm::go)
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (state.screen) {
                Screen.HOME -> HomeScreen(vm.products, vm::open, vm::go)
                Screen.CATALOG -> CatalogScreen(vm.products, vm::open, vm::add)
                Screen.CART -> CartScreen(vm.products, state.cart, vm::add, vm::remove)
                Screen.ORDERS -> OrdersScreen()
                Screen.RESPONSIBLE -> ResponsibleScreen()
                Screen.DETAIL -> state.selected?.let { DetailScreen(it, vm::add) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(screen: Screen, home: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(38.dp).clip(CircleShape).background(Honey), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Hive, contentDescription = null, tint = HoneyDark)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Dulce Miel", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(screen.title(), fontSize = 12.sp, color = Color(0xFF6E6254))
                }
            }
        },
        navigationIcon = {
            if (screen == Screen.DETAIL) IconButton(onClick = home) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al inicio")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
    )
}

private fun Screen.title() = when (this) {
    Screen.HOME -> "Compra local y responsable"
    Screen.CATALOG -> "Catálogo"
    Screen.CART -> "Tu carrito"
    Screen.ORDERS -> "Seguimiento"
    Screen.RESPONSIBLE -> "ODS 12"
    Screen.DETAIL -> "Detalle del producto"
}

@Composable
private fun HomeScreen(products: List<Product>, open: (Product) -> Unit, go: (Screen) -> Unit) {
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
                        Text("Miel que conecta", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = HoneyDark)
                        Spacer(Modifier.height(8.dp))
                        Text("Conoce el producto, compra lo necesario y sigue tu pedido.", color = HoneyDark, lineHeight = 21.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { go(Screen.CATALOG) }, colors = ButtonDefaults.buttonColors(containerColor = HoneyDark)) {
                            Text("Ver catálogo")
                        }
                    }
                    Icon(Icons.Default.Hive, contentDescription = null, tint = Color.White.copy(alpha = .72f), modifier = Modifier.size(92.dp).align(Alignment.CenterEnd))
                }
            }
        }
        item { SectionTitle("Productos destacados", "Disponibilidad actualizada") }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(products.take(3)) { ProductCard(it, { open(it) }) }
            }
        }
        item {
            Card(
                Modifier.fillMaxWidth().clickable { go(Screen.RESPONSIBLE) },
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
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 21.sp)
        Text(subtitle, fontSize = 13.sp, color = Color(0xFF766B5E))
    }
}

@Composable
private fun ProductCard(product: Product, open: () -> Unit) {
    Card(
        Modifier.width(190.dp).clickable(onClick = open),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(Modifier.fillMaxWidth().height(92.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFFFF0C2)), contentAlignment = Alignment.Center) {
                Icon(product.icon, contentDescription = null, tint = HoneyDark, modifier = Modifier.size(52.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(product.badge, color = Leaf, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("S/ ${"%.2f".format(product.price)}", color = HoneyDark, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun CatalogScreen(products: List<Product>, open: (Product) -> Unit, add: (Product) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Elige de manera informada", "Precios y stock visibles antes de comprar") }
        items(products) { product ->
            Card(Modifier.fillMaxWidth().clickable { open(product) }, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(72.dp).clip(RoundedCornerShape(15.dp)).background(Color(0xFFFFF0C2)), contentAlignment = Alignment.Center) {
                        Icon(product.icon, contentDescription = null, tint = HoneyDark, modifier = Modifier.size(38.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(product.name, fontWeight = FontWeight.Bold)
                        Text("${product.stock} disponibles", color = Leaf, fontSize = 12.sp)
                        Text("S/ ${"%.2f".format(product.price)}", fontWeight = FontWeight.ExtraBold, color = HoneyDark)
                    }
                    FilledIconButton(onClick = { add(product) }, colors = IconButtonDefaults.filledIconButtonColors(containerColor = Honey)) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = "Agregar ${product.name}", tint = HoneyDark)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailScreen(product: Product, add: (Product) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(22.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {
            Box(Modifier.fillMaxWidth().height(245.dp).clip(RoundedCornerShape(30.dp)).background(Color(0xFFFFE7A3)), contentAlignment = Alignment.Center) {
                Icon(product.icon, contentDescription = product.name, tint = HoneyDark, modifier = Modifier.size(120.dp))
            }
        }
        item {
            Text(product.badge.uppercase(), color = Leaf, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(product.name, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Text("S/ ${"%.2f".format(product.price)}", fontSize = 24.sp, color = HoneyDark, fontWeight = FontWeight.Bold)
            Text("Stock disponible: ${product.stock}", color = Leaf)
        }
        item { Text(product.description, lineHeight = 23.sp) }
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
            Button(onClick = { add(product) }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = HoneyDark)) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Agregar al carrito")
            }
        }
    }
}

@Composable
private fun CartScreen(products: List<Product>, cart: Map<Int, Int>, add: (Product) -> Unit, remove: (Product) -> Unit) {
    val selected = products.filter { cart.containsKey(it.id) }
    val total = selected.sumOf { it.price * (cart[it.id] ?: 0) }
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Compra sin sorpresas", "Ningún producto se añade automáticamente") }
        if (selected.isEmpty()) {
            item {
                Column(Modifier.fillParentMaxSize().padding(top = 70.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingBasket, contentDescription = null, modifier = Modifier.size(82.dp), tint = Honey)
                    Text("Tu carrito está vacío", fontWeight = FontWeight.Bold, fontSize = 21.sp)
                    Text("Agrega solo lo que necesitas", color = Color.Gray)
                }
            }
        } else {
            items(selected) { product ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(product.icon, contentDescription = null, tint = HoneyDark, modifier = Modifier.size(42.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold)
                            Text("S/ ${"%.2f".format(product.price)} c/u", fontSize = 12.sp)
                        }
                        IconButton(onClick = { remove(product) }) { Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Quitar uno") }
                        Text("${cart[product.id]}", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { add(product) }) { Icon(Icons.Default.AddCircleOutline, contentDescription = "Agregar uno") }
                    }
                }
            }
            item {
                HorizontalDivider()
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total visible", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("S/ ${"%.2f".format(total)}", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = HoneyDark)
                }
                Button(onClick = {}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = HoneyDark)) { Text("Continuar pedido") }
            }
        }
    }
}

@Composable
private fun OrdersScreen() {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { SectionTitle("Tus pedidos", "Información clara en cada etapa") }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pedido #DM-024", fontWeight = FontWeight.Bold)
                        AssistChip(onClick = {}, label = { Text("En preparación") }, leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) })
                    }
                    Text("2 productos · S/ 51.40", color = Color.Gray)
                    Spacer(Modifier.height(18.dp))
                    listOf("Pedido confirmado" to true, "En preparación" to true, "En camino" to false, "Entregado" to false).forEach { (label, active) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (active) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked, contentDescription = null, tint = if (active) Leaf else Color.LightGray)
                            Spacer(Modifier.width(10.dp)); Text(label, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal)
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
        item { Text("Las notificaciones del pedido son independientes de las promociones.", fontSize = 13.sp, color = Color(0xFF655B50)) }
    }
}

@Composable
private fun ResponsibleScreen() {
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
                    Text("ODS 12", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Producción y consumo responsables", color = Color.White, fontWeight = FontWeight.SemiBold)
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

@Composable
private fun BottomNavigation(current: Screen, count: Int, go: (Screen) -> Unit) {
    val items = listOf(
        Triple(Screen.HOME, "Inicio", Icons.Default.Home),
        Triple(Screen.CATALOG, "Catálogo", Icons.Default.Storefront),
        Triple(Screen.CART, "Carrito", Icons.Default.ShoppingCart),
        Triple(Screen.ORDERS, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
        Triple(Screen.RESPONSIBLE, "ODS 12", Icons.Default.Eco)
    )
    NavigationBar(containerColor = Color.White) {
        items.forEach { (screen, label, icon) ->
            NavigationBarItem(
                selected = current == screen,
                onClick = { go(screen) },
                icon = {
                    if (screen == Screen.CART && count > 0) BadgedBox(badge = { Badge { Text("$count") } }) { Icon(icon, contentDescription = label) }
                    else Icon(icon, contentDescription = label)
                },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = HoneyDark, indicatorColor = Color(0xFFFFE39A))
            )
        }
    }
}
