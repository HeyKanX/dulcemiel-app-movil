package com.dulcemiel.app.repository

import com.google.firebase.FirebaseException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Escribe sugerencias en Firestore sin duplicarlas en la API REST. */
class RepositorioBuzonFirebase(
    private val autenticacion: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun enviar(mensaje: String, categoria: String) {
        val texto = mensaje.trim()
        require(texto.length in 10..1000) { "La sugerencia debe tener entre 10 y 1000 caracteres." }
        require(categoria in CATEGORIAS) { "Selecciona una categoría válida." }

        val uid = autenticacion.currentUser?.uid
            ?: autenticacion.signInAnonymously().awaitResult().user?.uid
            ?: throw IllegalStateException("Firebase no creó una sesión anónima.")

        val documento = mapOf(
            "mensaje" to texto,
            "categoria" to categoria,
            "fechaCreacion" to FieldValue.serverTimestamp(),
            "uid" to uid
        )
        firestore.collection(COLECCION).add(documento).awaitResult()
    }

    private companion object {
        const val COLECCION = "buzon_sugerencias"
        val CATEGORIAS = setOf("producto", "servicio", "app", "otro")
    }
}

private suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (!continuation.isActive) return@addOnCompleteListener
        if (task.isSuccessful) {
            continuation.resume(task.result)
        } else {
            continuation.resumeWithException(task.exception ?: IllegalStateException("La operación de Firebase falló."))
        }
    }
}

internal fun mensajeErrorFirebase(error: Throwable): String = when (error) {
    is FirebaseFirestoreException -> when (error.code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Firebase rechazó la sugerencia. Verifica que la autenticación anónima y las reglas estén habilitadas."
        FirebaseFirestoreException.Code.UNAVAILABLE -> "Firestore no está disponible. Revisa tu conexión e inténtalo nuevamente."
        FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> "Se alcanzó el límite gratuito de Firebase. Inténtalo más tarde."
        else -> "No se pudo guardar la sugerencia en Firebase. Inténtalo nuevamente."
    }
    is FirebaseAuthException -> when {
        error.errorCode.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ||
            error.message.orEmpty().contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ->
            "Firebase Authentication no tiene configurado el acceso anónimo. Habilítalo en Authentication > Método de acceso > Anónimo."
        error.errorCode.contains("OPERATION_NOT_ALLOWED", ignoreCase = true) ||
            error.message.orEmpty().contains("OPERATION_NOT_ALLOWED", ignoreCase = true) ->
            "El acceso anónimo está deshabilitado en Firebase. Habilítalo en Authentication > Método de acceso > Anónimo."
        else -> "No se pudo iniciar la sesión anónima de Firebase. Revisa que el proveedor Anónimo esté habilitado."
    }
    is FirebaseException -> if (error.message.orEmpty().contains("CONFIGURATION_NOT_FOUND", ignoreCase = true)) {
        "Firebase Authentication no tiene configurado el acceso anónimo. Habilítalo en Authentication > Método de acceso > Anónimo."
    } else {
        "No se pudo completar la operación con Firebase. Inténtalo nuevamente."
    }
    else -> error.message?.takeIf { it.isNotBlank() } ?: "No se pudo enviar la sugerencia. Inténtalo nuevamente."
}
