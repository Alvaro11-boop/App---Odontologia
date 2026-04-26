package com.example.ultimointento.patient

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ultimointento.LocalStorageManager
import com.example.ultimointento.Pedido
import com.example.ultimointento.DetallePedido
import com.example.ultimointento.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var storage: LocalStorageManager
    private lateinit var rvCartItems: RecyclerView
    private lateinit var tvTotalCart: TextView
    private lateinit var btnConfirmOrder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        storage = LocalStorageManager(this)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        rvCartItems = findViewById(R.id.rvCartItems)
        tvTotalCart = findViewById(R.id.tvTotalCart)
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder)

        rvCartItems.layoutManager = LinearLayoutManager(this)

        cargarCarrito()
        
        btnConfirmOrder.setOnClickListener {
            procesarPedido()
        }
    }

    private fun cargarCarrito() {
        val user = storage.getLoggedInUsuario()
        if (user == null) {
            finish()
            return
        }

        val carrito = storage.getCarrito(user.id_usuario)
        val catalogo = storage.getMedicamentos()
        
        val adapter = CartAdapter(carrito.detalles.toMutableList(), catalogo) { detalleToRemove ->
            // Eliminar del carrito en memoria
            val currentCarrito = storage.getCarrito(user.id_usuario)
            currentCarrito.detalles.removeAll { it.id_medicamento == detalleToRemove.id_medicamento }
            
            // Guardar en almacenamiento
            storage.saveCarrito(currentCarrito)
            
            // Recargar vista
            cargarCarrito()
            Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
        }
        rvCartItems.adapter = adapter

        // Calcular total
        var total = 0.0f
        for (detalle in carrito.detalles) {
            val med = catalogo.find { it.id_medicamento == detalle.id_medicamento }
            if (med != null) {
                total += (med.precio * detalle.cantidad)
            }
        }
        // Formateo COP
        tvTotalCart.text = "$ ${String.format(Locale("es", "CO"), "%,.0f", total)} COP"
        
        btnConfirmOrder.isEnabled = carrito.detalles.isNotEmpty()
    }
    
    private fun procesarPedido() {
        val user = storage.getLoggedInUsuario()
        if (user == null) return
        
        val carrito = storage.getCarrito(user.id_usuario)
        if (carrito.detalles.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }
        
        val catalogo = storage.getMedicamentos()
        var total = 0.0f
        val detallesPedido = mutableListOf<DetallePedido>()
        
        val pedidoId = System.currentTimeMillis()
        
        for (detalle in carrito.detalles) {
            val med = catalogo.find { it.id_medicamento == detalle.id_medicamento }
            if (med != null) {
                val subtotal = med.precio * detalle.cantidad
                total += subtotal
                
                detallesPedido.add(
                    DetallePedido(
                        id_detalle = System.currentTimeMillis() + detalle.id_medicamento,
                        id_pedido = pedidoId,
                        id_medicamento = detalle.id_medicamento,
                        cantidad = detalle.cantidad,
                        subtotal = subtotal
                    )
                )
                
                // Reducir stock
                med.stock -= detalle.cantidad
                storage.saveMedicamento(med)
            }
        }
        
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = dateFormat.format(Date())
        
        val domiciliarios = storage.getDomiciliarios()
        val domAsignado = if (domiciliarios.isNotEmpty()) domiciliarios.random() else null

        val pedido = Pedido(
            id_pedido = pedidoId,
            fecha = fecha,
            total = total,
            estado = "PENDIENTE",
            id_usuario = user.id_usuario,
            id_domicilio = 1L, // TODO: Seleccionar domicilio real
            detalles = detallesPedido,
            id_domiciliario = domAsignado?.id_domiciliario
        )
        
        storage.savePedido(pedido)
        storage.clearCarrito(user.id_usuario)
        
        Toast.makeText(this, "Pedido confirmado con éxito", Toast.LENGTH_LONG).show()
        finish()
    }
}
