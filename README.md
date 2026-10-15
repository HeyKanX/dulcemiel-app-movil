# Dulce Miel Mobile

Aplicación Android nativa en Kotlin y Jetpack Compose para Dulce Miel. Está adaptada a la API REST desplegada: no requiere una base de datos ni backend local.

## Enlaces

- Repositorio: https://github.com/HeyKanX/dulcemiel-app-movil
- API desplegada: https://api-webdulcemiel-2026-cna2g8h7fmb6g3gu.brazilsouth-01.azurewebsites.net/api/

## Funcionalidades

- Inicio público dinámico, catálogo, detalle de producto, carrito y checkout.
- Registro, inicio y cierre de sesión; la sesión se conserva de forma privada en el dispositivo.
- Pedidos del cliente y detalle de cada pedido.
- Perfil y accesos según rol.
- Panel administrativo para productos, pedidos, promociones, usuarios, carrusel y configuración del sitio.
- Carga de imágenes desde la app para los recursos admitidos por la API.
- Estados de carga, errores y confirmaciones durante las operaciones remotas.

## Requisitos

- Android Studio actualizado con Android SDK Platform 37.
- JDK administrado por Android Studio.
- Un emulador o dispositivo con Android 8.0 (API 26) o superior.
- Internet para la primera sincronización de Gradle y para usar la API.

El proyecto declara `minSdk 26`, `targetSdk 36` y `compileSdk 37`.

## Clonar y ejecutar

```bash
git clone https://github.com/HeyKanX/dulcemiel-app-movil.git
cd dulcemiel-app-movil
```

1. Abra la carpeta clonada en Android Studio.
2. Espere la sincronización de Gradle. Android Studio descarga automáticamente las dependencias necesarias en el equipo local.
3. Elija un emulador o teléfono y ejecute el módulo `app`.

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

La URL base se encuentra en `app/build.gradle.kts`, dentro de `API_BASE_URL`. Por defecto apunta a la API desplegada indicada arriba. Si se despliega una nueva API, cambie únicamente ese valor y vuelva a compilar.

## Arquitectura de red

La app usa MVVM y Retrofit. `crearServicios` construye un único cliente HTTP autenticado y crea interfaces por dominio: `AutenticacionApiService`, `CatalogoApiService`, `CarritoApiService`, `PedidosApiService`, `AdministracionApiService`, `PromocionesApiService` y `SitioApiService`. `RepositorioTienda` consume esas interfaces y las pantallas se comunican con él por medio de `ModeloVistaTienda`.

## Contenido del repositorio

Se incluyen el código fuente, recursos, Gradle Wrapper y la configuración mínima para compilar. No se incluyen dependencias descargadas, cachés de Gradle/Kotlin, configuraciones locales de Android Studio, archivos del SDK ni APK/AAB generados.
