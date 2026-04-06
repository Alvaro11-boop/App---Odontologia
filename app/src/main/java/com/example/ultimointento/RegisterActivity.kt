package com.example.ultimointento

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ultimointento.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbar()
        configurarTarjetas()
    }

    private fun configurarToolbar() {
        setSupportActionBar(binding.toolbar)
        // Habilita el botón "atrás" en la toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun configurarTarjetas() {

        binding.cardPatient.setOnClickListener {
            abrirFormulario(TipoRegistro.PACIENTE)
        }

        binding.cardDentist.setOnClickListener {
            abrirFormulario(TipoRegistro.ODONTOLOGO)
        }

        binding.cardClinic.setOnClickListener {
            abrirFormulario(TipoRegistro.CLINICA)
        }
    }

    private fun abrirFormulario(tipo: TipoRegistro) {
        // Selecciona el Fragment según el tipo elegido
        val fragment = when (tipo) {
            TipoRegistro.PACIENTE   -> PatientRegisterFragment()
            TipoRegistro.ODONTOLOGO -> DentistRegisterFragment()
            TipoRegistro.CLINICA    -> ClinicRegisterFragment()
        }


        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

    enum class TipoRegistro { PACIENTE, ODONTOLOGO, CLINICA }
}