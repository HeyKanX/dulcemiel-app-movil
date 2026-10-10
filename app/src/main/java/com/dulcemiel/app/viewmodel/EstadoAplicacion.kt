package com.dulcemiel.app.viewmodel

import com.dulcemiel.app.model.Pedido
import com.dulcemiel.app.model.Producto

data class EstadoAplicacion(
    val pantalla: Pantalla = Pantalla.INICIO,
    val productoSeleccionado: Producto? = null,
    val carrito: Map<Int, Int> = emptyMap(),
    val lineasCarrito: List<LineaCarrito> = emptyList(),
    val totalCarritoCentimos: Int = 0,
    val pedidos: List<Pedido> = emptyList(),
    val mensaje: String? = null
)
