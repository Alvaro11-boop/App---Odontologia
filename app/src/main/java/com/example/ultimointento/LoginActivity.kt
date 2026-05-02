package com.example.ultimointento

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

/**
 * Actividad encargada de manejar el inicio de sesión de los usuarios.
 */
class LoginActivity : AppCompatActivity() {

    // Variables para los elementos de la interfaz (UI)
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvError: TextView
    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegister: Button

    // Inyección del ViewModel que maneja la lógica de autenticación
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(UserRepository(LocalStorageManager(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Enlazar las variables con las vistas del diseño XML
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        tvError = findViewById(R.id.tvError)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnGoToRegister)

        // Configurar los eventos de clic de los botones
        configurarBotones()
        // Observar los cambios de estado (éxito, error, cargando) desde el ViewModel
        observarResultados()
    }

    /**
     * Configura las acciones que ocurren al presionar los botones.
     */
    private fun configurarBotones() {
        // Al hacer clic en "Ingresar"
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            // Validaciones básicas de campos vacíos
            if (email.isEmpty()) {
                mostrarError("Por favor ingresa tu correo electrónico")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                mostrarError("Por favor ingresa tu contraseña")
                return@setOnClickListener
            }

            // Si los campos están correctos, se intenta iniciar sesión
            ocultarError()
            mostrarCargando(true)
            viewModel.login(email, password)
        }

        // Al hacer clic en "Registrarse", abre la pantalla de registro
        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Escucha los resultados del proceso de inicio de sesión que provienen del ViewModel.
     */
    private fun observarResultados() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                // Si el inicio de sesión fue exitoso, redirige al HomeActivity principal
                is AuthViewModel.UiState.Success -> {
                    val intent = Intent(this, com.example.ultimointento.patient.HomeActivity::class.java)
                    startActivity(intent)
                    finish() // Cierra la pantalla de Login para que el usuario no pueda volver atrás
                }
                // Si hubo un error (contraseña incorrecta, usuario no existe)
                is AuthViewModel.UiState.Error -> {
                    mostrarCargando(false)
                    // Mostrar un mensaje más amigable dependiendo del error técnico
                    val mensajeAmigable = when {
                        result.message.contains("inválid", ignoreCase = true) ->
                            "Correo o contraseña incorrectos. ¿Ya tienes una cuenta?"
                        result.message.contains("no encontrado", ignoreCase = true) ->
                            "No existe una cuenta con ese correo. Por favor regístrate."
                        else -> result.message
                    }
                    mostrarError(mensajeAmigable)
                }
                // Mientras se está procesando la solicitud (cargando)
                AuthViewModel.UiState.Loading -> {
                    mostrarCargando(true)
                }
            }
        }
    }

    /**
     * Muestra un mensaje de error en la pantalla de forma visible.
     */
    private fun mostrarError(mensaje: String) {
        tvError.text = mensaje
        tvError.visibility = View.VISIBLE
    }

    /**
     * Oculta el mensaje de error.
     */
    private fun ocultarError() {
        tvError.visibility = View.GONE
    }

    /**
     * Deshabilita el botón de login y cambia su texto para mostrar que está procesando.
     */
    private fun mostrarCargando(cargando: Boolean) {
        btnLogin.isEnabled = !cargando
        btnLogin.text = if (cargando) "Ingresando..." else "Ingresar"
    }
}
