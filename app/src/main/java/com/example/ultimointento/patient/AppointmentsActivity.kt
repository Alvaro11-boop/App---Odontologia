package com.example.ultimointento.patient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ultimointento.Appointment
import com.example.ultimointento.LocalStorageManager
import com.example.ultimointento.R

class AppointmentsActivity : AppCompatActivity() {
    private lateinit var storage: LocalStorageManager
    private lateinit var rvAppointments: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        storage = LocalStorageManager(this)
        rvAppointments = findViewById(R.id.rvAppointments)
        rvAppointments.layoutManager = LinearLayoutManager(this)

        loadAppointments()
    }

    private fun loadAppointments() {
        val user = storage.getLoggedInUser() ?: return
        val appointments = storage.getAppointmentsForPatient(user.id).sortedByDescending { it.date }
        
        rvAppointments.adapter = AppointmentAdapter(appointments) { appt ->
            appt.status = "CANCELLED"
            storage.saveAppointment(appt)
            loadAppointments() // Refresh list
        }
    }
}

class AppointmentAdapter(private val appointments: List<Appointment>, private val onCancelClick: (Appointment) -> Unit) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDentistName: TextView = view.findViewById(R.id.tvApptDentistName)
        val tvDate: TextView = view.findViewById(R.id.tvApptDate)
        val tvTime: TextView = view.findViewById(R.id.tvApptTime)
        val tvStatus: TextView = view.findViewById(R.id.tvApptStatus)
        val btnCancel: Button = view.findViewById(R.id.btnCancelAppointment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appt = appointments[position]
        holder.tvDentistName.text = "Con: ${appt.dentistName}"
        holder.tvDate.text = "Fecha: ${appt.date}"
        holder.tvTime.text = "Hora: ${appt.time}"
        holder.tvStatus.text = if (appt.status == "CONFIRMED") "CONFIRMADA" else "CANCELADA"
        
        if (appt.status == "CANCELLED") {
            holder.tvStatus.setTextColor(android.graphics.Color.RED)
            holder.btnCancel.visibility = View.GONE
        } else {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            holder.btnCancel.visibility = View.VISIBLE
            holder.btnCancel.setOnClickListener { onCancelClick(appt) }
        }
    }

    override fun getItemCount() = appointments.size
}
