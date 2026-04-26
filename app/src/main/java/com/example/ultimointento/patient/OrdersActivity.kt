package com.example.ultimointento.patient

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ultimointento.LocalStorageManager
import com.example.ultimointento.R

class OrdersActivity : AppCompatActivity() {

    private lateinit var storage: LocalStorageManager
    private lateinit var rvOrders: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        storage = LocalStorageManager(this)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        rvOrders = findViewById(R.id.rvOrders)
        rvOrders.layoutManager = LinearLayoutManager(this)

        cargarPedidos()
    }

    private fun cargarPedidos() {
        val user = storage.getLoggedInUsuario()
        if (user == null) {
            finish()
            return
        }

        val pedidos = storage.getPedidos(user.id_usuario)
        val catalogo = storage.getMedicamentos()
        val domiciliarios = storage.getDomiciliarios()
        
        if (pedidos.isEmpty()) {
            findViewById<TextView>(R.id.tvEmptyState).visibility = android.view.View.VISIBLE
            rvOrders.visibility = android.view.View.GONE
        } else {
            findViewById<TextView>(R.id.tvEmptyState).visibility = android.view.View.GONE
            rvOrders.visibility = android.view.View.VISIBLE
            val adapter = OrdersAdapter(pedidos.sortedByDescending { it.id_pedido }, catalogo, domiciliarios) { pedidoToCancel ->
                // Actualizar estado del pedido
                pedidoToCancel.estado = "CANCELADO"
                storage.savePedido(pedidoToCancel)
                
                // Recargar lista
                cargarPedidos()
                android.widget.Toast.makeText(this, "Pedido cancelado", android.widget.Toast.LENGTH_SHORT).show()
            }
            rvOrders.adapter = adapter
        }
    }
}
