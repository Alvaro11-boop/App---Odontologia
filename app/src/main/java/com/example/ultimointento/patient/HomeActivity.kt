package com.example.ultimointento.patient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ultimointento.LocalStorageManager
import com.example.ultimointento.LoginActivity
import com.example.ultimointento.DetalleCarrito
import com.example.ultimointento.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var storage: LocalStorageManager
    private lateinit var rvMedicamentos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        storage = LocalStorageManager(this)

        val user = storage.getLoggedInUsuario()
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        findViewById<TextView>(R.id.tvWelcome).text = "Hola, ${user.nombre}"

        rvMedicamentos = findViewById(R.id.rvMedicamentos)
        rvMedicamentos.layoutManager = LinearLayoutManager(this)
        
        cargarCatalogo()

        findViewById<FloatingActionButton>(R.id.fabCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<Button>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<Button>(R.id.btnDomiciliarios).setOnClickListener {
            startActivity(Intent(this, DomiciliariosActivity::class.java))
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            storage.saveLoggedInUsuario(null)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    private fun cargarCatalogo() {
        var medicamentos = storage.getMedicamentos()
        
        // Autocorrección si hay precios viejos (ej. menores a 100 COP)
        if (medicamentos.isNotEmpty() && medicamentos[0].precio < 100) {
            storage.resetMedicamentos()
            medicamentos = storage.getMedicamentos()
        }
        
        val adapter = MedicamentoAdapter(medicamentos) { medicamento ->
            val user = storage.getLoggedInUsuario()
            if (user != null) {
                val carrito = storage.getCarrito(user.id_usuario)
                
                val existingDetail = carrito.detalles.find { it.id_medicamento == medicamento.id_medicamento }
                if (existingDetail != null) {
                    existingDetail.cantidad += 1
                } else {
                    carrito.detalles.add(DetalleCarrito(System.currentTimeMillis(), carrito.id_carrito, medicamento.id_medicamento, 1))
                }
                
                storage.saveCarrito(carrito)
                Toast.makeText(this, "${medicamento.nombre} añadido al carrito", Toast.LENGTH_SHORT).show()
            }
        }
        rvMedicamentos.adapter = adapter
    }
}
