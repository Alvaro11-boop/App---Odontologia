package com.example.ultimointento.patient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ultimointento.Domicilio
import com.example.ultimointento.LocalStorageManager
import com.example.ultimointento.R

class ProfileActivity : AppCompatActivity() {
    private lateinit var storage: LocalStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        storage = LocalStorageManager(this)
        val user = storage.getLoggedInUsuario()

        if (user == null) {
            finish()
            return
        }

        val etEmail = findViewById<EditText>(R.id.etEmailProfile)
        val etName = findViewById<EditText>(R.id.etNameProfile)
        val etPhone = findViewById<EditText>(R.id.etPhoneProfile)
        
        val etDireccion = findViewById<EditText>(R.id.etDireccion)
        val etCiudad = findViewById<EditText>(R.id.etCiudad)
        val etReferencia = findViewById<EditText>(R.id.etReferencia)
        
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)
        val btnViewOrders = findViewById<Button>(R.id.btnViewOrders)

        // Cargar datos de usuario
        etEmail.setText(user.correo)
        etName.setText(user.nombre)
        etPhone.setText(user.telefono)
        
        // Cargar domicilio (por ahora asumiremos el ID 1 o crearemos uno si no existe)
        // En una app real, el usuario tendría una lista de domicilios. Aquí simplificaremos a uno.
        val domicilios = storage.getDomicilios(user.id_usuario)
        var miDomicilio = domicilios.firstOrNull()
        
        if (miDomicilio != null) {
            etDireccion.setText(miDomicilio.direccion)
            etCiudad.setText(miDomicilio.ciudad)
            etReferencia.setText(miDomicilio.referencia)
        }

        btnSave.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newPhone = etPhone.text.toString().trim()
            val newDir = etDireccion.text.toString().trim()
            val newCiudad = etCiudad.text.toString().trim()
            val newRef = etReferencia.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            user.nombre = newName
            user.telefono = newPhone
            storage.saveUsuario(user)
            storage.saveLoggedInUsuario(user)
            
            if (newDir.isNotEmpty() && newCiudad.isNotEmpty()) {
                if (miDomicilio == null) {
                    miDomicilio = Domicilio(System.currentTimeMillis(), newDir, newCiudad, newRef, user.id_usuario)
                } else {
                    miDomicilio!!.direccion = newDir
                    miDomicilio!!.ciudad = newCiudad
                    miDomicilio!!.referencia = newRef
                }
                storage.saveDomicilio(miDomicilio!!)
            }

            Toast.makeText(this, "Perfil y Domicilio actualizados", Toast.LENGTH_SHORT).show()
            finish()
        }
        
        btnViewOrders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }
    }
}
