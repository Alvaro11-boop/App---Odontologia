package com.example.ultimointento

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvError: TextView
    private lateinit var btnRegister: Button

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(UserRepository(LocalStorageManager(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        tvError = findViewById(R.id.tvError)
        btnRegister = findViewById(R.id.btnRegister)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        configurarBotonRegistro()
        observarViewModel()
    }

    private fun configurarBotonRegistro() {
        btnRegister.setOnClickListener {
            val name = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val pass = etPassword.text.toString()

            // Validaciones detalladas
            if (name.isEmpty()) {
                mostrarError("Por favor ingresa tu nombre completo.")
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                mostrarError("Por favor ingresa tu correo electrónico.")
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mostrarError("El correo electrónico no tiene un formato válido.")
                return@setOnClickListener
            }
            if (pass.isEmpty()) {
                mostrarError("Por favor ingresa una contraseña.")
                return@setOnClickListener
            }
            if (pass.length < 6) {
                mostrarError("La contraseña debe tener al menos 6 caracteres.")
                return@setOnClickListener
            }

            tvError.visibility = View.GONE
            viewModel.registerUsuario(email, pass, phone, name)
        }
    }

    private fun observarViewModel() {
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is AuthViewModel.UiState.Loading -> {
                    btnRegister.isEnabled = false
                    btnRegister.text = "Registrando..."
                }
                is AuthViewModel.UiState.Success -> {
                    Toast.makeText(this, "¡Cuenta creada con éxito! Ya puedes iniciar sesión.", Toast.LENGTH_LONG).show()
                    finish()
                }
                is AuthViewModel.UiState.Error -> {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Registrarme"
                    mostrarError(result.message)
                }
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        tvError.text = mensaje
        tvError.visibility = View.VISIBLE
    }
}