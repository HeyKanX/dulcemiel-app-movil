package com.dulcemiel.app.model

import java.util.Locale

/** Punto que el cliente seleccionó explícitamente para orientar la entrega. */
data class UbicacionEntrega(val latitud: Double, val longitud: Double) {
    fun enlaceMapa(): String = String.format(
        Locale.US,
        "https://maps.google.com/?q=%.6f,%.6f",
        latitud,
        longitud
    )
}
