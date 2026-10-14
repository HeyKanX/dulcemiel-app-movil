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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.dulcemiel.app.model.Promocion
import com.dulcemiel.app.ui.theme.Hoja as Leaf

@Composable
internal fun PantallaGestionPromociones(promociones: List<Promocion>, guardar: (Promocion) -> Unit, eliminar: (Int) -> Unit) {
    var edicion by remember { mutableStateOf<Promocion?>(null) }
    var titulo by remember { mutableStateOf("") }; var descripcion by remember { mutableStateOf("") }; var descuento by remember { mutableStateOf("0") }
    var inicio by remember { mutableStateOf("") }; var fin by remember { mutableStateOf("") }; var activa by remember { mutableStateOf(true) }
    fun cargar(p: Promocion?) { edicion=p; titulo=p?.titulo.orEmpty(); descripcion=p?.descripcion.orEmpty(); descuento=p?.descuento?.toString() ?: "0"; inicio=p?.fechaInicio?.take(10).orEmpty(); fin=p?.fechaFin?.take(10).orEmpty(); activa=p?.activa ?: true }
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { TituloSeccion(if (edicion == null) "Nueva promoción" else "Editar promoción", "Visible en el inicio si está activa y vigente") }
        item { Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            OutlinedTextField(titulo, { titulo=it }, label={ Text("Título") }, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(descripcion, { descripcion=it }, label={ Text("Descripción") }, modifier=Modifier.fillMaxWidth(), minLines=2)
            OutlinedTextField(descuento, { descuento=it }, label={ Text("Descuento %") }, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(inicio, { inicio=it }, label={ Text("Inicio (AAAA-MM-DD)") }, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(fin, { fin=it }, label={ Text("Fin opcional (AAAA-MM-DD)") }, modifier=Modifier.fillMaxWidth())
            FilterChip(selected=activa, onClick={activa=!activa}, label={Text(if(activa) "Activa" else "Inactiva")})
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { Button(onClick={ guardar(Promocion(edicion?.id ?: 0,titulo,descripcion.ifBlank{null},descuento.toDoubleOrNull() ?: -1.0,inicio,fin.ifBlank{null},activa)); if(edicion != null) cargar(null) }) { Text("Guardar") }; if(edicion != null) TextButton(onClick={cargar(null)}){Text("Cancelar")} }
        } } }
        item { TituloSeccion("Promociones", "${promociones.size} registradas") }
        items(promociones) { promo -> Card(Modifier.fillMaxWidth().clickable{cargar(promo)}, colors=CardDefaults.cardColors(containerColor=Color.White)) { Row(Modifier.padding(16.dp), horizontalArrangement=Arrangement.SpaceBetween) { Column(Modifier.weight(1f)) { Text(promo.titulo,fontWeight=FontWeight.Bold); Text("${promo.descuento}% · ${promo.fechaInicio.take(10)}",fontSize=13.sp); Text(if(promo.activa) "Activa" else "Inactiva",color=if(promo.activa) Leaf else Color.Gray,fontSize=12.sp) }; Column { TextButton(onClick={cargar(promo)}){Text("Editar")}; TextButton(onClick={eliminar(promo.id)}){Text("Eliminar")} } } } }
    }
}
