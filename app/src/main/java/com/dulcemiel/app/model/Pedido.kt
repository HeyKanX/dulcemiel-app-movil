package com.dulcemiel.app.model

data class Pedido(
    val id: Long,
    val productos: Map<Int, Int> = emptyMap(),
    val totalCentimos: Int,
    val estado: String = "pendiente",
    val numero: String = "",
    val fechaPedido: String? = null,
    val cantidadUnidadesApi: Int? = null,
    val contactoNombre: String? = null,
    val contactoTelefono: String? = null,
    val direccionEntrega: String? = null,
    val distrito: String? = null,
    val referencia: String? = null,
    val notas: String? = null
) {
    val cantidadUnidades: Int get() = cantidadUnidadesApi ?: productos.values.sum()
}

data class DetallePedido(
    val pedido: Pedido,
    val lineas: List<LineaPedido>
)

data class LineaPedido(
    val id: Long,
    val productoId: Int,
    val nombre: String,
    val cantidad: Int,
    val precioUnitarioCentimos: Int,
    val subtotalCentimos: Int
)
