package com.example.sispark

object UserSession {
    var idUsuario: Int = 0
    var nombre: String? = null
    var apellido: String? = null

    fun getNombreCompleto(): String {
        return "${nombre ?: ""} ${apellido ?: ""}".trim()
    }

    fun clearSession() {
        idUsuario = 0
        nombre = null
        apellido = null
    }
}
