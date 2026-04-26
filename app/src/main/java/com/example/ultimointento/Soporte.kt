package com.example.ultimointento

import java.util.UUID

// --- MODELOS DEL ERD FARMACIA ---
data class Usuario(
    val id_usuario: Long,
    var nombre: String,
    var correo: String,
    var contrasena: String,
    var rol: String = "CLIENTE",
    var telefono: String = ""
)

data class Domicilio(
    val id_domicilio: Long,
    var direccion: String,
    var ciudad: String,
    var referencia: String,
    val id_usuario: Long
)

data class Medicamento(
    val id_medicamento: Long,
    var nombre: String,
    var descripcion: String,
    var precio: Float,
    var stock: Int,
    var imageUrl: String = "" // Added for visual representation
)

data class DetalleCarrito(
    val id_detalle_carrito: Long,
    val id_carrito: Long,
    val id_medicamento: Long,
    var cantidad: Int
)

data class Carrito(
    val id_carrito: Long,
    val id_usuario: Long,
    val detalles: MutableList<DetalleCarrito> = mutableListOf() // Nested for easier access locally
)

data class DetallePedido(
    val id_detalle: Long,
    val id_pedido: Long,
    val id_medicamento: Long,
    val cantidad: Int,
    val subtotal: Float
)

data class Domiciliario(
    val id_domiciliario: Long,
    var nombre: String,
    var telefono: String,
    var placa_moto: String,
    var modelo_moto: String
)

data class Pedido(
    val id_pedido: Long,
    val fecha: String,
    val total: Float,
    var estado: String,
    val id_usuario: Long,
    val id_domicilio: Long,
    val detalles: List<DetallePedido> = listOf(), // Nested for easier access locally
    var id_domiciliario: Long? = null // Opcional, para asignar un repartidor
)

// --- RESULTADOS Y REPOSITORIOS ---
sealed class AuthResult {
    data class Success(val user: Usuario) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class UserRepository(private val storage: LocalStorageManager? = null) {
    suspend fun registerUsuario(correo: String, contrasena: String, telefono: String, nombre: String): AuthResult {
        val user = Usuario(
            id_usuario = System.currentTimeMillis(),
            correo = correo,
            contrasena = contrasena,
            nombre = nombre,
            telefono = telefono
        )
        storage?.saveUsuario(user)
        return AuthResult.Success(user)
    }

    suspend fun login(correo: String, contrasena: String): AuthResult {
        val user = storage?.getUsuarioByCorreo(correo)
        if (user != null && user.contrasena == contrasena) {
            storage?.saveLoggedInUsuario(user)
            return AuthResult.Success(user)
        }
        return AuthResult.Error("Credenciales inválidas")
    }
}
