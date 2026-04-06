package com.example.ultimointento

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val userId: Long, val userType: UserType) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _registerResult = MutableLiveData<UiState>()
    val registerResult: LiveData<UiState> = _registerResult

    fun registerClinic(email: String, pass: String, phone: String, name: String, nit: String, addr: String, city: String, web: String) {
        _registerResult.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.registerClinic(email, pass, phone, name, nit, addr, city, web)
            _registerResult.value = when (result) {
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