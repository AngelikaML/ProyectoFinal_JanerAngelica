package com.example.sispark

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializamos el helper para la base de datos
        dbHelper = DatabaseHelper(this)

        val campoUsuario = findViewById<EditText>(R.id.campoUsuario)
        val campoPassword = findViewById<EditText>(R.id.campoPasword)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        btnEntrar.setOnClickListener {
            val usuario = campoUsuario.text.toString().trim()
            val password = campoPassword.text.toString().trim()

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Debe ingresar usuario y contraseña", Toast.LENGTH_SHORT).show()
            } else if (validateUserLogin(usuario, password)) {
                // Si la validación es exitosa, redirige a la actividad principal
                Toast.makeText(this, "Inicio exitoso", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, PrincipalActivity::class.java) // tu pantalla principal
                startActivity(intent)
                finish()
            } else {
                // Si la validación falla
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para validar el usuario y la contraseña
    private fun validateUserLogin(username: String, password: String): Boolean {
        val db = dbHelper.readableDatabase // Obtiene la base de datos en modo lectura

        // Realiza una consulta a la base de datos buscando el usuario y la contraseña
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM USUARIOS WHERE usuario = ? AND contrasena = ?",
            arrayOf(username, password)
        )

        // Si la consulta devuelve al menos una fila, el login es válido
        val isValidUser = cursor.count > 0
        cursor.close() // Siempre cerrar el cursor después de usarlo
        return isValidUser
    }
}
