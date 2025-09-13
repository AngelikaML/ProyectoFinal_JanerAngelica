package com.example.sispark

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val campoUsuario = findViewById<EditText>(R.id.campoUsuario)
        val campoPassword = findViewById<EditText>(R.id.campoPasword)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        // Usuario y contraseña de prueba
        val usuarioValido = "admin"
        val passwordValido = "1234"

        btnEntrar.setOnClickListener {
            val usuario = campoUsuario.text.toString().trim()
            val password = campoPassword.text.toString().trim()

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Debe ingresar usuario y contraseña", Toast.LENGTH_SHORT).show()
            } else if (usuario == usuarioValido && password == passwordValido) {

                Toast.makeText(this, "Inicio exitoso", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, PrincipalActivity::class.java) // tu pantalla principal
                startActivity(intent)
                finish()
            } else {

                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

    }
}