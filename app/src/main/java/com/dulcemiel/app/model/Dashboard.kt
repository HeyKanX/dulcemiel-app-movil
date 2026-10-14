package com.dulcemiel.app.model

data class ResumenDashboard(
    val ventasCentimos: Int = 0,
    val pedidos: Int = 0,
    val clientes: Int = 0,
    val stockBajo: Int = 0,
    val pedidosPendientes: Int = 0,
    val porEstado: List<EstadoPedido> = emptyList(),
    val recientes: List<PedidoReciente> = emptyList()
)
data class EstadoPedido(val estado: String, val total: Int)
data class PedidoReciente(val id: Long, val numero: String, val totalCentimos: Int, val estado: String, val cliente: String)
