package com.example.ultimointento

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ultimointento.data.AppDatabase
import com.example.ultimointento.data.model.UserType
import com.example.ultimointento.data.repository.UserRepository
import com.example.ultimointento.databinding.ActivityLoginBinding
import com.example.ultimointento.viewmodel.AuthViewModel
import com.example.ultimointento.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // ViewBinding: acceso directo a las vistas del XML sin findViewById
    private lateinit var binding: ActivityLoginBinding

    // ViewModel conectado al Repository
    private val viewModel: AuthViewModel by viewModels {
        val db = AppDatabase.getInstance(this)
        AuthViewModelFactory(UserRepository(db))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotones()
        observarResultados()
    }

    private fun configurarBotones() {

        // Botón "Ingresar"
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            // Validación básica antes de llamar al ViewModel
            if (email.isEmpty()) {
                mostrarError("Ingresa tu correo electrónico")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                mostrarError("Ingresa tu contraseña")
                return@setOnClickListener
            }

            ocultarError()
            mostrarCargando(true)
            viewModel.login(email, password)
        }

        // Botón "Crear cuenta nueva"
        binding.btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Texto "¿Olvidaste tu contraseña?"
        binding.tvForgotPassword.setOnClickListener {
            // TODO: implementar recuperación de contraseña
        }
    }

    private fun observarResultados() {

        // Observa el LiveData del ViewModel
        viewModel.loginResult.observe(this) { result ->
            mostrarCargando(false)

            when (result) {
                is AuthViewModel.UiState.Success -> {
                    // Redirigir según el tipo de usuario
                    val destino = when (result.userType) {
                        UserType.PATIENT -> PatientHomeActivity::class.java
                        UserType.DENTIST -> DentistHomeActivity::class.java
                        UserType.CLINIC  -> ClinicHomeActivity::class.java
                    }
                    val intent = Intent(this, destino).apply {
                        putExtra("USER_ID", result.userId)
                        // Limpiar el back stack: el usuario no puede volver al login
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }

                is AuthViewModel.UiState.Error -> {
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
        // Deshabilitar botón para evitar doble clic
        binding.btnLogin.isEnabled = !cargando
        binding.btnLogin.text = if (cargando) "Ingresando..." else "Ingresar"
    }
}