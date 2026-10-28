# Reporte del Criterio 5: MVVM y UI State

**Proyecto:** Dulce Miel Mobile  
**Fecha:** 20 de octubre de 2026  
**Revisión base:** `63823b6` (`main`)  
**Alcance:** revisión estática del código Android. No se modificó código, no se compilaron fuentes y no se ejecutaron pruebas.

## Criterio evaluado

> Arquitectura MVVM (UI, ViewModel, Repository), gestión de UI State (carga, error, sin datos y conectividad) y pruebas básicas.

## Resultado general

**Cumplimiento parcial.** El proyecto tiene una separación reconocible de UI, ViewModel y Repository, usa estado observable para Compose y realiza solicitudes Retrofit en coroutines. Hay manejo básico de carga, mensajes de error y conectividad. Falta un modelo de estado por pantalla que distinga carga, éxito, vacío y error; algunos fallos dejan contenido presentado como si aún cargara; las acciones de reintento no están modeladas; y no existen pruebas Android/JVM del módulo app.

## Evidencia revisada

- `app/src/main/java/com/dulcemiel/app/view`: pantallas Compose y navegación.
- `app/src/main/java/com/dulcemiel/app/viewmodel/EstadoAplicacion.kt`: estado compartido.
- `app/src/main/java/com/dulcemiel/app/viewmodel/ModeloVistaTienda.kt`: acciones, coroutines, coordinación y errores.
- `app/src/main/java/com/dulcemiel/app/repository/RepositorioTienda.kt`: adaptación de datos y acceso a servicios.
- `app/src/main/java/com/dulcemiel/app/network`: servicios Retrofit y conectividad.
- `app/src/main/java/com/dulcemiel/app/view/ActividadPrincipal.kt`: composición de dependencias.
- `app/build.gradle.kts`: configuración del módulo app.

## Evaluación por componente

### 1. UI / View

**Presente:** las pantallas están en `view`, la UI recibe modelos/estado y callbacks, y los eventos se delegan al ViewModel. `VistaPrincipal` conecta `vm.estado` con las pantallas, muestra un `Snackbar` para `mensaje` y un indicador global para `cargando`. Los formularios conservan estado visual local con Compose, que es apropiado para valores temporales de entrada.

**Brechas observadas:** varias pantallas reciben solamente una lista y no reciben su estado de carga/error. Por ello no pueden elegir una presentación específica según el resultado de la solicitud. `PantallaCatalogo` itera los productos, pero no tiene un caso vacío explícito. En listas de pedidos, una lista vacía siempre se describe como “Aún no tienes pedidos” o “No hay pedidos”, aunque también puede estar vacía porque la carga falló.

### 2. ViewModel

**Presente:** `ModeloVistaTienda` coordina acciones, lanza operaciones con `viewModelScope`, valida algunas entradas y actualiza `EstadoAplicacion`. El ViewModel captura `HttpException`, timeout, host desconocido y `IOException`; distingue los códigos HTTP principales y revisa conectividad antes de iniciar una petición.

**Brechas observadas:** el ViewModel concentra navegación, autenticación/roles de UI, reglas de formularios, coordinación de datos y presentación de mensajes en una sola clase. `productos` es estado aparte de `estado`, mientras que el resto de los datos vive en `EstadoAplicacion`, lo que crea dos superficies de estado observables.

Las solicitudes concurrentes comparten un único `cargando: Boolean`. Al iniciar varias llamadas (por ejemplo, las cargas iniciales o privadas) una respuesta rápida puede ponerlo en `false` mientras otras siguen activas. Asimismo, operaciones anidadas disparan nuevos `ejecutar`, por lo que el indicador no representa de forma fiable la carga de cada pantalla o acción.

### 3. Repository

**Presente:** `RepositorioTienda` separa a la UI del detalle de Retrofit, consume interfaces por dominio, transforma DTO de red a modelos de la app y centraliza las operaciones remotas.

**Brechas observadas:** devuelve resultados o listas y deja que las excepciones Retrofit se propaguen al ViewModel; no ofrece un resultado tipado que distinga éxito, error de servidor, validación y falta de conectividad. Depende del contenedor concreto `ServiciosDulceMiel`, aunque sus servicios individuales son interfaces. No se observó una capa de caché para lecturas sin conexión; esto no impide MVVM, pero limita resiliencia.

### 4. UI State: carga, error, sin datos y conectividad

| Estado esperado | Evidencia actual | Evaluación |
|---|---|---|
| Carga | `EstadoAplicacion.cargando`, overlay con `CircularProgressIndicator` global en `VistaPrincipal` | Existe, pero es global y puede desincronizarse con solicitudes simultáneas. |
| Error | `mensaje` global presentado como Snackbar; mensajes diferenciados en el ViewModel | Existe como notificación temporal, no como estado persistente de la pantalla. No hay acción de reintento asociada. |
| Sin datos | Algunas listas muestran texto al recibir lista vacía | Parcial. No se diferencia vacío legítimo de fallo o carga aún no completada. El catálogo no tiene mensaje explícito. |
| Conectividad | `NetworkConnectivityHelper` comprueba `NET_CAPABILITY_INTERNET`; `ejecutar` muestra aviso si no encuentra conexión | Existe una comprobación antes de cada operación; no hay estado de conectividad observable/continuo ni indicador persistente. |
| Detalle pendiente | Detalle de pedido y dashboard muestran “Cargando…” cuando su modelo es `null` | No distingue carga de error. Tras una excepción, el dato puede seguir `null` y la vista continuar diciendo “Cargando…” indefinidamente. |

La verificación de conectividad es un chequeo puntual: la red puede perderse después del chequeo y antes/durante la petición. Además, `NET_CAPABILITY_INTERNET` indica que la red está configurada para Internet, pero no confirma por sí sola que el backend local responda. La excepción de red sigue siendo necesaria y ya está manejada de forma general.

## Pruebas básicas

No se encontraron carpetas `app/src/test` ni `app/src/androidTest`, ni archivos de prueba Kotlin para la app. En `app/build.gradle.kts` no están declaradas dependencias `testImplementation` para JUnit, coroutines-test o una librería de dobles; tampoco dependencias de instrumentación para Compose UI tests. La presencia de `androidTestImplementation(composeBom)` y `ui-test-manifest` por sí sola no constituye una suite de pruebas.

No se ejecutaron pruebas durante esta revisión.

## Brechas a resolver

1. **Estados por solicitud o pantalla.** Reemplazar la dependencia de una única bandera global por estados observables que permitan representar `Loading`, `Success(data)`, `Empty` y `Error(message, retryable)` para catálogo, pedidos, dashboard y detalles. Puede usarse `StateFlow` con `asStateFlow()` o un enfoque equivalente y consistente.
2. **Persistir los errores de lectura.** Un error de carga debe quedar visible en la pantalla hasta que se recargue o se navegue, en lugar de depender solo de un Snackbar que desaparece.
3. **Añadir reintentos.** Exponer callbacks de recarga desde la UI para catálogo, inicio, pedidos, dashboard y detalle; mostrar acción apropiada en los estados de error.
4. **Separar vacío de carga/fallo.** Mostrar “sin productos”, “sin pedidos”, etc. solamente tras una respuesta exitosa vacía; durante la carga mostrar progreso y tras un error mostrar error/reintento.
5. **Controlar acciones durante carga.** Deshabilitar botones de envío/guardado que no deban duplicarse, o representar carga por acción, evitando que un único overlay bloquee toda interacción en operaciones no relacionadas.
6. **Agregar pruebas JVM de ViewModel/Repository.** Inyectar dependencias sustituibles (repositorio/conectividad o contratos pequeños), controlar coroutines y verificar estados y eventos sin red real.
7. **Agregar pruebas de UI mínimas.** Cubrir al menos estado de carga, error con reintento, respuesta vacía y respuesta con datos en una pantalla crítica.
8. **Documentar evidencias ejecutadas.** Anotar comandos, resultado y entorno, distinguiendo pruebas automatizadas de comprobación manual.

## Propuesta de pruebas mínimas de aceptación

| ID | Caso | Resultado que debe comprobarse |
|---|---|---|
| VM-01 | Carga exitosa con catálogo no vacío | Estado pasa de carga a contenido; lista y acciones se habilitan. |
| VM-02 | API devuelve catálogo vacío | Estado muestra vacío legítimo, no error ni carga permanente. |
| VM-03 | API falla con HTTP 500 o IOException | Estado de error permanece visible e incluye opción de reintentar; carga termina. |
| VM-04 | Sin conectividad al iniciar una carga | Se presenta estado sin conexión/error y puede volver a intentar cuando haya red. |
| VM-05 | Dashboard/detalle falla | No queda el placeholder “Cargando…” indefinidamente; error/reintento visible. |
| VM-06 | Dos solicitudes concurrentes | Una solicitud completada no oculta el indicador de otra que aún sigue activa. |
| UI-01 | Lista vacía exitosa en catálogo y pedidos | Texto de vacío claro y no ambiguo para esa pantalla. |
| UI-02 | Error de red en una pantalla | Mensaje accesible y acción de reintento funcional. |

## Criterios de cierre sugeridos

El Criterio 5 se puede marcar como satisfecho cuando:

- La UI no ejecuta red directamente y solo emite eventos al ViewModel.
- ViewModel y Repository tienen responsabilidades claras y dependencias sustituibles en pruebas.
- Cada lectura principal expone estado de carga, datos, vacío y error de forma diferenciada.
- Los fallos de conectividad terminan la carga y ofrecen una recuperación entendible.
- Hay pruebas automatizadas que cubren como mínimo los casos VM-01 a VM-06, además de los estados UI esenciales.
- Se guardan resultados de ejecución en la documentación del proyecto.

## Conclusión

La arquitectura MVVM está iniciada y la app ya tiene una base útil de Compose, ViewModel, Repository, Retrofit, coroutines y manejo de conectividad. El cumplimiento del criterio no es completo porque el UI State es global y ambiguo en varios flujos, y no hay pruebas Android de la app. Las primeras prioridades son separar estados de carga/error/vacío por pantalla, corregir las vistas que permanecen en “Cargando…” tras fallos y agregar una suite JVM pequeña para ViewModel y Repository.

## Addendum: cambios preparados para el Criterio 5

**Rama de trabajo:** `criterion-5-mvvm-ui-state`  
**Base:** `63823b6` (`main`)  
**Estado de validación:** implementación estática; no se ejecutaron pruebas, compilación ni smoke test para reservar esa revisión.

### Cambios aplicados

- Se agregó `RecursoUi` y el modelo sellado `EstadoRecurso` (`Inicial`, `Cargando`, `Contenido`, `Vacio`, `Error`) junto a `EstadoAplicacion`.
- El ViewModel ahora registra estados independientes para inicio, catálogo, detalle de producto, perfil, carrito, pedidos, detalle de pedido, dashboard y listados administrativos.
- Las cargas de esas pantallas terminan en contenido/vacío o error. Los errores de conectividad quedan marcados y visibles en el estado de la pantalla.
- Se agregó `ContenidoConEstado`, que presenta progreso, mensaje vacío o error con acción de reintento. Se conectó a las pantallas de lectura principales y a los listados administrativos.
- Se añadió `reintentar(RecursoUi)` para volver a cargar los recursos aplicables.
- El indicador global usa `operacionesActivas` en vez de un booleano que se apaga al terminar la primera de varias solicitudes concurrentes.

### Alcance aún pendiente de validación o ampliación

- No se incluyeron ni ejecutaron pruebas unitarias, según el alcance solicitado. La carencia de pruebas automatizadas registrada en la revisión base permanece pendiente para una solicitud posterior.
- Todavía no hay pruebas del ViewModel completo, Repository, Compose UI ni integración Retrofit/backend.
- Las acciones de escritura siguen usando el mensaje global y el indicador compartido; aún no tienen estado ocupado por botón/acción ni protección específica contra doble envío.
- La carga de productos destacados del inicio y la carga de configuración del sitio son solicitudes distintas; el inicio muestra el error del sitio, mientras que el error de catálogo queda expuesto al entrar al catálogo.
- No hay un banner continuo de conectividad; la ausencia de red se representa en el recurso que falló y puede recuperarse con Reintentar.

Este addendum registra el trabajo local de la rama; las pruebas unitarias, el smoke test y la aceptación visual quedan deliberadamente fuera de esta entrega.
