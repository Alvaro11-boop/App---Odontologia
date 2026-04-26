package com.example.ultimointento.patient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ultimointento.Medicamento
import com.example.ultimointento.R

class MedicamentoAdapter(
    private val medicamentos: List<Medicamento>,
    private val onAddToCart: (Medicamento) -> Unit
) : RecyclerView.Adapter<MedicamentoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreMedicamento)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcionMedicamento)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecioMedicamento)
        val btnAdd: Button = view.findViewById(R.id.btnAddCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicamento, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicamento = medicamentos[position]
        holder.tvNombre.text = medicamento.nombre
        holder.tvDescripcion.text = medicamento.descripcion
        holder.tvPrecio.text = "$ ${String.format(java.util.Locale("es", "CO"), "%,.0f", medicamento.precio)} COP"
        
        holder.btnAdd.setOnClickListener {
            onAddToCart(medicamento)
        }
    }

    override fun getItemCount() = medicamentos.size
}
