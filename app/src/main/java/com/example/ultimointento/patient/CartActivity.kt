package com.example.ultimointento.patient

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ultimointento.LocalStorageManager
import com.example.ultimointento.Pedido
import com.example.ultimointento.DetallePedido
import com.example.ultimointento.Domiciliario
import com.example.ultimointento.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Actividad que maneja el carrito de compras.
 * Permite visualizar los productos, modificar cantidades, eliminar productos,
 * seleccionar un domiciliario y confirmar el pedido final.
 */
class CartActivity : AppCompatActivity() {

    // Instancia del manejador de almacenamiento local (SharedPreferences)
    private lateinit var storage: LocalStorageManager
    
    // Variables de los componentes de la interfaz de usuario (UI)
    private lateinit var rvCartItems: RecyclerView
    private lateinit var tvTotalCart: TextView
    private lateinit var btnConfirmOrder: Button
    private lateinit var spinnerDomiciliario: Spinner
    
    // Lista para almacenar los domiciliarios disponibles y poder seleccionarlos
    private var domiciliariosList: List<Domiciliario> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Inicializar el manejador de base de datos local
        storage = LocalStorageManager(this)

        // Configuración de la barra superior (Toolbar) para el botón de regresar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Enlazar las variables con las vistas del layout XML
        rvCartItems = findViewById(R.id.rvCartItems)
        tvTotalCart = findViewById(R.id.tvTotalCart)
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder)
        spinnerDomiciliario = findViewById(R.id.spinnerDomiciliario)
        
        // Obtener la lista de domiciliarios desde el almacenamiento local
        domiciliariosList = storage.getDomiciliarios()
        
        // Configurar el adaptador para el Spinner (menú desplegable) extrayendo solo los nombres
        val adapterSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            domiciliariosList.map { it.nombre }
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDomiciliario.adapter = adapterSpinner

        // Configurar el RecyclerView para mostrar la lista en formato vertical
        rvCartItems.layoutManager = LinearLayoutManager(this)

        // Cargar los datos iniciales del carrito en la pantalla
        cargarCarrito()
        
        // Configurar la acción de confirmar el pedido al hacer clic en el botón
        btnConfirmOrder.setOnClickListener {
            procesarPedido()
        }
    }

    /**
     * Función encargada de leer el carrito actual del usuario desde el almacenamiento,
     * configurar el adaptador (CartAdapter) para mostrar la lista, y calcular el total.
     */
    private fun cargarCarrito() {
        val user = storage.getLoggedInUsuario()
        // Si no hay usuario logueado, se cierra la actividad por seguridad
        if (user == null) {
            finish()
            return
        }

        // Obtener el carrito del usuario y el catálogo de medicamentos para obtener precios
        val carrito = storage.getCarrito(user.id_usuario)
        val catalogo = storage.getMedicamentos()
        
        // Inicializar el adaptador de la lista pasando las callbacks necesarias
        val adapter = CartAdapter(
            carrito.detalles.toMutableList(), 
            catalogo, 
            onRemoveItem = { detalleToRemove ->
                // Acción para eliminar un producto del carrito
                val currentCarrito = storage.getCarrito(user.id_usuario)
                // Remover el medicamento específico
                currentCarrito.detalles.removeAll { it.id_medicamento == detalleToRemove.id_medicamento }
                
                // Guardar los cambios y recargar la vista
                storage.saveCarrito(currentCarrito)
                cargarCarrito()
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
            },
            onQuantityChange = { detalleToUpdate, newQuantity ->
                // Acción para cambiar la cantidad (+ o -) de un producto
                val currentCarrito = storage.getCarrito(user.id_usuario)
                val detail = currentCarrito.detalles.find { it.id_medicamento == detalleToUpdate.id_medicamento }
                
                // Si el producto existe en el carrito, se actualiza su cantidad y se recarga
                if (detail != null) {
                    detail.cantidad = newQuantity
                    storage.saveCarrito(currentCarrito)
                    cargarCarrito()
                }
            }
        )
        // Asignar el adaptador configurado a la lista (RecyclerView)
        rvCartItems.adapter = adapter

        // Calcular el valor total del carrito multiplicando cantidades por precios
        var total = 0.0f
        for (detalle in carrito.detalles) {
            val med = catalogo.find { it.id_medicamento == detalle.id_medicamento }
            if (med != null) {
                total += (med.precio * detalle.cantidad)
            }
        }
        
        // Mostrar el total formateado en formato de moneda COP
        tvTotalCart.text = "$ ${String.format(Locale("es", "CO"), "%,.0f", total)} COP"
        
        // Habilitar el botón de confirmar pedido solo si el carrito no está vacío
        btnConfirmOrder.isEnabled = carrito.detalles.isNotEmpty()
    }
    
    /**
     * Función que se ejecuta al confirmar un pedido.
     * Crea un nuevo registro de pedido, restaura el stock y vacía el carrito.
     */
    private fun procesarPedido() {
        val user = storage.getLoggedInUsuario()
        if (user == null) return
        
        val carrito = storage.getCarrito(user.id_usuario)
        // Verificación de seguridad adicional
        if (carrito.detalles.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }
        
        val catalogo = storage.getMedicamentos()
        var total = 0.0f
        val detallesPedido = mutableListOf<DetallePedido>()
        
        // Generar un ID único para el pedido basado en la fecha/hora actual
        val pedidoId = System.currentTimeMillis()
        
        // Recorrer el carrito para estructurar los detalles del nuevo pedido
        for (detalle in carrito.detalles) {
            val med = catalogo.find { it.id_medicamento == detalle.id_medicamento }
            if (med != null) {
                val subtotal = med.precio * detalle.cantidad
                total += subtotal
                
                // Agregar ítem al arreglo del pedido
                detallesPedido.add(
                    DetallePedido(
                        id_detalle = System.currentTimeMillis() + detalle.id_medicamento,
                        id_pedido = pedidoId,
                        id_medicamento = detalle.id_medicamento,
                        cantidad = detalle.cantidad,
                        subtotal = subtotal
                    )
                )
                
                // Reducir la cantidad comprada del inventario general (stock)
                med.stock -= detalle.cantidad
                storage.saveMedicamento(med)
            }
        }
        
        // Obtener y formatear la fecha actual
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = dateFormat.format(Date())
        
        // Obtener el domiciliario elegido en el menú desplegable (Spinner)
        val selectedPosition = spinnerDomiciliario.selectedItemPosition
        val domAsignado = if (selectedPosition != android.widget.AdapterView.INVALID_POSITION && domiciliariosList.isNotEmpty()) {
            domiciliariosList[selectedPosition] // Obtener el objeto Domiciliario de la lista original
        } else {
            null
        }

        // Crear el objeto del nuevo pedido con toda su información
        val pedido = Pedido(
            id_pedido = pedidoId,
            fecha = fecha,
            total = total,
            estado = "PENDIENTE",
            id_usuario = user.id_usuario,
            id_domicilio = 1L, // TODO: Seleccionar domicilio real del usuario más adelante
            detalles = detallesPedido,
            id_domiciliario = domAsignado?.id_domiciliario // Asignar el ID del repartidor seleccionado
        )
        
        // Guardar el pedido finalizado en la memoria
        storage.savePedido(pedido)
        
        // Vaciar el carrito de compras una vez el pedido es exitoso
        storage.clearCarrito(user.id_usuario)
        
        Toast.makeText(this, "Pedido confirmado con éxito", Toast.LENGTH_LONG).show()
        
        // Cerrar la pantalla del carrito y regresar a la anterior
        finish()
    }
}
