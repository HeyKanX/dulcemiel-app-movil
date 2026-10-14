package com.dulcemiel.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dulcemiel.app.ui.theme.MielOscura as HoneyDark

@Composable
internal fun PantallaPerfil(nombre: String?, email: String?, rol: String?, puedeAdministrar: Boolean, abrirDashboard: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = HoneyDark)
                    Text(nombre ?: "Mi cuenta", fontWeight = FontWeight.ExtraBold, fontSize = 23.sp)
                    Text(email.orEmpty())
                    Text(rol?.replaceFirstChar { it.uppercase() } ?: "Usuario", color = HoneyDark, fontWeight = FontWeight.Bold)
                }
            }
        }
        if (puedeAdministrar) item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0C2))) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = HoneyDark)
                    Text("Panel administrativo", fontWeight = FontWeight.Bold)
                    Text("Consulta el resumen operativo de Dulce Miel.", fontSize = 13.sp)
                    Button(onClick = abrirDashboard) { Text("Abrir dashboard") }
                }
            }
        }
    }
}
