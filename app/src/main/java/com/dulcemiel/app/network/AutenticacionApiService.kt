package com.dulcemiel.app.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Endpoints de identidad, sesión y usuarios. */
interface AutenticacionApiService {
    @POST("auth/login") suspend fun login(@Body body: LoginBody): LoginRespuesta
    @POST("auth/register") suspend fun registrar(@Body body: RegistroBody): Respuesta<Any>
    @GET("auth/perfil") suspend fun perfil(): PerfilRespuesta
    @GET("auth/usuarios") suspend fun usuarios(): UsuariosRespuesta
    @POST("auth/empleados") suspend fun crearEmpleado(@Body body: EmpleadoBody): Respuesta<Any>
    @PUT("auth/usuarios/{id}") suspend fun editarUsuario(@Path("id") id: Int, @Body body: UsuarioActualizacionBody): Respuesta<Any>
}
