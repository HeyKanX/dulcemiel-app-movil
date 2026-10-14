package com.dulcemiel.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hive
import androidx.compose.material.icons.filled.Image
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
import com.dulcemiel.app.model.ArchivoAdjunto
import com.dulcemiel.app.model.CarruselEdicion
import com.dulcemiel.app.model.ConfiguracionSitioEdicion
import com.dulcemiel.app.model.SlideInicio
import com.dulcemiel.app.ui.theme.Hoja as Leaf

@Composable
internal fun PantallaGestionSitio(nombreActual: String, descripcionActual: String, misionActual: String, visionActual: String, logoUrl: String?, carrusel: List<SlideInicio>, guardarConfig: (ConfiguracionSitioEdicion)->Unit, guardarCarrusel: (CarruselEdicion)->Unit, eliminarCarrusel: (Int)->Unit) {
    var nombre by remember(nombreActual){mutableStateOf(nombreActual)}; var descripcion by remember(descripcionActual){mutableStateOf(descripcionActual)}; var mision by remember(misionActual){mutableStateOf(misionActual)}; var vision by remember(visionActual){mutableStateOf(visionActual)}; var logo by remember{mutableStateOf<ArchivoAdjunto?>(null)}
    var editando by remember{mutableStateOf<SlideInicio?>(null)}; var titulo by remember{mutableStateOf("")}; var descripcionSlide by remember{mutableStateOf("")}; var orden by remember{mutableStateOf("0")}; var activo by remember{mutableStateOf(true)}; var imagen by remember{mutableStateOf<ArchivoAdjunto?>(null)}
    fun cargarSlide(slide: SlideInicio?) { editando=slide; titulo=slide?.titulo.orEmpty(); descripcionSlide=slide?.descripcion.orEmpty(); orden=slide?.orden?.toString() ?: "0"; activo=slide?.activo ?: true; imagen=null }
    LazyColumn(contentPadding=PaddingValues(20.dp), verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item { TituloSeccion("Marca e inicio", "Configura el contenido público de Dulce Miel") }
        item { Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(18.dp)){Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(9.dp)){
            ImagenRemota(logoUrl,"Logo actual",Modifier.fillMaxWidth().padding(bottom=4.dp),Icons.Default.Hive)
            OutlinedTextField(nombre,{nombre=it},label={Text("Nombre de empresa")},modifier=Modifier.fillMaxWidth())
            OutlinedTextField(descripcion,{descripcion=it},label={Text("Descripción inicial")},modifier=Modifier.fillMaxWidth(),minLines=2)
            OutlinedTextField(mision,{mision=it},label={Text("Misión")},modifier=Modifier.fillMaxWidth(),minLines=2)
            OutlinedTextField(vision,{vision=it},label={Text("Visión")},modifier=Modifier.fillMaxWidth(),minLines=2)
            SelectorImagen("Cambiar logo",logo){logo=it}
            Button(onClick={guardarConfig(ConfiguracionSitioEdicion(nombre,descripcion,mision,vision,logo))}){Text("Guardar configuración")}
        }}}
        item { TituloSeccion(if(editando==null)"Nueva imagen de carrusel" else "Editar imagen de carrusel","La imagen es requerida al crear") }
        item { Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(18.dp)){Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(9.dp)){
            OutlinedTextField(titulo,{titulo=it},label={Text("Título")},modifier=Modifier.fillMaxWidth())
            OutlinedTextField(descripcionSlide,{descripcionSlide=it},label={Text("Descripción")},modifier=Modifier.fillMaxWidth())
            OutlinedTextField(orden,{orden=it},label={Text("Orden")},modifier=Modifier.fillMaxWidth())
            FilterChip(selected=activo,onClick={activo=!activo},label={Text(if(activo)"Activo" else "Inactivo")})
            SelectorImagen("Seleccionar imagen",imagen){imagen=it}
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick={guardarCarrusel(CarruselEdicion(editando?.id,titulo,descripcionSlide,orden,activo,imagen));if(editando!=null)cargarSlide(null)}){Text("Guardar carrusel")};if(editando!=null)TextButton(onClick={cargarSlide(null)}){Text("Cancelar")}}
        }}}
        item { TituloSeccion("Carrusel publicado","${carrusel.size} imágenes activas") }
        items(carrusel){slide->Card(colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){ImagenRemota(slide.imagenUrl,slide.titulo,Modifier.fillMaxWidth(),Icons.Default.Image);Text(slide.titulo,fontWeight=FontWeight.Bold);slide.descripcion?.let{Text(it,fontSize=13.sp)};Row{TextButton(onClick={cargarSlide(slide)}){Text("Editar")};TextButton(onClick={eliminarCarrusel(slide.id)}){Text("Eliminar",color=Leaf)}}}}}
    }
}
