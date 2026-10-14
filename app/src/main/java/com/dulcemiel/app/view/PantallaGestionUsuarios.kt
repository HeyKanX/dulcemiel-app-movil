package com.dulcemiel.app.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dulcemiel.app.model.UsuarioAdministrativo
import com.dulcemiel.app.ui.theme.Hoja as Leaf

@Composable
internal fun PantallaGestionUsuarios(usuarios: List<UsuarioAdministrativo>, esAdmin: Boolean, crearEmpleado: (String,String,String,String)->Unit, actualizar: (UsuarioAdministrativo)->Unit) {
    var seleccionado by remember { mutableStateOf<UsuarioAdministrativo?>(null) }
    var nombreNuevo by remember { mutableStateOf("") }; var emailNuevo by remember { mutableStateOf("") }; var passwordNuevo by remember { mutableStateOf("") }; var rolNuevo by remember { mutableStateOf("empleado") }
    LazyColumn(contentPadding=PaddingValues(20.dp), verticalArrangement=Arrangement.spacedBy(14.dp)) {
        if (esAdmin) item { Card(colors=CardDefaults.cardColors(containerColor=Color.White)) { Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) { Text("Registrar empleado",fontWeight=FontWeight.Bold); OutlinedTextField(nombreNuevo,{nombreNuevo=it},label={Text("Nombre")},modifier=Modifier.fillMaxWidth()); OutlinedTextField(emailNuevo,{emailNuevo=it},label={Text("Correo")},modifier=Modifier.fillMaxWidth()); OutlinedTextField(passwordNuevo,{passwordNuevo=it},label={Text("Contraseña")},modifier=Modifier.fillMaxWidth()); Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){ listOf("empleado","admin").forEach{rol->FilterChip(selected=rolNuevo==rol,onClick={rolNuevo=rol},label={Text(rol)})} }; Button(onClick={crearEmpleado(nombreNuevo,emailNuevo,passwordNuevo,rolNuevo); nombreNuevo=""; emailNuevo=""; passwordNuevo=""}){Text("Crear acceso")} } } }
        item { TituloSeccion("Usuarios", "${usuarios.size} cuentas registradas") }
        items(usuarios) { usuario -> Card(Modifier.fillMaxWidth().clickable { if(esAdmin) seleccionado=usuario },colors=CardDefaults.cardColors(containerColor=Color.White)) { Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(4.dp)){Text(usuario.nombre,fontWeight=FontWeight.Bold);Text(usuario.email,fontSize=13.sp);Text("${usuario.rol} · ${usuario.estado}",color=if(usuario.estado=="activo")Leaf else Color.Gray,fontSize=12.sp);if(esAdmin)Text("Toca para editar",fontSize=12.sp)} } }
        seleccionado?.let { usuario -> item { EditorUsuario(usuario, { seleccionado=null }, { actualizado -> actualizar(actualizado); seleccionado=null }) } }
    }
}

@Composable private fun EditorUsuario(usuario: UsuarioAdministrativo, cancelar:()->Unit, guardar:(UsuarioAdministrativo)->Unit) {
    var nombre by remember(usuario.id){mutableStateOf(usuario.nombre)}; var email by remember(usuario.id){mutableStateOf(usuario.email)}; var rol by remember(usuario.id){mutableStateOf(usuario.rol)}; var estado by remember(usuario.id){mutableStateOf(usuario.estado)}
    Card(colors=CardDefaults.cardColors(containerColor=Color(0xFFFFF0C2))){Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){Text("Editar usuario",fontWeight=FontWeight.Bold);OutlinedTextField(nombre,{nombre=it},label={Text("Nombre")},modifier=Modifier.fillMaxWidth());OutlinedTextField(email,{email=it},label={Text("Correo")},modifier=Modifier.fillMaxWidth());Row{listOf("usuario","empleado","admin").forEach{v->FilterChip(selected=rol==v,onClick={rol=v},label={Text(v)})}};Row{listOf("activo","inactivo").forEach{v->FilterChip(selected=estado==v,onClick={estado=v},label={Text(v)})}};Row{Button(onClick={guardar(UsuarioAdministrativo(usuario.id,nombre,email,rol,estado))}){Text("Guardar")};TextButton(onClick=cancelar){Text("Cancelar")}}}}
}
