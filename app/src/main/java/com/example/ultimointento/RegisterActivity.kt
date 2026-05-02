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

/**
 * Actividad encargada de registrar un nuevo usuario en la aplicación.
 */
class RegisterActivity : AppCompatActivity() {

    // Variables de la interfaz de usuario (UI)
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvError: TextView
    private lateinit var btnRegister: Button

    // Inyección del ViewModel para manejar la lógica de registro de forma separada
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(UserRepository(LocalStorageManager(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Enlazar variables con el archivo XML
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        tvError = findViewById(R.id.tvError)
        btnRegister = findViewById(R.id.btnRegister)

        // Configuración de la barra superior para poder retroceder
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Inicializar acciones
        configurarBotonRegistro()
        observarViewModel()
    }

    /**
     * Configura lo que sucede al hacer clic en el botón de "Registrarme".
     */
    private fun configurarBotonRegistro() {
        btnRegister.setOnClickListener {
            // Extraer textos de los campos de entrada
            val name = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val pass = etPassword.text.toString()

            // Validaciones detalladas para evitar datos vacíos o incorrectos
            if (name.isEmpty()) {
                mostrarError("Por favor ingresa tu nombre completo.")
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                mostrarError("Por favor ingresa tu correo electrónico.")
                return@setOnClickListener
            }
            // Verifica que el correo tenga formato válido (ej: texto@texto.com)
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mostrarError("El correo electrónico no tiene un formato válido.")
                return@setOnClickListener
            }
            if (pass.isEmpty()) {
                mostrarError("Por favor ingresa una contraseña.")
                return@setOnClickListener
            }
            // Verifica que la contraseña sea suficientemente larga
            if (pass.length < 6) {
                mostrarError("La contraseña debe tener al menos 6 caracteres.")
                return@setOnClickListener
            }

            // Si todo está correcto, oculta errores y procede a registrar enviando los datos al ViewModel
            tvError.visibility = View.GONE
            viewModel.registerUsuario(email, pass, phone, name)
        }
    }

    /**
     * Observa los resultados del intento de registro que emite el ViewModel.
     */
    private fun observarViewModel() {
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                // Estado de carga mientras se guarda en la base de datos local
                is AuthViewModel.UiState.Loading -> {
                    btnRegister.isEnabled = false
                    btnRegister.text = "Registrando..."
                }
                // Si el registro fue completamente exitoso
                is AuthViewModel.UiState.Success -> {
                    Toast.makeText(this, "¡Cuenta creada con éxito! Ya puedes iniciar sesión.", Toast.LENGTH_LONG).show()
                    finish() // Regresa automáticamente a la pantalla de login
                }
                // Si ocurrió algún error (ej: el correo ya está registrado)
                is AuthViewModel.UiState.Error -> {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Registrarme"
                    mostrarError(result.message)
                }
            }
        }
    }

    /**
     * Función auxiliar para mostrar un mensaje de error visible al usuario.
     */
    private fun mostrarError(mensaje: String) {
        tvError.text = mensaje
        tvError.visibility = View.VISIBLE
    }
}