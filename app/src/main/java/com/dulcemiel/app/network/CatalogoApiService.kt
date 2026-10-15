package com.dulcemiel.app.network

import retrofit2.http.GET
import retrofit2.http.Path

/** Endpoints públicos de consulta de productos. */
interface CatalogoApiService {
    @GET("productos") suspend fun productos(): Respuesta<List<ProductoApi>>
    @GET("productos/{id}") suspend fun producto(@Path("id") id: Int): Respuesta<ProductoApi>
}
