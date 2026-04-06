package com.example.ultimointento

// --- TODOS LOS MODELOS EN UN SOLO LUGAR ---
enum class UserType { PATIENT, DENTIST, CLINIC }
data class User(val id: Long, val email: String, val userType: UserType)

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class UserRepository {
    suspend fun registerClinic(email: String, pass: String, phone: String, name: String, nit: String, addr: String, city: String, web: String): AuthResult {
        return AuthResult.Success(User(1L, email, UserType.CLINIC))
    }

    suspend fun registerDentist(email: String, pass: String, phone: String, name: String, specialty: String, license: String): AuthResult {
        return AuthResult.Success(User(2L, email, UserType.DENTIST))
    }

    suspend fun registerPatient(email: String, pass: String, phone: String, name: String, doc: String, birth: String): AuthResult {
        return AuthResult.Success(User(3L, email, UserType.PATIENT))
    }

    suspend fun login(email: String, pass: String): AuthResult {
        return AuthResult.Success(User(1L, email, UserType.PATIENT))
    }
}
