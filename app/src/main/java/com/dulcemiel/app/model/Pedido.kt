package com.dulcemiel.app.model

data class Pedido(
    val id: Long,
    val productos: Map<Int, Int>,
    val totalCentimos: Int,
    val estado: String = "Registrado en este dispositivo"
) {
    val cantidadUnidades: Int get() = productos.values.sum()
}
