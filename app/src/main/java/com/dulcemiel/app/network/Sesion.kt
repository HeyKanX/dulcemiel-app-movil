package com.dulcemiel.app.network

import android.content.Context

class Sesion(context: Context) {
    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
    var token: String? get() = prefs.getString("token", null); set(value) { prefs.edit().putString("token", value).apply() }
    var nombre: String? get() = prefs.getString("nombre", null); private set(value) { prefs.edit().putString("nombre", value).apply() }
    var email: String? get() = prefs.getString("email", null); private set(value) { prefs.edit().putString("email", value).apply() }
    var rol: String? get() = prefs.getString("rol", null); private set(value) { prefs.edit().putString("rol", value).apply() }

    fun iniciar(token: String, nombre: String, email: String, rol: String) {
        prefs.edit().putString("token", token).putString("nombre", nombre).putString("email", email).putString("rol", rol).apply()
    }

    fun actualizarPerfil(nombre: String, email: String, rol: String) { prefs.edit().putString("nombre", nombre).putString("email", email).putString("rol", rol).apply() }

    fun cerrar() { prefs.edit().clear().apply() }
}
