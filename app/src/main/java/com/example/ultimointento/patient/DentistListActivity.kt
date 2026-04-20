package com.example.ultimointento.patient

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ultimointento.LocalStorageManager
import com.example.ultimointento.R
import com.example.ultimointento.User

class DentistListActivity : AppCompatActivity() {
    private lateinit var storage: LocalStorageManager
    private lateinit var rvDentists: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dentist_list)

        storage = LocalStorageManager(this)
        rvDentists = findViewById(R.id.rvDentists)
        rvDentists.layoutManager = LinearLayoutManager(this)

        val dentists = storage.getDentists()
        rvDentists.adapter = DentistAdapter(dentists) { dentist ->
            val intent = Intent(this, BookAppointmentActivity::class.java)
            intent.putExtra("DENTIST_ID", dentist.id)
            intent.putExtra("DENTIST_NAME", dentist.name)
            intent.putExtra("DENTIST_SPECIALTY", dentist.specialty)
            startActivity(intent)
        }
    }
}

class DentistAdapter(private val dentists: List<User>, private val onBookClick: (User) -> Unit) :
    RecyclerView.Adapter<DentistAdapter.DentistViewHolder>() {

    class DentistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvDentistName)
        val tvSpecialty: TextView = view.findViewById(R.id.tvDentistSpecialty)
        val btnBook: Button = view.findViewById(R.id.btnBookAppointment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DentistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dentist, parent, false)
        return DentistViewHolder(view)
    }

    override fun onBindViewHolder(holder: DentistViewHolder, position: Int) {
        val dentist = dentists[position]
        holder.tvName.text = dentist.name
        holder.tvSpecialty.text = dentist.specialty
        holder.btnBook.setOnClickListener { onBookClick(dentist) }
    }

    override fun getItemCount() = dentists.size
}
