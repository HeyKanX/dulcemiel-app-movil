package com.dulcemiel.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dulcemiel.app.model.Pedido
import com.dulcemiel.app.model.Producto
import com.dulcemiel.app.repository.RepositorioTienda

class ModeloVistaTienda(private val repositorio: RepositorioTienda) : ViewModel() {
    val productos = repositorio.productos
    private val carritoInicial = repositorio.cargarCarrito()
    var estado by mutableStateOf(EstadoAplicacion(
        carrito = carritoInicial,
        lineasCarrito = crearLineas(carritoInicial),
        totalCarritoCentimos = calcularTotal(carritoInicial),
        pedidos = repositorio.cargarPedidos()
    ))
        private set

    private fun crearLineas(carrito: Map<Int, Int>): List<LineaCarrito> = productos.mapNotNull { producto ->
        carrito[producto.id]?.let { cantidad -> LineaCarrito(producto, cantidad) }
    }

    private fun calcularTotal(carrito: Map<Int, Int>): Int = productos.sumOf { producto ->
        producto.precioCentimos * (carrito[producto.id] ?: 0)
    }

    fun irA(pantalla: Pantalla) { estado = estado.copy(pantalla = pantalla, productoSeleccionado = null, mensaje = null) }
    fun abrir(producto: Producto) { estado = estado.copy(pantalla = Pantalla.DETALLE, productoSeleccionado = producto) }
    fun agregar(producto: Producto) {
        val actual = estado.carrito[producto.id] ?: 0
        if (actual >= producto.existencias) return
        cambiarCarrito(estado.carrito + (producto.id to actual + 1))
    }
    fun quitar(producto: Producto) {
        val actual = estado.carrito[producto.id] ?: return
        cambiarCarrito(if (actual <= 1) estado.carrito - producto.id else estado.carrito + (producto.id to actual - 1))
    }
    private fun cambiarCarrito(carrito: Map<Int, Int>) {
        repositorio.guardarCarrito(carrito)
        estado = estado.copy(
            carrito = carrito,
            lineasCarrito = crearLineas(carrito),
            totalCarritoCentimos = calcularTotal(carrito)
        )
    }
    fun registrarPedido() {
        if (estado.carrito.isEmpty()) return
        val pedido = Pedido(System.currentTimeMillis(), estado.carrito.toMap(), estado.totalCarritoCentimos)
        val pedidos = listOf(pedido) + estado.pedidos
        repositorio.guardarPedidos(pedidos)
        repositorio.guardarCarrito(emptyMap())
        estado = estado.copy(pedidos = pedidos, carrito = emptyMap(), lineasCarrito = emptyList(),
            totalCarritoCentimos = 0, pantalla = Pantalla.PEDIDOS,
            mensaje = "Pedido guardado en este dispositivo. No se ha enviado a Dulce Miel.")
    }
    fun cantidadCarrito(): Int = estado.carrito.values.sum()
}

class FabricaModeloVistaTienda(private val repositorio: RepositorioTienda) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ModeloVistaTienda(repositorio) as T
}

