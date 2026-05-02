package com.example.ultimointento.patient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ultimointento.DetalleCarrito
import com.example.ultimointento.Medicamento
import com.example.ultimointento.R
import java.util.Locale

/**
 * Adaptador para mostrar los elementos del carrito de compras en un RecyclerView.
 * Recibe la lista de detalles del carrito, el catálogo de medicamentos,
 * y funciones (callbacks) para eliminar ítems o cambiar su cantidad.
 */
class CartAdapter(
    private val detalles: MutableList<DetalleCarrito>,
    private val catalogo: List<Medicamento>,
    private val onRemoveItem: (DetalleCarrito) -> Unit,
    private val onQuantityChange: (DetalleCarrito, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    /**
     * ViewHolder que mantiene las referencias a las vistas (UI) de cada ítem del carrito.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCartItemName)
        val tvPrice: TextView = view.findViewById(R.id.tvCartItemPrice)
        val tvQuantity: TextView = view.findViewById(R.id.tvCartItemQuantity)
        val btnMinusQuantity: android.widget.Button = view.findViewById(R.id.btnMinusQuantity)
        val btnPlusQuantity: android.widget.Button = view.findViewById(R.id.btnPlusQuantity)
        val tvSubtotal: TextView = view.findViewById(R.id.tvCartItemSubtotal)
        val btnRemove: android.widget.ImageButton = view.findViewById(R.id.btnRemoveCartItem)
    }

    /**
     * Crea la vista individual (infla el layout item_cart.xml) para cada elemento de la lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    /**
     * Vincula los datos del elemento actual con las vistas (TextViews, Botones) correspondientes.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detalle = detalles[position]
        // Buscar el medicamento correspondiente en el catálogo
        val medicamento = catalogo.find { it.id_medicamento == detalle.id_medicamento }
        
        if (medicamento != null) {
            // Mostrar nombre y precio unitario formateado a pesos colombianos
            holder.tvName.text = medicamento.nombre
            holder.tvPrice.text = "$ ${String.format(Locale("es", "CO"), "%,.0f", medicamento.precio)} c/u"
            
            // Mostrar cantidad actual del producto
            holder.tvQuantity.text = "${detalle.cantidad}"
            
            // Calcular y mostrar el subtotal (precio x cantidad)
            val subtotal = medicamento.precio * detalle.cantidad
            holder.tvSubtotal.text = "$ ${String.format(Locale("es", "CO"), "%,.0f", subtotal)} COP"
        }

        // Lógica del botón para disminuir la cantidad
        holder.btnMinusQuantity.setOnClickListener {
            // Solo permite disminuir si la cantidad actual es mayor a 1
            if (detalle.cantidad > 1) {
                onQuantityChange(detalle, detalle.cantidad - 1)
            }
        }
        
        // Lógica del botón para aumentar la cantidad
        holder.btnPlusQuantity.setOnClickListener {
            onQuantityChange(detalle, detalle.cantidad + 1)
        }

        // Lógica del botón para eliminar el producto del carrito
        holder.btnRemove.setOnClickListener {
            onRemoveItem(detalle)
        }
    }

    /**
     * Retorna la cantidad total de elementos en el carrito.
     */
    override fun getItemCount() = detalles.size
}
