package com.dulcemiel.app.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.dulcemiel.app.BuildConfig
import com.dulcemiel.app.model.UbicacionEntrega
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.launch

private val CentroLima = LatLng(-12.0464, -77.0428)

@Composable
fun DialogoUbicacionEntrega(
    ubicacionInicial: UbicacionEntrega?,
    cerrar: () -> Unit,
    confirmar: (UbicacionEntrega) -> Unit,
    quitar: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val proveedorUbicacion = remember(context) { LocationServices.getFusedLocationProviderClient(context) }
    var ubicacionSeleccionada by remember(ubicacionInicial) {
        mutableStateOf(ubicacionInicial?.let { LatLng(it.latitud, it.longitud) })
    }
    var cargandoUbicacion by remember { mutableStateOf(false) }
    var mensajeUbicacion by remember { mutableStateOf<String?>(null) }
    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    val posicionCamara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            ubicacionSeleccionada ?: CentroLima,
            if (ubicacionSeleccionada == null) 11f else 16f
        )
    }

    fun obtenerUbicacionActual() {
        val permisoPreciso = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val permisoAproximado = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!permisoPreciso && !permisoAproximado) {
            mensajeUbicacion = "Concede el permiso de ubicación mientras usas la app, o selecciona el punto tocando el mapa."
            return
        }
        tienePermiso = true
        cargandoUbicacion = true
        mensajeUbicacion = null
        val prioridad = if (permisoPreciso) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY
        val token = CancellationTokenSource()
        proveedorUbicacion.getCurrentLocation(prioridad, token.token)
            .addOnSuccessListener { location ->
                cargandoUbicacion = false
                if (location == null) {
                    mensajeUbicacion = "No se obtuvo una ubicación. Activa la ubicación del dispositivo o toca el mapa para marcar el punto."
                } else {
                    val punto = LatLng(location.latitude, location.longitude)
                    ubicacionSeleccionada = punto
                    mensajeUbicacion = "Ubicación obtenida. Puedes mover el punto tocando el mapa."
                    scope.launch { posicionCamara.animate(CameraUpdateFactory.newLatLngZoom(punto, 17f)) }
                }
            }
            .addOnFailureListener {
                cargandoUbicacion = false
                mensajeUbicacion = "No se pudo obtener la ubicación. Puedes marcar el punto manualmente en el mapa."
            }
    }

    val permisoUbicacion = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        tienePermiso = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (tienePermiso) obtenerUbicacionActual()
        else mensajeUbicacion = "No se concedió el permiso. Puedes seguir y elegir el punto manualmente en el mapa."
    }

    AlertDialog(
        onDismissRequest = cerrar,
        title = { Text("Punto de entrega") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "El permiso solo se usa mientras esta pantalla está abierta para centrar el mapa. No rastreamos tu ubicación en segundo plano. El punto se adjunta al pedido cuando lo envías.",
                    style = MaterialTheme.typography.bodySmall
                )
                if (BuildConfig.MAPS_API_KEY.isBlank()) {
                    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.medium) {
                        Text(
                            "Falta configurar MAPS_API_KEY en local.properties. La clave no se incluye en el repositorio. Después de agregarla y sincronizar Gradle, el mapa estará disponible.",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    GoogleMap(
                        modifier = Modifier.fillMaxWidth().height(360.dp),
                        cameraPositionState = posicionCamara,
                        properties = MapProperties(isMyLocationEnabled = tienePermiso),
                        uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = false),
                        onMapClick = { punto ->
                            ubicacionSeleccionada = punto
                            mensajeUbicacion = "Punto marcado. Confírmalo para asociarlo al pedido."
                        }
                    ) {
                        ubicacionSeleccionada?.let { punto ->
                            Marker(
                                state = rememberUpdatedMarkerState(position = punto),
                                title = "Punto de entrega",
                                snippet = "Toca otra ubicación para mover el marcador"
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = {
                                if (tienePermiso) obtenerUbicacionActual()
                                else permisoUbicacion.launch(
                                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                                )
                            },
                            enabled = !cargandoUbicacion
                        ) {
                            if (cargandoUbicacion) CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
                            else androidx.compose.material3.Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text(if (cargandoUbicacion) "Buscando…" else "Usar mi ubicación")
                        }
                        if (ubicacionSeleccionada != null) {
                            Text("Punto seleccionado", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    mensajeUbicacion?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary) }
                }
                Text(
                    "También puedes escribir tu dirección manualmente. La ubicación del mapa es opcional.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    ubicacionSeleccionada?.let { confirmar(UbicacionEntrega(it.latitude, it.longitude)) }
                },
                enabled = ubicacionSeleccionada != null && BuildConfig.MAPS_API_KEY.isNotBlank()
            ) { Text("Confirmar punto") }
        },
        dismissButton = {
            Row {
                if (ubicacionInicial != null) TextButton(onClick = quitar) { Text("Quitar punto") }
                TextButton(onClick = cerrar) { Text("Cancelar") }
            }
        }
    )
}
