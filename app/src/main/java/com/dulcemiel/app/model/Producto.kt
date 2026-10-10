package com.dulcemiel.app.model

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precioCentimos: Int,
    val existencias: Int,
    val etiqueta: String
)
