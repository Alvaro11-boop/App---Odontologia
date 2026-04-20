package com.example.ultimointento.patient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ultimointento.LocalStorageManager
import com.example.ultimointento.LoginActivity
import com.example.ultimointento.R
import com.google.android.material.card.MaterialCardView

class PatientDashboardActivity : AppCompatActivity() {

    private lateinit var storage: LocalStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        storage = LocalStorageManager(this)

        val user = storage.getLoggedInUser()
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        findViewById<TextView>(R.id.tvWelcome).text = "Bienvenido, ${user.name}"

        findViewById<MaterialCardView>(R.id.cardProfile).setOnClickListener {
            startActivity(Intent(this, PatientProfileActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.cardDentists).setOnClickListener {
            startActivity(Intent(this, DentistListActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.cardAppointments).setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            storage.saveLoggedInUser(null)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
