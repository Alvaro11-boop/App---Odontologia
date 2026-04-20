package com.example.ultimointento.patient

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ultimointento.LocalStorageManager
import com.example.ultimointento.R

class PatientProfileActivity : AppCompatActivity() {
    private lateinit var storage: LocalStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile)

        storage = LocalStorageManager(this)
        val user = storage.getLoggedInUser()

        if (user == null) {
            finish()
            return
        }

        val etEmail = findViewById<EditText>(R.id.etEmailProfile)
        val etName = findViewById<EditText>(R.id.etNameProfile)
        val etPhone = findViewById<EditText>(R.id.etPhoneProfile)
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)

        etEmail.setText(user.email)
        etName.setText(user.name)
        etPhone.setText(user.phone)

        btnSave.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newPhone = etPhone.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            user.name = newName
            user.phone = newPhone
            
            storage.saveUser(user)
            storage.saveLoggedInUser(user) // Update logged in instance too

            Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
