package com.dulcemiel.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable fun PantallaLogin(login:(String,String)->Unit, registrar:(String,String,String)->Unit) {
    var email by remember { mutableStateOf("") }; var password by remember { mutableStateOf("") }; var nombre by remember { mutableStateOf("") }; var esRegistro by remember { mutableStateOf(false) }
    Column(Modifier.padding(24.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Text(if(esRegistro) "Crear cuenta" else "Iniciar sesión", style=MaterialTheme.typography.headlineMedium)
        Text("Usa tu cuenta de Dulce Miel para sincronizar carrito y pedidos.")
        if(esRegistro) OutlinedTextField(nombre,{nombre=it},label={Text("Nombre")},singleLine=true)
        OutlinedTextField(email,{email=it},label={Text("Correo")},singleLine=true)
        OutlinedTextField(password,{password=it},label={Text("Contraseña")},singleLine=true,visualTransformation=PasswordVisualTransformation())
        Button(onClick={if(esRegistro) registrar(nombre,email,password) else login(email,password)}, enabled=email.isNotBlank()&&password.isNotBlank()&&(!esRegistro||nombre.isNotBlank())){Text(if(esRegistro) "Registrarme" else "Ingresar")}
        TextButton(onClick={esRegistro=!esRegistro}){Text(if(esRegistro) "Ya tengo una cuenta" else "Crear una cuenta")}
    }
}
