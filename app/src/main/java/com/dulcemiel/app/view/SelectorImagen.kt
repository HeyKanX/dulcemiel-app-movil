package com.dulcemiel.app.view

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.dulcemiel.app.model.ArchivoAdjunto

@Composable
internal fun SelectorImagen(etiqueta: String, archivo: ArchivoAdjunto?, alCambiar: (ArchivoAdjunto?) -> Unit) {
    val contexto = LocalContext.current
    val selector = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { alCambiar(leerArchivo(contexto, it.toString(), it.lastPathSegment ?: "imagen")) }
    }
    Button(onClick = { selector.launch("image/*") }) { Text(if (archivo == null) etiqueta else "Cambiar imagen") }
    archivo?.let { Text(it.nombre) }
}

private fun leerArchivo(contexto: Context, uriTexto: String, nombre: String): ArchivoAdjunto? = try {
    val uri = android.net.Uri.parse(uriTexto)
    val contenido = contexto.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
    ArchivoAdjunto(nombre, contexto.contentResolver.getType(uri) ?: "image/*", contenido)
} catch (_: Exception) { null }
