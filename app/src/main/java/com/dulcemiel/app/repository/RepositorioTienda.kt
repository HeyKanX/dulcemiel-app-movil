package com.dulcemiel.app.repository

import com.dulcemiel.app.model.Pedido
import com.dulcemiel.app.model.Producto
import com.dulcemiel.app.model.provider.FuenteDatosLocal
import com.dulcemiel.app.model.provider.ProveedorProductos

class RepositorioTienda(private val fuente: FuenteDatosLocal) {
    val productos: List<Producto> = ProveedorProductos.obtenerProductos()

    fun cargarCarrito(): Map<Int, Int> = fuente.leerCarrito().mapNotNull { (id, cantidad) ->
        productos.find { it.id == id }?.let { id to cantidad.coerceAtMost(it.existencias) }
    }.toMap()

    fun guardarCarrito(carrito: Map<Int, Int>) = fuente.guardarCarrito(carrito)
    fun cargarPedidos(): List<Pedido> = fuente.leerPedidos()
    fun guardarPedidos(pedidos: List<Pedido>) = fuente.guardarPedidos(pedidos)
}
