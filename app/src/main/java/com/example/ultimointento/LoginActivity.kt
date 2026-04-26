package com.example.ultimointento

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvError: TextView
    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegister: Button

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(UserRepository(LocalStorageManager(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        tvError = findViewById(R.id.tvError)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnGoToRegister)

        configurarBotones()
        observarResultados()
    }

    private fun configurarBotones() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty()) {
                mostrarError("Por favor ingresa tu correo electrónico")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                mostrarError("Por favor ingresa tu contraseña")
                return@setOnClickListener
            }

            ocultarError()
            mostrarCargando(true)
            viewModel.login(email, password)
        }

        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observarResultados() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is AuthViewModel.UiState.Success -> {
                    val intent = Intent(this, com.example.ultimointento.patient.HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is AuthViewModel.UiState.Error -> {
                    mostrarCargando(false)
                    // Mensaje de error más claro según el tipo de error
                    val mensajeAmigable = when {
                        result.message.contains("inválid", ignoreCase = true) ->
                            "Correo o contraseña incorrectos. ¿Ya tienes una cuenta?"
                        result.message.contains("no encontrado", ignoreCase = true) ->
                            "No existe una cuenta con ese correo. Por favor regístrate."
                        else -> result.message
                    }
                    mostrarError(mensajeAmigable)
                }
                AuthViewModel.UiState.Loading -> {
                    mostrarCargando(true)
                }
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        tvError.text = mensaje
        tvError.visibility = View.VISIBLE
    }

    private fun ocultarError() {
        tvError.visibility = View.GONE
    }

    private fun mostrarCargando(cargando: Boolean) {
        btnLogin.isEnabled = !cargando
        btnLogin.text = if (cargando) "Ingresando..." else "Ingresar"
    }
}
