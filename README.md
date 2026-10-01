# Dulce Miel Mobile — prototipo inicial

Prototipo navegable de la migración móvil de **Dulce Miel**, desarrollado con Kotlin, Android Studio, Jetpack Compose y una separación inicial basada en MVVM.

## Alcance del prototipo

Este avance permite demostrar el flujo principal antes de conectar servicios reales:

- Inicio con productos destacados y acceso al consumo responsable.
- Catálogo con precio y stock visible.
- Detalle del producto con información de conservación.
- Carrito con cantidad, total transparente y sin productos añadidos automáticamente.
- Seguimiento claro del estado de un pedido.
- Sección ODS 12 con recomendaciones de compra, conservación, reutilización y control de promociones.
- Etiquetas y descripciones de accesibilidad en los controles principales.

Los productos, existencias y pedidos son **datos simulados**. La API REST, Firebase, autenticación y pagos quedan fuera de este prototipo inicial y se incorporarán en iteraciones posteriores.

## Relación con la semana 4

El proyecto sirve como evidencia del **prototipo inicial** y permite validar criterios de aceptación relacionados con:

1. Consultar productos, precios y existencias antes de comprar.
2. Añadir un producto de forma voluntaria y visualizar el total.
3. Consultar el estado de un pedido con etapas comprensibles.
4. Recibir orientación vinculada con producción y consumo responsables (ODS 12).
5. Evitar patrones de marketing irresponsable: no hay productos preseleccionados, costos ocultos ni promociones obligatorias.

El Product Backlog, las reglas de negocio y los criterios de aceptación deben mantenerse también en el documento académico de la semana 4.

## Abrir y ejecutar

1. En Android Studio, selecciona **Open**.
2. Abre la carpeta `App movil`.
3. Espera a que finalice la sincronización de Gradle.
4. Selecciona el dispositivo virtual **Pixel 10 API 36**.
5. Presiona **Run app**.

Configuración principal: `compileSdk 37`, `targetSdk 36`, `minSdk 26`. Se compila con API 37 porque la versión actual de Compose lo requiere, pero se ejecuta y prueba en Android 16 (API 36).

## Capturas para sustentación

- [Inicio](docs/prototipo-capturas/01-inicio.png)
- [Catálogo](docs/prototipo-capturas/02-catalogo.png)
- [Detalle del producto](docs/prototipo-capturas/03-detalle.png)
- [Carrito](docs/prototipo-capturas/04-carrito.png)
- [Seguimiento de pedidos](docs/prototipo-capturas/05-pedidos.png)
- [ODS 12](docs/prototipo-capturas/06-ods12.png)

## Estructura principal

- `MainActivity.kt`: interfaz, navegación y estado del prototipo.
- `DulceMielViewModel`: estado de pantalla, selección de producto y carrito.
- `docs/prototipo-capturas`: evidencias visuales obtenidas del emulador.

