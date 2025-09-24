package com.example.sispark

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SISPARK_DB.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Si no usas un .db preexistente, aquí deberías crear tablas
        // db?.execSQL("CREATE TABLE ejemplo (...)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Si cambias de versión, aquí manejas la migración
    }

    /**
     * Copia la base de datos desde assets a la ruta interna de Android
     */
    private fun copyDatabase() {
        try {
            val dbPath = context.getDatabasePath(DATABASE_NAME).absolutePath
            val dbFile = File(dbPath)

            // Crear carpeta si no existe
            dbFile.parentFile?.mkdirs()

            val inputStream: InputStream = context.assets.open(DATABASE_NAME)
            val outputStream: OutputStream = FileOutputStream(dbPath)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            Log.d("DatabaseHelper", "Base de datos copiada en: $dbPath")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("DatabaseHelper", "Error copiando la base de datos: ${e.message}")
        }
    }

    /**
     * Abre la base de datos existente, si no existe la copia desde assets
     */
    fun openDatabase(): SQLiteDatabase {
        val dbPath = context.getDatabasePath(DATABASE_NAME).absolutePath
        val dbFile = File(dbPath)

        if (!dbFile.exists()) {
            copyDatabase()
        }

        return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    fun getReadableDatabaseInstance(): SQLiteDatabase {
        return readableDatabase
    }

    fun getWritableDatabaseInstance(): SQLiteDatabase {
        return writableDatabase
    }
}
