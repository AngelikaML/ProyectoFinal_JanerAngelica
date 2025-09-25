package com.example.sispark

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import com.example.sispark.databinding.ActivityPrincipalBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
class PrincipalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrincipalBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarPrincipal.toolbar)

        dbHelper = DatabaseHelper(this)
        llenarTabla()

        binding.appBarPrincipal.contentPrincipal.btnValidar.setOnClickListener {
            val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
            val IdUsuario = prefs.getInt("IdUsuario", 1)
            val placa = binding.appBarPrincipal.contentPrincipal.campoPlaca.text.toString()
            val tipoIngreso = obtenerTipoPago(placa)
            val fechaIngreso = validarPlaca(placa)

            if (fechaIngreso != null) {
                //Toast.makeText(this, "Placa ya registrada", Toast.LENGTH_SHORT).show()
                val exito = actualizarSalidaVehiculo(placa,fechaIngreso,IdUsuario)

                if (exito) {
                    Toast.makeText(this, "Vehículo registrado para salida", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar el vehículo", Toast.LENGTH_SHORT).show()
                }
            } else {
                //Toast.makeText(this, "Placa disponible", Toast.LENGTH_SHORT).show()
                val exito = insertarVehiculo(tipoIngreso,placa, IdUsuario)

                if (exito) {
                    Toast.makeText(this, "Vehículo registrado en entrada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al registrar vehículo", Toast.LENGTH_SHORT).show()
                }
            }

            llenarTabla()
        }

        val toolbar: Toolbar = binding.appBarPrincipal.toolbar
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        navView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.nav_mensual -> {
                    val intent = Intent(this, VehiculosMensualidadActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                R.id.nav_horas -> {
                    val intent = Intent(this, PrincipalActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                R.id.nav_usuario -> {
                    val intent = Intent(this, UsuarioActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                R.id.nav_salir -> {
                    salirApp()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun validarPlaca(valorPlaca: String): String? {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var fechaIngreso: String? = null

        try {
            db = dbHelper.openDatabase()
            cursor = db.rawQuery(
                """
            SELECT FechaIngreso 
            FROM REGISTRO_INGRESOS 
            WHERE Placa = ? 
            ORDER BY FechaIngreso DESC 
            LIMIT 1
            """.trimIndent(),
                arrayOf(valorPlaca)
            )

            if (cursor.moveToFirst()) {
                fechaIngreso = cursor.getString(cursor.getColumnIndexOrThrow("FechaIngreso"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db?.close()
        }

        return fechaIngreso
    }

    private fun insertarVehiculo(idTipoIngreso: Int, placa: String, idUsuario: Int): Boolean {
        var db: SQLiteDatabase? = null
        return try {
            db = dbHelper.openDatabase()

            val fechaIngreso = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())

            val sql = """INSERT INTO REGISTRO_INGRESOS 
            (IdTipoIngreso, Placa, FechaIngreso, FechaSalida, ValorPagar, IdUsuario) 
            VALUES (?, ?, ?, ?, ?, ?)""".trimIndent()

            val stmt = db.compileStatement(sql)
            stmt.bindLong(1, idTipoIngreso.toLong())
            stmt.bindString(2, placa)
            stmt.bindString(3, fechaIngreso)
            stmt.bindString(4, "")
            stmt.bindString(5, "")
            stmt.bindLong(6, idUsuario.toLong())

            stmt.executeInsert()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db?.close()
        }
    }

    private fun actualizarSalidaVehiculo(placa: String, fechaIng: String, idUsuario: Int): Boolean {
        var db: SQLiteDatabase? = null
        return try {
            db = dbHelper.openDatabase()

            val fechaSalida = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())
            val fechaIngreso =fechaIng

            val valor = calcularValorPagar(fechaIngreso, fechaSalida)
            println("Total a pagar: $valor") // 👉 8000 (2 horas)

            val sql = """UPDATE REGISTRO_INGRESOS 
                SET FechaSalida = ?,
                    ValorPagar = ?
            WHERE IdUsuario = ? and Placa = ?""".trimIndent()

            val stmt = db.compileStatement(sql)
            stmt.bindString(1, fechaSalida)
            stmt.bindDouble(2, valor.toDouble())
            stmt.bindLong(3, idUsuario.toLong())
            stmt.bindString(4, placa)

            stmt.executeUpdateDelete()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db?.close()
        }
    }

    private fun llenarTabla() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT IdRegistro, Placa, FechaIngreso, FechaSalida FROM REGISTRO_INGRESOS",
            null
        )

        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)
        tableLayout.removeAllViews()

        // -------- ENCABEZADO --------
        val headerRow = TableRow(this)
        val headers = arrayOf("ID", "PLACA", "FECHA ENTRADA", "FECHA SALIDA")

        for (header in headers) {
            val tv = TextView(this)
            tv.text = header
            tv.setPadding(20, 20, 20, 20)
            tv.setTextColor(Color.WHITE)
            tv.setTypeface(null, Typeface.BOLD)
            tv.textSize = 14f
            tv.gravity = Gravity.CENTER
            tv.setBackgroundColor(Color.parseColor("#4472C4")) // azul
            headerRow.addView(tv)
        }
        tableLayout.addView(headerRow)

        // -------- FILAS CON DATOS --------
        if (cursor.moveToFirst()) {
            do {
                val fila = TableRow(this)

                val id = cursor.getInt(0).toString()
                val placa = cursor.getString(1)
                val fechaIngreso = cursor.getString(2)
                val fechaSalida = cursor.getString(3) ?: "--"

                val columnas = arrayOf(id, placa, fechaIngreso, fechaSalida)

                for (col in columnas) {
                    val tv = TextView(this)
                    tv.text = col
                    tv.setPadding(20, 20, 20, 20)
                    tv.textSize = 13f
                    tv.gravity = Gravity.CENTER
                    tv.setBackgroundResource(R.drawable.celda_borde) // borde + fondo
                    fila.addView(tv)
                }

                tableLayout.addView(fila)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
    }

    fun obtenerTipoPago(placa: String): Int {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        var tipoPago = 1 // valor por defecto (pago por horas)

        try {
            db = dbHelper.openDatabase()
            cursor = db.rawQuery(
                """SELECT 
                CASE 
                    WHEN EXISTS (SELECT 1 FROM Vehiculos_Mensualidad WHERE Placa = ?) 
                    THEN 2 
                    ELSE 1 
                END AS TipoPago""".trimIndent(),
                arrayOf(placa)
            )

            if (cursor.moveToFirst()) {
                tipoPago = cursor.getInt(cursor.getColumnIndexOrThrow("TipoPago"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db?.close()
        }

        return tipoPago
    }

    private fun salirApp() {
        try {
            val db = dbHelper.openDatabase()
            if (db.isOpen) {
                db.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        finishAffinity() // Cierra todas las actividades y sale de la app
    }

    fun calcularValorPagar(fechaIngreso: String, fechaSalida: String): Int {
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val valorHora = 4000
        var totalPagar = 0

        try {
            val ingreso = formato.parse(fechaIngreso)
            val salida = formato.parse(fechaSalida)

            if (ingreso != null && salida != null) {
                val diffMillis = salida.time - ingreso.time
                val diffMinutos = TimeUnit.MILLISECONDS.toMinutes(diffMillis)

                if (diffMinutos <= 15) {
                    totalPagar = 0
                } else {
                    // Convertir minutos a horas, redondeando hacia arriba
                    val horas = ceil(diffMinutos / 60.0).toInt()
                    totalPagar = horas * valorHora
                }
            }

            val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            binding.appBarPrincipal.contentPrincipal.txtValorPagar.text =
                "Valor a pagar: ${formatoMoneda.format(totalPagar)}"

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return totalPagar
    }

}

