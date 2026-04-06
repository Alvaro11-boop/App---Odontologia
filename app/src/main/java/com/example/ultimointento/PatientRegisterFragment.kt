package com.example.ultimointento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
// IMPORTANTE: Este nombre debe ser exacto (Mayúsculas y sin guiones bajos)
import com.example.ultimointento.databinding.FragmentRegisterPatientBinding

class PatientRegisterFragment : Fragment() {

    // ERROR CORREGIDO: Antes tenías "fragment_register_patient" (eso es el XML, no la clase)
    private var _binding: FragmentRegisterPatientBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(UserRepository())
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
            // Tu lógica aquí
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}