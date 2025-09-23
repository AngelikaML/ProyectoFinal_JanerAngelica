package com.example.sispark

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val context: Context = context

    companion object {
        private const val DATABASE_NAME = "SISPARK_DB.db"
        private const val DATABASE_VERSION = 1
        private const val DB_PATH = "/data/data/com.example.sispark/databases/"
    }

    // Si la base de datos no existe, se crea una nueva
    override fun onCreate(db: SQLiteDatabase?) {
        // Aquí puedes escribir la lógica para crear las tablas si es necesario
    }

    // Método para actualizar la base de datos si se cambia de versión
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Aquí puedes escribir la lógica para actualizar la base de datos
    }

    // Copia la base de datos desde los assets al almacenamiento interno
    fun copyDatabase() {
        val inputStream: InputStream = context.assets.open(DATABASE_NAME)
        val outputStream: OutputStream = FileOutputStream(DB_PATH + DATABASE_NAME)

        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }

    // Abre la base de datos si no existe, copia la base de datos desde assets
    fun openDatabase(): SQLiteDatabase {
        val dbFile = File(DB_PATH + DATABASE_NAME)
        if (!dbFile.exists()) {
            copyDatabase()  // Si la base de datos no existe, la copiamos
        }
        return SQLiteDatabase.openDatabase(dbFile.absolutePath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    // Método para obtener la base de datos en modo lectura
    fun getReadableDatabaseInstance(): SQLiteDatabase {
        return readableDatabase
    }

    // Método para obtener la base de datos en modo escritura
    fun getWritableDatabaseInstance(): SQLiteDatabase {
        return writableDatabase
    }
}
