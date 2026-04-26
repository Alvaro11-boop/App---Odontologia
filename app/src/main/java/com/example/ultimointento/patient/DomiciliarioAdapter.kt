package com.example.ultimointento.patient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ultimointento.Domiciliario
import com.example.ultimointento.R

class DomiciliarioAdapter(
    private val domiciliarios: List<Domiciliario>
) : RecyclerView.Adapter<DomiciliarioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvDomNombre)
        val tvMoto: TextView = view.findViewById(R.id.tvDomMoto)
        val tvPlaca: TextView = view.findViewById(R.id.tvDomPlaca)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_domiciliario, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dom = domiciliarios[position]
        holder.tvNombre.text = dom.nombre
        holder.tvMoto.text = "Moto: ${dom.modelo_moto}"
        holder.tvPlaca.text = "Placa: ${dom.placa_moto}"
    }

    override fun getItemCount() = domiciliarios.size
}
