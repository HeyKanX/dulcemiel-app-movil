# Dulce Miel Mobile

Prototipo Android en Kotlin y Jetpack Compose para consultar un catálogo de muestra, planificar una compra y conservar un carrito y pedidos de demostración en el dispositivo.

## Funciones actuales

- Inicio, catálogo, detalle, carrito, pedidos locales y consejos de compra y conservación.
- Cantidades limitadas por las existencias de muestra y total calculado en céntimos.
- Carrito y pedidos guardados en preferencias privadas del dispositivo. Se recuperan al reiniciar la aplicación.
- Mensaje explícito al guardar un pedido: **no se ha enviado a Dulce Miel**.

Los productos, precios y existencias son datos codificados para la demostración. No hay autenticación, API, pagos, notificaciones ni seguimiento de entregas real.

## Arquitectura

La estructura sigue los paquetes del ejemplo de clase:

```text
com.dulcemiel.app
├── model
│   ├── Producto.kt y Pedido.kt
│   └── provider/FuenteDatosLocal.kt y ProveedorProductos.kt
├── repository/RepositorioTienda.kt
├── ui/theme/Colores.kt y TemaDulceMiel.kt
├── view/ActividadPrincipal.kt, VistaPrincipal.kt y pantallas separadas
└── viewmodel/EstadoAplicacion.kt, LineaCarrito.kt, Pantalla.kt y ModeloVistaTienda.kt
```

La vista solo dibuja el estado y envía eventos. `ModeloVistaTienda` valida acciones, prepara las líneas y el total del carrito, y actualiza `EstadoAplicacion`; `RepositorioTienda` media el acceso al catálogo de muestra y a la fuente local. `ProveedorProductos` suministra los datos de muestra; `FuenteDatosLocal` conserva carrito y pedidos mediante `SharedPreferences` y JSON. La actividad principal prepara el tema y las dependencias de la aplicación.

## Abrir en otro equipo

1. Descomprimir el ZIP completo y abrir la carpeta `dulcemiel-app-movil` en Android Studio.
2. Instalar **Android SDK Platform 37** desde SDK Manager si Android Studio lo solicita. La primera sincronización necesita Internet para descargar Gradle 9.5.0, Android Gradle Plugin 9.3.0, Kotlin 2.4.20 y las bibliotecas AndroidX.
3. Usar el JDK configurado por Android Studio. El proyecto declara Java 21 para el código y el archivo `gradle/gradle-daemon-jvm.properties` solicita un JDK 25 para ejecutar Gradle; Gradle puede descargarlo si no está disponible.
4. Seleccionar un emulador o teléfono con Android 8.0 (API 26) o posterior y ejecutar el módulo `app`.

También se puede compilar con `gradlew.bat :app:assembleDebug` en Windows o `./gradlew :app:assembleDebug` en macOS y Linux, con el Android SDK configurado en el equipo. El proyecto usa `minSdk 26`, `targetSdk 36` y `compileSdk 37`. No se incluye `local.properties`: Android Studio genera ese archivo para la ruta del SDK de cada equipo.

## Comprobación manual

1. Añadir un producto, cambiar su cantidad y verificar el total.
2. Cerrar y abrir la aplicación; confirmar que el carrito se conserva.
3. Guardar el pedido local y comprobar que se muestra en Pedidos con el aviso de que no fue enviado.
4. Reiniciar y comprobar que el pedido permanece y el carrito está vacío.
5. Recorrer controles con TalkBack y fuente ampliada antes de cualquier entrega a usuarios.

Los documentos académicos se entregan por separado del código de la aplicación.
