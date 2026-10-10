package com.dulcemiel.app.model.provider

import com.dulcemiel.app.model.Producto

/** Catálogo de demostración hasta que exista una fuente de datos validada. */
object ProveedorProductos {
    fun obtenerProductos(): List<Producto> = listOf(
        Producto(1, "Miel pura", "Miel natural de sabor floral, ideal para el consumo diario.", 2290, 12, "Producto de muestra"),
        Producto(2, "Miel con propóleo", "Combinación de miel y propóleo en frasco de 350 g.", 2850, 7, "Producto de muestra"),
        Producto(3, "Polen apícola", "Polen seleccionado para complementar la alimentación.", 1890, 5, "Producto de muestra"),
        Producto(4, "Pack familiar", "Dos frascos de miel para planificar el consumo del hogar.", 3990, 8, "Producto de muestra")
    )
}
