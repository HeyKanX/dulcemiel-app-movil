package com.dulcemiel.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.dulcemiel.app.ui.theme.Hoja
import com.dulcemiel.app.ui.theme.MielOscura
import com.dulcemiel.app.viewmodel.ModeloVistaBuzon

private val categoriasSugerencia = listOf(
    "producto" to "Productos",
    "servicio" to "Atención y servicio",
    "app" to "Aplicación móvil",
    "otro" to "Otro"
)

@Composable
internal fun PantallaBuzonSugerencias(vm: ModeloVistaBuzon) {
    var mensaje by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("otro") }
    var menuAbierto by remember { mutableStateOf(false) }
    val estado = vm.estado

    androidx.compose.foundation.layout.Box {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Tu opinión nos ayuda", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MielOscura)
                Text("Cuéntanos qué podemos mejorar. El mensaje llegará directamente al equipo de Dulce Miel.")
            }

            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Categoría", fontWeight = FontWeight.SemiBold)
                    androidx.compose.foundation.layout.Box {
                        OutlinedButton(onClick = { menuAbierto = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(categoriasSugerencia.first { it.first == categoria }.second)
                        }
                        DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                            categoriasSugerencia.forEach { (valor, etiqueta) ->
                                DropdownMenuItem(
                                    text = { Text(etiqueta) },
                                    onClick = { categoria = valor; menuAbierto = false; vm.limpiarMensaje() }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = mensaje,
                        onValueChange = { nuevo ->
                            if (nuevo.length <= 1000) mensaje = nuevo
                            vm.limpiarMensaje()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Tu sugerencia") },
                        placeholder = { Text("Escribe aquí tu comentario…") },
                        minLines = 5,
                        maxLines = 8,
                        supportingText = { Text("${mensaje.length}/1000 · mínimo 10 caracteres") },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )

                    Text("No incluyas contraseñas ni información de pago. El buzón guarda el mensaje, su categoría y la fecha de envío.", style = MaterialTheme.typography.bodySmall)

                    Button(
                        onClick = { vm.enviar(mensaje, categoria) },
                        enabled = !estado.enviando && mensaje.trim().length in 10..1000,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (estado.enviando) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(Modifier.size(10.dp))
                            Text("Enviando…")
                        } else {
                            Icon(Icons.Default.CloudUpload, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text("Enviar sugerencia")
                        }
                    }

                    estado.mensaje?.let { texto ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                if (estado.enviado) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = if (estado.enviado) Hoja else MaterialTheme.colorScheme.error
                            )
                            Text(texto, color = if (estado.enviado) Hoja else MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (estado.enviado && mensaje.isNotEmpty()) {
        // El éxito queda visible mientras se limpia el formulario para evitar reenvíos accidentales.
        androidx.compose.runtime.LaunchedEffect(estado.enviado) { mensaje = "" }
    }
}
