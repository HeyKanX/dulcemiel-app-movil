package com.dulcemiel.app.viewmodel

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.dulcemiel.app.network.CatalogoApiService
import com.dulcemiel.app.network.TokenInterceptor

class MensajeHttpParaUsuarioTest {
    private lateinit var server: MockWebServer
    private lateinit var api: CatalogoApiService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = Retrofit.Builder()
            .baseUrl(server.url("/api/"))
            .client(OkHttpClient.Builder().addInterceptor(TokenInterceptor { null }).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatalogoApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `usa el mensaje que devuelve la API para un error HTTP`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(403).setBody("""
            {"success":false,"message":"Acceso denegado por el servidor"}
        """.trimIndent()))

        val error = capturarHttpException { api.producto(9) }

        assertEquals(403, error.code())
        assertEquals("Acceso denegado por el servidor", mensajeHttpParaUsuario(error, true))
    }

    @Test
    fun `usa un mensaje legible por defecto para error 5xx`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500).setBody("""{"success":false}"""))

        val error = capturarHttpException { api.producto(9) }

        assertEquals("El servidor presentó un problema. Inténtalo más tarde. (HTTP 500)", mensajeHttpParaUsuario(error, true))
    }

    private suspend fun capturarHttpException(request: suspend () -> Any): HttpException = try {
        request()
        error("Se esperaba una respuesta HTTP de error")
    } catch (exception: HttpException) {
        exception
    }
}
