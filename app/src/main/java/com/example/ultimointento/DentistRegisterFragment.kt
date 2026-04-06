package com.example.ultimointento.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ultimointento.data.AppDatabase
import com.example.ultimointento.data.repository.UserRepository
import com.example.ultimointento.databinding.FragmentRegisterDentistBinding
import com.example.ultimointento.viewmodel.AuthViewModel
import com.example.ultimointento.viewmodel.AuthViewModelFactory

class DentistRegisterFragment : Fragment() {

    private var _binding: FragmentRegisterDentistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        val db = AppDatabase.getInstance(requireContext())
        AuthViewModelFactory(UserRepository(db))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterDentistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarDropdownEspecialidad()
        configurarBotonRegistro()
        observarResultado()
    }

    private fun configurarDropdownEspecialidad() {
        val especialidades = listOf(
            "Odontología general",
            "Ortodoncia",
            "Endodoncia",
            "Periodoncia",
            "Cirugía oral y maxilofacial",
            "Odontopediatría",
            "Implantología",
            "Estética dental",
            "Otra"
        )
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            especialidades
        )
        binding.actvSpecialty.setAdapter(adapter)
    }

    private fun configurarBotonRegistro() {
        binding.btnRegister.setOnClickListener {
            val email    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val phone    = binding.etPhone.text.toString().trim()
            val fullName = binding.etFullName.text.toString().trim()
            val license  = binding.etLicense.text.toString().trim()
            val specialty = binding.actvSpecialty.text.toString()
            val bio      = binding.etBio.text.toString().trim()

            if (!validarCampos(email, password, fullName, license)) return@setOnClickListener

            ocultarError()
            mostrarCargando(true)
            viewModel.registerDentist(
                email, password, phone,
                fullName, license, specialty, bio
            )
        }
    }

    private fun validarCampos(
        email: String, password: String,
        fullName: String, license: String
    ): Boolean {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarError("Ingresa un correo electrónico válido")
            return false
        }
        if (password.length < 8) {
            mostrarError("La contraseña debe tener mínimo 8 caracteres")
            return false
        }
        if (fullName.isEmpty()) {
            mostrarError("Ingresa tu nombre completo")
            return false
        }
        if (license.isEmpty()) {
            mostrarError("Ingresa tu número de tarjeta profesional")
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
        binding.btnRegister.text = if (cargando) "Creando cuenta..." else "Crear cuenta como Odontólogo"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}