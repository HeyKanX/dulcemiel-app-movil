package com.dulcemiel.app.model

data class PedidoAdministrativo(
    val id: Long,
    val numero: String,
    val totalCentimos: Int,
    val estado: String,
    val fechaPedido: String? = null,
    val cliente: String = "",
    val email: String = ""
)
