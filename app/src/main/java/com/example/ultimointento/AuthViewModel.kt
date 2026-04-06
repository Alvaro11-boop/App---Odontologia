package com.example.ultimointento

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val userId: Long, val userType: UserType) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _authResult = MutableLiveData<UiState>()
    val authResult: LiveData<UiState> = _authResult

    // Alias para compatibilidad con código existente
    val registerResult: LiveData<UiState> = _authResult
    val loginResult: LiveData<UiState> = _authResult

    fun registerClinic(email: String, pass: String, phone: String, name: String, nit: String, addr: String, city: String, web: String) {
        execute { repository.registerClinic(email, pass, phone, name, nit, addr, city, web) }
    }

    fun registerDentist(email: String, pass: String, phone: String, name: String, specialty: String, license: String) {
        execute { repository.registerDentist(email, pass, phone, name, specialty, license) }
    }

    fun registerPatient(email: String, pass: String, phone: String, name: String, doc: String, birth: String) {
        execute { repository.registerPatient(email, pass, phone, name, doc, birth) }
    }

    fun login(email: String, pass: String) {
        execute { repository.login(email, pass) }
    }

    private fun execute(call: suspend () -> AuthResult) {
        _authResult.value = UiState.Loading
        viewModelScope.launch {
            val result = call()
            _authResult.value = when (result) {
                is AuthResult.Success -> UiState.Success(result.user.id, result.user.userType)
                is AuthResult.Error -> UiState.Error(result.message)
            }
        }
    }
}

class AuthViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repository) as T
    }
}
