package com.dulcemiel.app.model

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precioCentimos: Int,
    val existencias: Int,
    val etiqueta: String,
    val carritoId: Int? = null,
    val imagenUrl: String? = null,
    val estado: String = "disponible"
)

data class ProductoEdicion(
    val id: Int? = null,
    val nombre: String,
    val descripcion: String,
    val precio: String,
    val stock: String,
    val categoria: String,
    val estado: String = "disponible",
    val imagen: ArchivoAdjunto? = null
)

data class ArchivoAdjunto(val nombre: String, val mime: String, val contenido: ByteArray)
