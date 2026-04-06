package com.example.ultimointento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ultimointento.databinding.FragmentRegisterClinicBinding

class ClinicRegisterFragment : Fragment() {

    private var _binding: FragmentRegisterClinicBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(UserRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterClinicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarBotonRegistro()
        observarResultado()
    }

    private fun configurarBotonRegistro() {
        binding.btnRegister.setOnClickListener {
            val email      = binding.etEmail.text.toString().trim()
            val password   = binding.etPassword.text.toString()
            val phone      = binding.etPhone.text.toString().trim()
            val clinicName = binding.etClinicName.text.toString().trim()
            val nit        = binding.etNit.text.toString().trim()
            val address    = binding.etAddress.text.toString().trim()
            val city       = binding.etCity.text.toString().trim()
            val website    = binding.etWebsite.text.toString().trim()

            if (!validarCampos(email, password, clinicName, nit, city)) return@setOnClickListener

            ocultarError()
            mostrarCargando(true)
            viewModel.registerClinic(
                email, password, phone,
                clinicName, nit, address, city, website
            )
        }
    }

    private fun validarCampos(
        email: String, password: String, clinicName: String,
        nit: String, city: String
    ): Boolean {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarError("Ingresa un correo electrónico válido")
            return false
        }
        if (password.length < 8) {
            mostrarError("La contraseña debe tener mínimo 8 caracteres")
            return false
        }
        if (clinicName.isEmpty()) {
            mostrarError("Ingresa el nombre de la clínica")
            return false
        }
        if (nit.isEmpty()) {
            mostrarError("Ingresa el NIT de la empresa")
            return false
        }
        if (city.isEmpty()) {
            mostrarError("Ingresa la ciudad")
            return false
        }
        return true
    }

    private fun observarResultado() {
        viewModel.registerResult.observe(viewLifecycleOwner) { state ->
            mostrarCargando(false)
            when (state) {
                is AuthViewModel.UiState.Success -> requireActivity().finish()
                is AuthViewModel.UiState.Error   -> mostrarError(state.message)
                AuthViewModel.UiState.Loading    -> mostrarCargando(true)
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
        binding.btnRegister.isEnabled = !cargando
        binding.btnRegister.text = if (cargando) "Creando cuenta..." else "Crear cuenta como Clínica"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
