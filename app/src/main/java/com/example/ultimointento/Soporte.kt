package com.example.ultimointento

// --- TODOS LOS MODELOS EN UN SOLO LUGAR ---
enum class UserType { PATIENT, DENTIST, CLINIC }
data class User(
    val id: Long, 
    val email: String, 
    val userType: UserType,
    var pass: String = "",
    var name: String = "",
    var phone: String = "",
    var doc: String = "",
    var specialty: String = ""
)

data class Appointment(
    val id: String,
    val patientId: Long,
    val dentistId: Long,
    val date: String,
    val time: String,
    val dentistName: String,
    val dentistSpecialty: String,
    var status: String = "CONFIRMED" // CONFIRMED, CANCELLED
)
sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class UserRepository(private val storage: LocalStorageManager? = null) {
    suspend fun registerClinic(email: String, pass: String, phone: String, name: String, nit: String, addr: String, city: String, web: String): AuthResult {
        return AuthResult.Success(User(1L, email, UserType.CLINIC))
    }

    suspend fun registerDentist(email: String, pass: String, phone: String, name: String, specialty: String, license: String): AuthResult {
        return AuthResult.Success(User(2L, email, UserType.DENTIST))
    }

    suspend fun registerPatient(email: String, pass: String, phone: String, name: String, doc: String, birth: String): AuthResult {
        val user = User(
            id = System.currentTimeMillis(), 
            email = email, 
            userType = UserType.PATIENT, 
            pass = pass, 
            name = name, 
            phone = phone, 
            doc = doc
        )
        storage?.saveUser(user)
        return AuthResult.Success(user)
    }

    suspend fun login(email: String, pass: String): AuthResult {
        val user = storage?.getUserByEmail(email)
        if (user != null && user.pass == pass) {
            storage?.saveLoggedInUser(user)
            return AuthResult.Success(user)
        }
        return AuthResult.Error("Credenciales inválidas")
    }
}
