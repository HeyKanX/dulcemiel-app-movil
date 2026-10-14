package com.dulcemiel.app.model

data class InicioTienda(
    val nombreEmpresa: String,
    val descripcion: String,
    val mision: String,
    val vision: String,
    val logoUrl: String? = null,
    val carrusel: List<SlideInicio> = emptyList(),
    val promociones: List<Promocion> = emptyList()
)

data class SlideInicio(val id: Int, val titulo: String, val descripcion: String? = null, val imagenUrl: String? = null, val orden: Int = 0, val activo: Boolean = true)
data class Promocion(val id: Int, val titulo: String, val descripcion: String? = null, val descuento: Double, val fechaInicio: String = "", val fechaFin: String? = null, val activa: Boolean = true)

data class UsuarioAdministrativo(val id: Int, val nombre: String, val email: String, val rol: String, val estado: String)

data class ConfiguracionSitioEdicion(val nombre: String, val descripcion: String, val mision: String, val vision: String, val logo: ArchivoAdjunto? = null)
data class CarruselEdicion(val id: Int? = null, val titulo: String, val descripcion: String, val orden: String, val activo: Boolean, val imagen: ArchivoAdjunto? = null)
