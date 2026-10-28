package com.dulcemiel.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dulcemiel.app.repository.RepositorioBuzonFirebase
import com.dulcemiel.app.repository.mensajeErrorFirebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class EstadoBuzon(
    val enviando: Boolean = false,
    val enviado: Boolean = false,
    val mensaje: String? = null
)

class ModeloVistaBuzon(private val repositorio: RepositorioBuzonFirebase) : ViewModel() {
    var estado by mutableStateOf(EstadoBuzon())
        private set

    fun enviar(mensaje: String, categoria: String) {
        val texto = mensaje.trim()
        if (texto.length !in 10..1000) {
            estado = EstadoBuzon(mensaje = "Escribe una sugerencia de entre 10 y 1000 caracteres.")
            return
        }
        if (categoria !in CATEGORIAS) {
            estado = EstadoBuzon(mensaje = "Selecciona una categoría.")
            return
        }

        viewModelScope.launch {
            estado = EstadoBuzon(enviando = true)
            try {
                repositorio.enviar(texto, categoria)
                estado = EstadoBuzon(enviado = true, mensaje = "Gracias. Tu sugerencia se envió correctamente.")
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                estado = EstadoBuzon(mensaje = mensajeErrorFirebase(error))
            }
        }
    }

    fun limpiarMensaje() {
        estado = estado.copy(enviado = false, mensaje = null)
    }

    private companion object {
        val CATEGORIAS = setOf("producto", "servicio", "app", "otro")
    }
}

class FabricaModeloVistaBuzon(private val repositorio: RepositorioBuzonFirebase) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ModeloVistaBuzon(repositorio) as T
}
