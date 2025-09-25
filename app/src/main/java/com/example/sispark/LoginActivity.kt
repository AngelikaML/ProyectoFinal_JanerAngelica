package com.example.sispark

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializamos el helper de la base de datos
        dbHelper = DatabaseHelper(this)

        val campoUsuario = findViewById<EditText>(R.id.campoUsuario)
        val campoPassword = findViewById<EditText>(R.id.campoPasword)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val linkReg = findViewById<TextView>(R.id.linkRegistrar)

        btnEntrar.setOnClickListener {
            val usuario = campoUsuario.text.toString().trim()
            val password = campoPassword.text.toString().trim()

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Debe ingresar usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
            else if (validateUserLogin(usuario, password)) {
                Toast.makeText(this, "Bienvenido ${getUsuarioDeSesion()}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, PrincipalActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        linkReg.setOnClickListener {
            val intent = Intent(this, UsuarioActivity::class.java)
            startActivity(intent)
        };
    }

    private fun validateUserLogin(username: String, password: String): Boolean {
        var isValidUser = false
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null

        try {
            db = dbHelper.openDatabase()
            cursor = db.rawQuery(
                "SELECT IdUsuario, Nombre, Apellido, usuario FROM USUARIOS WHERE usuario = ? AND contrasena = ? AND IdEstado = 1",
                arrayOf(username, password)
            )

            if (cursor.moveToFirst()) {
                val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("IdUsuario"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("Apellido"))
                val usuario = cursor.getString(cursor.getColumnIndexOrThrow("usuario"))

                // Guardamos en la sesión
                UserSession.idUsuario = idUsuario
                UserSession.nombre = nombre
                UserSession.apellido = apellido
                UserSession.usuario = usuario
                guardarUsuarioEnSesion(UserSession.idUsuario, UserSession.nombre!!, UserSession.apellido!!,
                    UserSession.usuario!!)

                isValidUser = true
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        finally {
            cursor?.close()
            db?.close()
        }

        return isValidUser
    }

    private fun guardarUsuarioEnSesion(idUsuario: Int, nombre: String, apellido: String, usuario: String) {
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt("IdUsuario", idUsuario)
        editor.putString("Nombre", nombre)
        editor.putString("Apellido", apellido)
        editor.putString("usuario", usuario)
        editor.apply()
    }

    private fun getUsuarioDeSesion(): String {
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val nombre = prefs.getString("Nombre", "")
        val apellido = prefs.getString("Apellido", "")
        return "$nombre $apellido".trim()
    }


}
