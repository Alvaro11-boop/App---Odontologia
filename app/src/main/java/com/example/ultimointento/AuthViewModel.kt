package com.example.ultimointento

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val userId: Long, val rol: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _authResult = MutableLiveData<UiState>()
    val authResult: LiveData<UiState> = _authResult

    // Alias para compatibilidad con código existente
    val registerResult: LiveData<UiState> = _authResult
    val loginResult: LiveData<UiState> = _authResult

    fun registerUsuario(correo: String, contrasena: String, telefono: String, nombre: String) {
        execute { repository.registerUsuario(correo, contrasena, telefono, nombre) }
    }

    fun login(correo: String, contrasena: String) {
        execute { repository.login(correo, contrasena) }
    }

    private fun execute(call: suspend () -> AuthResult) {
        _authResult.value = UiState.Loading
        viewModelScope.launch {
            val result = call()
            _authResult.value = when (result) {
                is AuthResult.Success -> UiState.Success(result.user.id_usuario, result.user.rol)
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
