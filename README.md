# Dulce Miel Mobile

Aplicación Android nativa en Kotlin y Jetpack Compose para Dulce Miel. Consume el backend Spring Boot ejecutado localmente desde VS Code.

## Enlaces

- Repositorio: https://github.com/HeyKanX/dulcemiel-app-movil
- API local (desde el emulador Android Studio): `http://10.0.2.2:5000/api/`

## Funcionalidades

- Inicio público dinámico, catálogo, detalle de producto, carrito y checkout.
- Registro, inicio y cierre de sesión; la sesión se conserva de forma privada en el dispositivo.
- Pedidos del cliente y detalle de cada pedido.
- Perfil y accesos según rol.
- Panel administrativo para productos, pedidos, promociones, usuarios, carrusel y configuración del sitio.
- Carga de imágenes desde la app para los recursos admitidos por la API.
- Buzón de sugerencias escrito en Cloud Firestore, con sesión Firebase anónima y reglas de creación validadas.
- Selección opcional de punto de entrega con Google Maps durante el checkout; el enlace queda en la referencia del pedido y se puede abrir desde el detalle del pedido.
- Estados de carga, errores y confirmaciones durante las operaciones remotas.

## Requisitos

- Android Studio actualizado con Android SDK Platform 37.
- JDK administrado por Android Studio.
- Un emulador o dispositivo con Android 8.0 (API 26) o superior.
- Internet para la primera sincronización de Gradle y acceso a la base de datos configurada para el backend.

El proyecto declara `minSdk 26`, `targetSdk 36` y `compileSdk 37`.

## Clonar y ejecutar

```bash
git clone https://github.com/HeyKanX/dulcemiel-app-movil.git
cd dulcemiel-app-movil
```

1. Abra la carpeta clonada en Android Studio.
2. Espere la sincronización de Gradle. Android Studio descarga automáticamente las dependencias necesarias en el equipo local.
3. Inicie el backend siguiendo los pasos de abajo.
4. Elija un emulador y ejecute el módulo `app`.

No se versiona `local.properties`: Android Studio lo crea con la ruta del SDK de cada equipo.

## Compilar por terminal

En Windows:

```powershell
.\gradlew.bat :app:assembleDebug
```

En macOS o Linux:

```bash
./gradlew :app:assembleDebug
```

El APK de prueba se genera localmente en `app/build/outputs/apk/debug/` y no se sube al repositorio.

## API configurada

La URL base está configurada en `app/build.gradle.kts`, dentro de `API_BASE_URL`, y apunta al backend local:

```text
http://10.0.2.2:5000/api/
```

El código del backend y las instrucciones para configurar SQL Server local están en [`backend/README.md`](backend/README.md). Desde la terminal de VS Code, en Windows, puedes iniciarlo así:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Déjalo ejecutándose y verifica `http://localhost:5000/api/health` en el navegador del PC. Luego ejecuta la app en el emulador Android Studio. La dirección `10.0.2.2` conecta el emulador con el PC; no uses `localhost` en la app. Para un teléfono físico conectado a la misma Wi-Fi, reemplaza `10.0.2.2` por la IP local del PC y permite el puerto 5000 en el firewall.

El backend necesita que sus variables de entorno y acceso a base de datos estén configurados localmente. La app permite HTTP para esta conexión local.

## Firebase y buzón de sugerencias

La app conserva el backend REST para autenticación, catálogo, carrito y pedidos. El buzón escribe directamente en Cloud Firestore y no modifica el backend ni su base de datos.

- Proyecto Firebase: `dulcemiel-f65a1`.
- Aplicación Android: `com.dulcemiel.app`.
- Proveedor Firebase Auth: anónimo, utilizado solo para autorizar el envío del buzón.
- Colección: `buzon_sugerencias`.
- Campos: `mensaje`, `categoria`, `fechaCreacion` (hora del servidor) y `uid` (usuario anónimo de Firebase).
- Los clientes pueden crear sugerencias válidas, pero no leer, actualizar ni borrar documentos. El equipo revisa las sugerencias en Firebase Console.
- Las reglas desplegadas se conservan en `firestore.rules`; la configuración del proyecto Firebase CLI está en `firebase.json` y `.firebaserc`.
- `app/google-services.json` conecta esta app con el proyecto Firebase. Es la configuración cliente de Android; no contiene una cuenta de servicio ni credenciales administrativas.

Para desplegar cambios futuros a las reglas se necesita Firebase CLI y acceso autorizado al proyecto:

```powershell
npx firebase-tools login
npx firebase-tools deploy --only firestore:rules --project dulcemiel-f65a1
```

## Google Maps y punto de entrega

El checkout permite seleccionar en Google Maps el punto exacto de entrega o centrar el mapa en la ubicación actual. Android solicita el permiso de ubicación solo cuando se pulsa **Usar mi ubicación**; se acepta ubicación aproximada o precisa y no se pide acceso en segundo plano. Si se deniega el permiso, el cliente aún puede marcar el punto manualmente o completar el pedido con dirección escrita.

Al enviar el pedido, la app agrega a `referencia` un enlace `https://maps.google.com/?q=LATITUD,LONGITUD`. El backend ya admite y devuelve `referencia`, por lo que esta integración no requiere cambios en sus endpoints ni en la base de datos. El enlace aparece en el detalle del pedido del cliente y del panel administrativo para abrir el mapa.

### Crear y restringir una clave de Maps

1. Abre [Google Cloud Console](https://console.cloud.google.com/) e inicia sesión.
2. Crea un proyecto, por ejemplo `DulceMiel Android`, o selecciona uno que ya administres.
3. Configura una cuenta de facturación para ese proyecto si Google Cloud la solicita. Revisa los precios y condiciones vigentes antes de habilitar servicios.
4. En **APIs y servicios → Biblioteca**, busca **Maps SDK for Android** y pulsa **Habilitar**.
5. En **APIs y servicios → Credenciales → Crear credenciales → Clave de API**, genera una clave.
6. Restringe la clave antes de usarla:
   - En **Restricciones de aplicación**, selecciona **Aplicaciones de Android** y agrega el paquete `com.dulcemiel.app` junto con la huella SHA-1 de firma.
   - Obtén la SHA-1 de depuración desde la carpeta `App movil` ejecutando `\.\gradlew.bat signingReport`; copia la que aparece bajo `Variant: debug`.
   - En **Restricciones de API**, limita la clave a **Maps SDK for Android**.
7. En `App movil/local.properties` (el archivo local que Android Studio ya creó), conserva la línea `sdk.dir` existente y agrega:

   ```properties
   MAPS_API_KEY=pega_aqui_tu_clave
   ```

8. Sincroniza Gradle y vuelve a ejecutar la app. `local.properties` está ignorado por Git: no subas la clave al repositorio ni la pegues en archivos Kotlin o en el manifiesto.

Para una compilación de otro equipo, cada desarrollador configura su propia `MAPS_API_KEY` local. Las claves Android se pueden extraer del APK; las restricciones de paquete, SHA-1 y API son necesarias para reducir el uso ajeno.

## Arquitectura de red

La app usa MVVM y Retrofit. `crearServicios` construye un único cliente HTTP autenticado y crea interfaces por dominio: `AutenticacionApiService`, `CatalogoApiService`, `CarritoApiService`, `PedidosApiService`, `AdministracionApiService`, `PromocionesApiService` y `SitioApiService`. `RepositorioTienda` consume esas interfaces y las pantallas se comunican con él por medio de `ModeloVistaTienda`.

La sesión se guarda cifrada con una clave del Android Keystore y la aplicación no permite respaldos automáticos de sus datos. La app detecta ausencia de Internet antes de ejecutar operaciones remotas y muestra mensajes diferenciados para validaciones, permisos, recursos no encontrados, fallos de servidor y credenciales inválidas.

## Contenido del repositorio

Se incluyen el código fuente, recursos, Gradle Wrapper y la configuración mínima para compilar. No se incluyen dependencias descargadas, cachés de Gradle/Kotlin, configuraciones locales de Android Studio, archivos del SDK ni APK/AAB generados.
