package com.example.sispark

object UserSession {
    var idUsuario: Int = 0

    var identificacion: String? = null
    var nombre: String? = null
    var apellido: String? = null
    var usuario: String? = null

    fun getNombreCompleto(): String {
        return "${nombre ?: ""} ${apellido ?: ""}".trim()
    }

    fun clearSession() {
        idUsuario = 0
        identificacion = null
        nombre = null
        apellido = null
        usuario = null
    }
}
