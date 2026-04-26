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

class CartAdapter(
    private val detalles: MutableList<DetalleCarrito>,
    private val catalogo: List<Medicamento>,
    private val onRemoveItem: (DetalleCarrito) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCartItemName)
        val tvPrice: TextView = view.findViewById(R.id.tvCartItemPrice)
        val tvQuantity: TextView = view.findViewById(R.id.tvCartItemQuantity)
        val tvSubtotal: TextView = view.findViewById(R.id.tvCartItemSubtotal)
        val btnRemove: android.widget.ImageButton = view.findViewById(R.id.btnRemoveCartItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detalle = detalles[position]
        val medicamento = catalogo.find { it.id_medicamento == detalle.id_medicamento }
        
        if (medicamento != null) {
            holder.tvName.text = medicamento.nombre
            // Formateo COP
            holder.tvPrice.text = "$ ${String.format(Locale("es", "CO"), "%,.0f", medicamento.precio)} c/u"
            holder.tvQuantity.text = "x${detalle.cantidad}"
            
            val subtotal = medicamento.precio * detalle.cantidad
            holder.tvSubtotal.text = "$ ${String.format(Locale("es", "CO"), "%,.0f", subtotal)} COP"
        }

        holder.btnRemove.setOnClickListener {
            onRemoveItem(detalle)
        }
    }

    override fun getItemCount() = detalles.size
}
