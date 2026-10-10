package com.dulcemiel.app.model.provider

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import com.dulcemiel.app.model.Pedido

/** Guarda únicamente el carrito y pedidos de demostración en almacenamiento privado. */
class FuenteDatosLocal(contexto: Context) {
    private val preferencias = contexto.getSharedPreferences("dulce_miel_local", Context.MODE_PRIVATE)

    fun leerCarrito(): Map<Int, Int> = try {
        val objeto = JSONObject(preferencias.getString("carrito", "{}") ?: "{}")
        objeto.keys().asSequence().mapNotNull { clave ->
            val id = clave.toIntOrNull()
            val cantidad = objeto.optInt(clave)
            if (id != null && cantidad > 0) id to cantidad else null
        }.toMap()
    } catch (_: Exception) { emptyMap() }

    fun guardarCarrito(carrito: Map<Int, Int>) {
        val objeto = JSONObject()
        carrito.forEach { (id, cantidad) -> objeto.put(id.toString(), cantidad) }
        preferencias.edit().putString("carrito", objeto.toString()).apply()
    }

    fun leerPedidos(): List<Pedido> = try {
        val arreglo = JSONArray(preferencias.getString("pedidos", "[]") ?: "[]")
        (0 until arreglo.length()).map { indice ->
            val objeto = arreglo.getJSONObject(indice)
            val lineas = objeto.getJSONObject("productos")
            Pedido(
                objeto.getLong("id"),
                lineas.keys().asSequence().associate { it.toInt() to lineas.getInt(it) },
                objeto.getInt("totalCentimos"),
                objeto.getString("estado")
            )
        }
    } catch (_: Exception) { emptyList() }

    fun guardarPedidos(pedidos: List<Pedido>) {
        val arreglo = JSONArray()
        pedidos.forEach { pedido ->
            val lineas = JSONObject()
            pedido.productos.forEach { (id, cantidad) -> lineas.put(id.toString(), cantidad) }
            arreglo.put(JSONObject().put("id", pedido.id).put("productos", lineas)
                .put("totalCentimos", pedido.totalCentimos).put("estado", pedido.estado))
        }
        preferencias.edit().putString("pedidos", arreglo.toString()).apply()
    }
}
