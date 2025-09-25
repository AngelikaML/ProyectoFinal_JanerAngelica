package com.example.sispark

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvEmail = findViewById<TextView>(R.id.tvIdentificacion)
        val tvPhone = findViewById<TextView>(R.id.tvPhone)
        val tvUsuario = findViewById<TextView>(R.id.tvUsuario)

        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val nombre = prefs.getString("Nombre", "")
        val apellido = prefs.getString("Apellido", "")
        val usuario = prefs.getString("usuario", "")

        tvName.text = "${ nombre } ${ apellido }"
        tvUsuario.text = "Tu usuario es: ${ usuario }"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}