# Backend Dulce Miel (local)

API REST en Java 21 y Spring Boot 3.5, con SQL Server. Este directorio contiene el código fuente, el esquema de desarrollo y el Maven Wrapper; no contiene credenciales ni dependencias descargadas.

## Requisitos

- Windows 10/11 y Visual Studio Code (o una terminal PowerShell).
- JDK 21 instalado y disponible en `PATH` (`java --version`).
- SQL Server local con TCP/IP habilitado y autenticación de SQL Server habilitada.
- SQL Server Management Studio (SSMS) para preparar la base de datos.
- Conexión a Internet la primera vez: Maven Wrapper descargará Maven y las librerías del proyecto.

## Preparar SQL Server

1. Inicia el servicio de SQL Server y habilita TCP/IP en SQL Server Configuration Manager. Configura un puerto TCP conocido (por ejemplo `1433`) y reinicia el servicio.
2. En SSMS, habilita el modo de autenticación de SQL Server y crea un usuario local con permisos sobre la base `DulceMielBD`.
3. Ejecuta `database/schema.sql` conectado a tu servidor. El script crea la base si no existe, pero elimina y vuelve a crear sus tablas y procedimientos. Úsalo únicamente para una base nueva/de pruebas; no lo ejecutes sobre una base con datos que quieras conservar.

Si SQL Server usa otra dirección o puerto, actualiza `MSSQL_JDBC_URL` en el archivo `.env` del siguiente paso. No pongas contraseñas reales en el repositorio.

## Configurar y ejecutar

Abre una terminal PowerShell en `backend` (la carpeta que contiene `pom.xml`) y crea la configuración local:

```powershell
Copy-Item .env.example .env
```

Edita `.env` y reemplaza `DB_USER` y `DB_PASSWORD` por el login de SQL Server que preparaste. La URL de ejemplo conecta a `localhost:1433`; ajusta servidor/puerto si tu instalación difiere. Cambia también `JWT_SECRET` por una clave privada aleatoria de al menos 32 caracteres.

Ejecuta las pruebas y luego inicia la API:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

La primera ejecución puede tardar mientras Maven descarga herramientas y dependencias. Cuando Spring Boot indique que inició en el puerto 5000, comprueba en el navegador o PowerShell:

```powershell
Invoke-RestMethod http://localhost:5000/api/health
Invoke-RestMethod http://localhost:5000/api/productos
```

`/api/health` confirma que la API está encendida; `/api/productos` también comprueba la conexión con SQL Server.

## Usar la app Android

Deja el backend ejecutándose. El emulador de Android Studio debe usar `http://10.0.2.2:5000/api/`, que es la dirección del PC anfitrión desde el emulador. La app de este repositorio ya tiene esa URL configurada. En un teléfono físico, usa la IP local del PC en la misma red Wi-Fi (por ejemplo `http://192.168.x.x:5000/api/`) y permite el puerto 5000 en el firewall de Windows.

## Solución de problemas

- **`Login failed for user` / error 18456:** revisa que SQL Server Authentication esté habilitado y que usuario/contraseña de `.env` sean correctos; confirma que el login tenga acceso a `DulceMielBD`.
- **No se puede conectar al servidor SQL:** verifica el servicio, TCP/IP, puerto de `MSSQL_JDBC_URL` y reglas de firewall. Para una instancia con nombre, puedes indicar `instanceName=SQLEXPRESS` en la URL JDBC.
- **No existe una tabla o la base:** ejecuta `database/schema.sql` en la instancia local, teniendo presente que recrea tablas.
- **La API responde pero falla una operación de datos:** visita `/api/productos`; `/api/health` no requiere conexión a la base.
- **Puerto 5000 ocupado:** cambia `PORT` en `.env` y actualiza también `API_BASE_URL` en `app/build.gradle.kts`.
- **Versión Java incorrecta:** instala JDK 21 y configura `JAVA_HOME`/`PATH`; comprueba con `java --version`.

## Contenido versionado

Se versionan solo el código, configuración de ejemplo, esquema local y Maven Wrapper. `.env`, `target/`, cachés, IDE y dependencias descargadas se excluyen. Maven obtiene esas dependencias automáticamente durante la primera compilación.
