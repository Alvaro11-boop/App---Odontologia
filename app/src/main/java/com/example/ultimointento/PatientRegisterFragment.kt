package com.example.ultimointento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
// IMPORTANTE: Este nombre debe ser exacto (Mayúsculas y sin guiones bajos)
import com.example.ultimointento.databinding.FragmentRegisterPatientBinding
import android.widget.Toast
import android.content.Intent
class PatientRegisterFragment : Fragment() {

    // ERROR CORREGIDO: Antes tenías "fragment_register_patient" (eso es el XML, no la clase)
    private var _binding: FragmentRegisterPatientBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(UserRepository(LocalStorageManager(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ERROR CORREGIDO: Aquí también debe ser la Clase en Mayúsculas
        _binding = FragmentRegisterPatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aquí conectas tus botones.
        // Si en el XML el id es btn_register_patient, aquí se escribe btnRegisterPatient
        binding.btnRegisterPatient.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString()
            val phone = binding.etPhone.text.toString().trim()
            val name = binding.etFullName.text.toString().trim()
            val doc = binding.etIdNumber.text.toString().trim()
            val birth = binding.etBirthDate.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty() || name.isEmpty() || doc.isEmpty()) {
                binding.tvError.text = "Por favor, completa los campos obligatorios."
                binding.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            binding.tvError.visibility = View.GONE
            viewModel.registerPatient(email, pass, phone, name, doc, birth)
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthViewModel.UiState.Loading -> {
                    binding.btnRegisterPatient.isEnabled = false
                    binding.btnRegisterPatient.text = "Registrando..."
                }
                is AuthViewModel.UiState.Success -> {
                    Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                    requireActivity().finish() // Close register activity and return to login
                }
                is AuthViewModel.UiState.Error -> {
                    binding.btnRegisterPatient.isEnabled = true
                    binding.btnRegisterPatient.text = "Crear cuenta como Paciente"
                    binding.tvError.text = result.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}