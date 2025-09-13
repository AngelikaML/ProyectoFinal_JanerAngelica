package com.example.sispark

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import com.example.sispark.databinding.ActivityPrincipalBinding

class PrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarPrincipal.toolbar)

        val valor = "ABC123"

        binding.appBarPrincipal.contentPrincipal.btnValidar.setOnClickListener {
            val placa = binding.appBarPrincipal.contentPrincipal.campoPlaca.text.toString()
            if (placa==valor){
                Toast.makeText(this, "El vehiculo con la placa: "+placa+", ingreso exitosamente" ,Toast.LENGTH_SHORT).show()
            }

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

            }
            drawerLayout.closeDrawers()
            true
        }

    }
}
