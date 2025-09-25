package com.example.sispark

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Toast

class UsuarioActivity : AppCompatActivity() {
    private lateinit var nombre: EditText
    private lateinit var apellido: EditText
    private lateinit var usuario: EditText
    private lateinit var contraseña: EditText
    private lateinit var estado: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnNuevo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        nombre = findViewById(R.id.nombre)
        apellido = findViewById(R.id.apellido)
        usuario = findViewById(R.id.usuario)
        contraseña = findViewById(R.id.contraseña)
        estado = findViewById(R.id.estado)

        btnGuardar = findViewById(R.id.guardar)
        btnActualizar = findViewById(R.id.actualizar)
        btnNuevo = findViewById(R.id.nuevo)

        btnGuardar.setOnClickListener {
            val mensaje = """
                Guardado:
                Nombre: ${nombre.text}
                Apellido: ${apellido.text}
                Usuario: ${usuario.text}
                Contraseña: ${contraseña.text}
                Estado: ${estado.text}
            """.trimIndent()

            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }

        btnActualizar.setOnClickListener {
            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
        }

        btnNuevo.setOnClickListener {
            nombre.text.clear()
            apellido.text.clear()
            usuario.text.clear()
            contraseña.text.clear()
            estado.text.clear()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}