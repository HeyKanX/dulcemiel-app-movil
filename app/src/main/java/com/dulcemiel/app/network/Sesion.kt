package com.dulcemiel.app.network

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class Sesion(context: Context) {
    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
    private val clave by lazy { obtenerClave() }

    init {
        val campos = listOf("token", "nombre", "email", "rol")
        if (campos.any(prefs::contains) && campos.any { campo -> prefs.getString(campo, null)?.let(::descifrar) == null }) {
            // Las instalaciones anteriores guardaban la sesión sin cifrar: se fuerza un nuevo inicio seguro.
            prefs.edit().clear().apply()
        }
    }

    var token: String? get() = leer("token"); set(value) { guardar("token", value) }
    var nombre: String? get() = leer("nombre"); private set(value) { guardar("nombre", value) }
    var email: String? get() = leer("email"); private set(value) { guardar("email", value) }
    var rol: String? get() = leer("rol"); private set(value) { guardar("rol", value) }

    fun iniciar(token: String, nombre: String, email: String, rol: String) {
        this.token = token
        this.nombre = nombre
        this.email = email
        this.rol = rol
    }

    fun actualizarPerfil(nombre: String, email: String, rol: String) {
        this.nombre = nombre
        this.email = email
        this.rol = rol
    }

    fun cerrar() { prefs.edit().clear().apply() }

    private fun guardar(nombre: String, valor: String?) {
        prefs.edit().apply {
            if (valor == null) remove(nombre) else putString(nombre, cifrar(valor))
        }.apply()
    }

    private fun leer(nombre: String): String? = prefs.getString(nombre, null)?.let(::descifrar)

    private fun cifrar(valor: String): String {
        val cipher = Cipher.getInstance(TRANSFORMACION).apply { init(Cipher.ENCRYPT_MODE, clave) }
        val datos = cipher.iv + cipher.doFinal(valor.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(datos, Base64.NO_WRAP)
    }

    private fun descifrar(valor: String): String? = runCatching {
        val datos = Base64.decode(valor, Base64.NO_WRAP)
        require(datos.size > TAMANIO_IV)
        val cipher = Cipher.getInstance(TRANSFORMACION).apply {
            init(Cipher.DECRYPT_MODE, clave, javax.crypto.spec.GCMParameterSpec(128, datos.copyOfRange(0, TAMANIO_IV)))
        }
        String(cipher.doFinal(datos.copyOfRange(TAMANIO_IV, datos.size)), Charsets.UTF_8)
    }.getOrNull()

    private fun obtenerClave(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(ALIAS_CLAVE, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(
                KeyGenParameterSpec.Builder(ALIAS_CLAVE, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
        }.generateKey()
    }

    private companion object {
        const val ALIAS_CLAVE = "dulcemiel_sesion_aes"
        const val TRANSFORMACION = "AES/GCM/NoPadding"
        const val TAMANIO_IV = 12
    }
}
