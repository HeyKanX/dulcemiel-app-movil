package com.dulcemiel.app.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/** Comprueba si el dispositivo posee una red validada con acceso a Internet. */
class NetworkConnectivityHelper(context: Context) {
    private val connectivityManager = context.applicationContext
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun hayConexionInternet(): Boolean {
        val red = connectivityManager.activeNetwork ?: return false
        val capacidades = connectivityManager.getNetworkCapabilities(red) ?: return false
        // NET_CAPABILITY_VALIDATED exige acceso a Internet público y puede descartar
        // una conexión útil con el backend local del PC.
        return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
