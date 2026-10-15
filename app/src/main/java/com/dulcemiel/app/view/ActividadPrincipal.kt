package com.dulcemiel.app.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import com.dulcemiel.app.network.NetworkConnectivityHelper
import com.dulcemiel.app.network.Sesion
import com.dulcemiel.app.network.crearServicios
import com.dulcemiel.app.repository.RepositorioTienda
import com.dulcemiel.app.ui.theme.TemaDulceMiel
import com.dulcemiel.app.viewmodel.FabricaModeloVistaTienda

class ActividadPrincipal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaDulceMiel {
                val sesion = remember { Sesion(applicationContext) }
                val servicios = remember { crearServicios { sesion.token } }
                val repositorio = remember { RepositorioTienda(servicios, sesion) }
                val conectividad = remember { NetworkConnectivityHelper(applicationContext) }
                DulceMielApp(viewModel(factory = FabricaModeloVistaTienda(repositorio, conectividad)))
            }
        }
    }
}
