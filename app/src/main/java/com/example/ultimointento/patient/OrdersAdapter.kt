package com.example.ultimointento.patient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ultimointento.Medicamento
import com.example.ultimointento.Pedido
import com.example.ultimointento.R

class OrdersAdapter(
    private val pedidos: List<Pedido>,
    private val catalogo: List<Medicamento>,
    private val domiciliarios: List<com.example.ultimointento.Domiciliario>,
    private val onCancelOrder: (Pedido) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val tvOrderTotal: TextView = view.findViewById(R.id.tvOrderTotal)
        val tvOrderItems: TextView = view.findViewById(R.id.tvOrderItems)
        val tvDomiciliario: TextView = view.findViewById(R.id.tvDomiciliarioInfo)
        val btnCancel: android.widget.Button = view.findViewById(R.id.btnCancelOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pedido = pedidos[position]
        
        holder.tvOrderDate.text = "Pedido del ${pedido.fecha}"
        holder.tvOrderStatus.text = pedido.estado
        
        // Colores por estado
        when (pedido.estado) {
            "PENDIENTE" -> {
                holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#00BFA5"))
                holder.btnCancel.visibility = View.VISIBLE
            }
            "CANCELADO" -> {
                holder.tvOrderStatus.setTextColor(android.graphics.Color.RED)
                holder.btnCancel.visibility = View.GONE
            }
            else -> {
                holder.tvOrderStatus.setTextColor(android.graphics.Color.GRAY)
                holder.btnCancel.visibility = View.GONE
            }
        }
        
        // Formateo COP
        holder.tvOrderTotal.text = "Total: $ ${String.format(java.util.Locale("es", "CO"), "%,.0f", pedido.total)} COP"
        
        var itemsText = ""
        for (detalle in pedido.detalles) {
            val med = catalogo.find { it.id_medicamento == detalle.id_medicamento }
            if (med != null) {
                itemsText += "${detalle.cantidad}x ${med.nombre}\n"
            }
        }
        holder.tvOrderItems.text = itemsText.trim()

        // Mostrar Domiciliario si tiene uno asignado
        val dom = domiciliarios.find { it.id_domiciliario == pedido.id_domiciliario }
        if (dom != null) {
            holder.tvDomiciliario.visibility = View.VISIBLE
            holder.tvDomiciliario.text = "Repartidor: ${dom.nombre}\nMoto: ${dom.modelo_moto} (${dom.placa_moto})"
        } else {
            holder.tvDomiciliario.visibility = View.GONE
        }
        
        holder.btnCancel.setOnClickListener {
            onCancelOrder(pedido)
        }
    }

    override fun getItemCount() = pedidos.size
}
