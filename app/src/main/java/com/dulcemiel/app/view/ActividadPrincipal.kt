package com.dulcemiel.app.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dulcemiel.app.model.provider.FuenteDatosLocal
import com.dulcemiel.app.repository.RepositorioTienda
import com.dulcemiel.app.ui.theme.TemaDulceMiel
import com.dulcemiel.app.viewmodel.FabricaModeloVistaTienda

class ActividadPrincipal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaDulceMiel {
                DulceMielApp(viewModel(factory = FabricaModeloVistaTienda(RepositorioTienda(FuenteDatosLocal(applicationContext)))))
            }
        }
    }
}
