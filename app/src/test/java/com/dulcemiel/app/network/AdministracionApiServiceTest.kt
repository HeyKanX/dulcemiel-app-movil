package com.dulcemiel.app.network

import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AdministracionApiServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var api: AdministracionApiService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor { "test-token" })
            .build()
        api = Retrofit.Builder()
            .baseUrl(server.url("/api/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AdministracionApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `listar productos administrativos envia GET y token`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"success":true,"message":"ok","data":[]}
        """.trimIndent()))

        val response = api.todosProductos()
        val request = server.takeRequest(1, TimeUnit.SECONDS)!!

        assertTrue(response.success)
        assertTrue(response.data.isEmpty())
        assertRequest(request, "GET", "/api/productos/admin/todos")
    }

    @Test
    fun `crear producto envia POST multipart y acepta 201`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(201).setBody("""
            {"success":true,"message":"Producto creado","data":null}
        """.trimIndent()))
        val fields = mapOf("nombre" to "Miel de prueba".toRequestBody("text/plain".toMediaType()))
        val image = MultipartBody.Part.createFormData(
            "imagen", "miel.txt", "contenido".toRequestBody("text/plain".toMediaType())
        )

        val response = api.crearProducto(fields, image)
        val request = server.takeRequest(1, TimeUnit.SECONDS)!!

        assertEquals("Producto creado", response.message)
        assertRequest(request, "POST", "/api/productos")
        assertTrue(request.body.readUtf8().contains("Miel de prueba"))
    }

    @Test
    fun `editar producto envia PUT a la ruta con id`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"success":true,"message":"Producto actualizado","data":null}
        """.trimIndent()))
        val fields = mapOf("nombre" to "Miel editada".toRequestBody("text/plain".toMediaType()))

        val response = api.editarProducto(27, fields, null)
        val request = server.takeRequest(1, TimeUnit.SECONDS)!!

        assertEquals("Producto actualizado", response.message)
        assertRequest(request, "PUT", "/api/productos/27")
    }

    @Test
    fun `eliminar producto envia DELETE`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"success":true,"message":"Producto eliminado","data":null}
        """.trimIndent()))

        val response = api.eliminarProducto(27)
        val request = server.takeRequest(1, TimeUnit.SECONDS)!!

        assertEquals("Producto eliminado", response.message)
        assertRequest(request, "DELETE", "/api/productos/27")
    }

    @Test
    fun `conflicto HTTP se conserva para que el ViewModel lo pueda tratar`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(409).setBody("""
            {"success":false,"message":"Producto asociado a un pedido"}
        """.trimIndent()))

        val error = try {
            api.eliminarProducto(27)
            null
        } catch (exception: HttpException) {
            exception
        }
        val request = server.takeRequest(1, TimeUnit.SECONDS)!!

        assertEquals(409, error?.code() ?: -1)
        assertRequest(request, "DELETE", "/api/productos/27")
    }

    private fun assertRequest(request: RecordedRequest, method: String, path: String) {
        assertEquals(method, request.method)
        assertEquals(path, request.path)
        assertEquals("Bearer test-token", request.getHeader("Authorization"))
    }
}
