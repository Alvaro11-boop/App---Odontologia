package com.example.ultimointento

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ultimointento.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(UserRepository(LocalStorageManager(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotones()
        observarResultados()
    }

    private fun configurarBotones() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty()) {
                mostrarError("Ingresa tu correo electrónico")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                mostrarError("Ingresa tu contraseña")
                return@setOnClickListener
            }

            ocultarError()
            viewModel.login(email, password)
        }

        binding.btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observarResultados() {
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is AuthViewModel.UiState.Success -> {
                    // Start PatientDashboardActivity
                    val intent = Intent(this, com.example.ultimointento.patient.PatientDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is AuthViewModel.UiState.Error -> {
                    mostrarCargando(false)
                    mostrarError(result.message)
                }
                AuthViewModel.UiState.Loading -> {
                    mostrarCargando(true)
                }
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        binding.tvError.text = mensaje
        binding.tvError.visibility = View.VISIBLE
    }

    private fun ocultarError() {
        binding.tvError.visibility = View.GONE
    }

    private fun mostrarCargando(cargando: Boolean) {
        binding.btnLogin.isEnabled = !cargando
        binding.btnLogin.text = if (cargando) "Ingresando..." else "Ingresar"
    }
}
