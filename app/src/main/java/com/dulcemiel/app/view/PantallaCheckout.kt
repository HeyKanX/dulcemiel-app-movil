package com.dulcemiel.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PantallaCheckout(enviar: (String, String, String?, String, String, String?, String?) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var distrito by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var mostrarErrores by remember { mutableStateOf(false) }
    val telefonoValido = telefono.trim().matches(Regex("^[0-9+()\\s-]{6,20}$"))
    val requeridosCompletos = listOf(nombre, telefono, direccion, distrito).all { it.isNotBlank() }

    Column(
        Modifier.padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Datos de entrega", style = MaterialTheme.typography.headlineSmall)
        Text("Usaremos estos datos para registrar y coordinar tu pedido.", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre de contacto") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
            isError = mostrarErrores && nombre.isBlank())
        OutlinedTextField(
            telefono, { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = mostrarErrores && !telefonoValido,
            supportingText = { if (mostrarErrores && !telefonoValido) Text("Ingresa un teléfono válido de 6 a 20 caracteres.") }
        )
        OutlinedTextField(
            email, { email = it }, label = { Text("Correo electrónico (opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(direccion, { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(),
            isError = mostrarErrores && direccion.isBlank())
        OutlinedTextField(distrito, { distrito = it }, label = { Text("Distrito") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
            isError = mostrarErrores && distrito.isBlank())
        OutlinedTextField(referencia, { referencia = it }, label = { Text("Referencia (opcional)") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
        OutlinedTextField(notas, { notas = it }, label = { Text("Notas para el pedido (opcional)") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
        Button(
            onClick = {
                mostrarErrores = true
                if (requeridosCompletos && telefonoValido) {
                    enviar(nombre.trim(), telefono.trim(), email.trim().ifBlank { null }, direccion.trim(), distrito.trim(), referencia.trim().ifBlank { null }, notas.trim().ifBlank { null })
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Enviar pedido") }
    }
}
