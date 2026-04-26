package com.example.ultimointento

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class LocalStorageManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("FarmaciaPrefs", Context.MODE_PRIVATE)

    // --- Usuarios ---
    fun saveUsuario(usuario: Usuario) {
        val usuarios = getAllUsuarios()
        val index = usuarios.indexOfFirst { it.correo == usuario.correo }
        if (index != -1) {
            usuarios[index] = usuario
        } else {
            usuarios.add(usuario)
        }
        
        val jsonArray = JSONArray()
        for (u in usuarios) {
            val jsonObject = JSONObject().apply {
                put("id_usuario", u.id_usuario)
                put("nombre", u.nombre)
                put("correo", u.correo)
                put("contrasena", u.contrasena)
                put("rol", u.rol)
                put("telefono", u.telefono)
            }
            jsonArray.put(jsonObject)
        }
        prefs.edit().putString("usuarios_list", jsonArray.toString()).apply()
    }

    private fun getAllUsuarios(): MutableList<Usuario> {
        val jsonString = prefs.getString("usuarios_list", "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        
        if (jsonArray.length() == 0) {
            val defaultUser = Usuario(
                id_usuario = 1L,
                nombre = "Usuario Prueba",
                correo = "garrido@gmail.com",
                contrasena = "123456",
                telefono = "0000000000"
            )
            val seedArray = JSONArray()
            val obj = JSONObject().apply {
                put("id_usuario", defaultUser.id_usuario)
                put("nombre", defaultUser.nombre)
                put("correo", defaultUser.correo)
                put("contrasena", defaultUser.contrasena)
                put("rol", defaultUser.rol)
                put("telefono", defaultUser.telefono)
            }
            seedArray.put(obj)
            prefs.edit().putString("usuarios_list", seedArray.toString()).apply()
            
            return mutableListOf(defaultUser)
        }
        
        val usuarios = mutableListOf<Usuario>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            usuarios.add(
                Usuario(
                    id_usuario = obj.getLong("id_usuario"),
                    nombre = obj.getString("nombre"),
                    correo = obj.getString("correo"),
                    contrasena = obj.optString("contrasena", ""),
                    rol = obj.optString("rol", "CLIENTE"),
                    telefono = obj.optString("telefono", "")
                )
            )
        }
        return usuarios
    }

    fun getUsuarioByCorreo(correo: String): Usuario? {
        return getAllUsuarios().find { it.correo == correo }
    }

    // --- Usuario Logueado ---
    fun saveLoggedInUsuario(usuario: Usuario?) {
        if (usuario == null) {
            prefs.edit().remove("logged_in_correo").apply()
        } else {
            prefs.edit().putString("logged_in_correo", usuario.correo).apply()
        }
    }

    fun getLoggedInUsuario(): Usuario? {
        val correo = prefs.getString("logged_in_correo", null) ?: return null
        return getUsuarioByCorreo(correo)
    }

    // --- Medicamentos (Catálogo) ---
    fun getMedicamentos(): List<Medicamento> {
        val jsonString = prefs.getString("medicamentos_list", "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val medicamentos = mutableListOf<Medicamento>()
        
        if (jsonArray.length() == 0) {
            // Seed some default medicines con precios COP realistas
            val m1 = Medicamento(1L, "Acetaminofén 500mg", "Analgésico y antipirético (Paracetamol)", 4500f, 100)
            val m2 = Medicamento(2L, "Ibuprofeno 400mg", "Antiinflamatorio y analgésico", 7500f, 50)
            val m3 = Medicamento(3L, "Amoxicilina 500mg", "Antibiótico (Requiere fórmula)", 12000f, 30)
            val m4 = Medicamento(4L, "Loratadina 10mg", "Antihistamínico para alergias", 5500f, 80)
            val m5 = Medicamento(5L, "Omeprazol 20mg", "Protector gástrico y antiácido", 10500f, 60)
            
            val seedList = listOf(m1, m2, m3, m4, m5)
            val seedArray = JSONArray()
            for (m in seedList) {
                val obj = JSONObject().apply {
                    put("id_medicamento", m.id_medicamento)
                    put("nombre", m.nombre)
                    put("descripcion", m.descripcion)
                    put("precio", m.precio)
                    put("stock", m.stock)
                    put("imageUrl", m.imageUrl)
                }
                seedArray.put(obj)
            }
            prefs.edit().putString("medicamentos_list", seedArray.toString()).apply()
            
            return seedList
        }

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            medicamentos.add(
                Medicamento(
                    id_medicamento = obj.getLong("id_medicamento"),
                    nombre = obj.getString("nombre"),
                    descripcion = obj.getString("descripcion"),
                    precio = obj.getDouble("precio").toFloat(),
                    stock = obj.getInt("stock"),
                    imageUrl = obj.optString("imageUrl", "")
                )
            )
        }
        return medicamentos
    }

    fun saveMedicamento(medicamento: Medicamento) {
        val medicamentos = getMedicamentos().toMutableList()
        val index = medicamentos.indexOfFirst { it.id_medicamento == medicamento.id_medicamento }
        if (index != -1) {
            medicamentos[index] = medicamento
        } else {
            medicamentos.add(medicamento)
        }
        
        val jsonArray = JSONArray()
        for (m in medicamentos) {
            val obj = JSONObject().apply {
                put("id_medicamento", m.id_medicamento)
                put("nombre", m.nombre)
                put("descripcion", m.descripcion)
                put("precio", m.precio)
                put("stock", m.stock)
                put("imageUrl", m.imageUrl)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("medicamentos_list", jsonArray.toString()).apply()
    }
    
    fun getMedicamentoById(id: Long): Medicamento? {
        return getMedicamentos().find { it.id_medicamento == id }
    }

    // --- Carrito ---
    fun getCarrito(id_usuario: Long): Carrito {
        val jsonString = prefs.getString("carrito_$id_usuario", null)
        if (jsonString == null) {
            return Carrito(System.currentTimeMillis(), id_usuario)
        }
        
        val obj = JSONObject(jsonString)
        val carrito = Carrito(obj.getLong("id_carrito"), obj.getLong("id_usuario"))
        val detallesArray = obj.getJSONArray("detalles")
        for (i in 0 until detallesArray.length()) {
            val detObj = detallesArray.getJSONObject(i)
            carrito.detalles.add(
                DetalleCarrito(
                    id_detalle_carrito = detObj.getLong("id_detalle_carrito"),
                    id_carrito = detObj.getLong("id_carrito"),
                    id_medicamento = detObj.getLong("id_medicamento"),
                    cantidad = detObj.getInt("cantidad")
                )
            )
        }
        return carrito
    }

    fun saveCarrito(carrito: Carrito) {
        val obj = JSONObject().apply {
            put("id_carrito", carrito.id_carrito)
            put("id_usuario", carrito.id_usuario)
            val detallesArray = JSONArray()
            for (d in carrito.detalles) {
                val detObj = JSONObject().apply {
                    put("id_detalle_carrito", d.id_detalle_carrito)
                    put("id_carrito", d.id_carrito)
                    put("id_medicamento", d.id_medicamento)
                    put("cantidad", d.cantidad)
                }
                detallesArray.put(detObj)
            }
            put("detalles", detallesArray)
        }
        prefs.edit().putString("carrito_${carrito.id_usuario}", obj.toString()).apply()
    }
    
    fun clearCarrito(id_usuario: Long) {
        prefs.edit().remove("carrito_$id_usuario").apply()
    }

    // --- Pedidos ---
    fun getPedidos(id_usuario: Long): List<Pedido> {
        val jsonString = prefs.getString("pedidos_list", "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val pedidos = mutableListOf<Pedido>()
        
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            if (obj.getLong("id_usuario") == id_usuario) {
                val detallesList = mutableListOf<DetallePedido>()
                val detallesArray = obj.getJSONArray("detalles")
                for (j in 0 until detallesArray.length()) {
                    val detObj = detallesArray.getJSONObject(j)
                    detallesList.add(
                        DetallePedido(
                            id_detalle = detObj.getLong("id_detalle"),
                            id_pedido = detObj.getLong("id_pedido"),
                            id_medicamento = detObj.getLong("id_medicamento"),
                            cantidad = detObj.getInt("cantidad"),
                            subtotal = detObj.getDouble("subtotal").toFloat()
                        )
                    )
                }
                
                pedidos.add(
                    Pedido(
                        id_pedido = obj.getLong("id_pedido"),
                        fecha = obj.getString("fecha"),
                        total = obj.getDouble("total").toFloat(),
                        estado = obj.getString("estado"),
                        id_usuario = obj.getLong("id_usuario"),
                        id_domicilio = obj.getLong("id_domicilio"),
                        detalles = detallesList,
                        id_domiciliario = if (obj.has("id_domiciliario")) obj.getLong("id_domiciliario") else null
                    )
                )
            }
        }
        return pedidos
    }

    fun savePedido(pedido: Pedido) {
        val pedidos = getAllPedidos()
        val index = pedidos.indexOfFirst { it.id_pedido == pedido.id_pedido }
        if (index != -1) {
            pedidos[index] = pedido
        } else {
            pedidos.add(pedido)
        }
        
        val jsonArray = JSONArray()
        for (p in pedidos) {
            val obj = JSONObject().apply {
                put("id_pedido", p.id_pedido)
                put("fecha", p.fecha)
                put("total", p.total)
                put("estado", p.estado)
                put("id_usuario", p.id_usuario)
                put("id_domicilio", p.id_domicilio)
                if (p.id_domiciliario != null) {
                    put("id_domiciliario", p.id_domiciliario)
                }
                
                val detallesArray = JSONArray()
                for (d in p.detalles) {
                    val detObj = JSONObject().apply {
                        put("id_detalle", d.id_detalle)
                        put("id_pedido", d.id_pedido)
                        put("id_medicamento", d.id_medicamento)
                        put("cantidad", d.cantidad)
                        put("subtotal", d.subtotal)
                    }
                    detallesArray.put(detObj)
                }
                put("detalles", detallesArray)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("pedidos_list", jsonArray.toString()).apply()
    }
    
    private fun getAllPedidos(): MutableList<Pedido> {
        val jsonString = prefs.getString("pedidos_list", "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val pedidos = mutableListOf<Pedido>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val detallesList = mutableListOf<DetallePedido>()
            val detallesArray = obj.getJSONArray("detalles")
            for (j in 0 until detallesArray.length()) {
                val detObj = detallesArray.getJSONObject(j)
                detallesList.add(
                    DetallePedido(
                        id_detalle = detObj.getLong("id_detalle"),
                        id_pedido = detObj.getLong("id_pedido"),
                        id_medicamento = detObj.getLong("id_medicamento"),
                        cantidad = detObj.getInt("cantidad"),
                        subtotal = detObj.getDouble("subtotal").toFloat()
                    )
                )
            }
            pedidos.add(
                Pedido(
                    id_pedido = obj.getLong("id_pedido"),
                    fecha = obj.getString("fecha"),
                    total = obj.getDouble("total").toFloat(),
                    estado = obj.getString("estado"),
                    id_usuario = obj.getLong("id_usuario"),
                    id_domicilio = obj.getLong("id_domicilio"),
                    detalles = detallesList,
                    id_domiciliario = if (obj.has("id_domiciliario")) obj.getLong("id_domiciliario") else null
                )
            )
        }
        return pedidos
    }
    
    // --- Domicilios ---
    fun getDomicilios(id_usuario: Long): List<Domicilio> {
        val jsonString = prefs.getString("domicilios_list", "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val domicilios = mutableListOf<Domicilio>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            if (obj.getLong("id_usuario") == id_usuario) {
                domicilios.add(
                    Domicilio(
                        id_domicilio = obj.getLong("id_domicilio"),
                        direccion = obj.getString("direccion"),
                        ciudad = obj.getString("ciudad"),
                        referencia = obj.getString("referencia"),
                        id_usuario = obj.getLong("id_usuario")
                    )
                )
            }
        }
        return domicilios
    }
    
    fun saveDomicilio(domicilio: Domicilio) {
        val domicilios = getAllDomicilios()
        val index = domicilios.indexOfFirst { it.id_domicilio == domicilio.id_domicilio }
        if (index != -1) {
            domicilios[index] = domicilio
        } else {
            domicilios.add(domicilio)
        }
        
        val jsonArray = JSONArray()
        for (d in domicilios) {
            val obj = JSONObject().apply {
                put("id_domicilio", d.id_domicilio)
                put("direccion", d.direccion)
                put("ciudad", d.ciudad)
                put("referencia", d.referencia)
                put("id_usuario", d.id_usuario)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("domicilios_list", jsonArray.toString()).apply()
    }
    
    private fun getAllDomicilios(): MutableList<Domicilio> {
        val jsonString = prefs.getString("domicilios_list", "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val domicilios = mutableListOf<Domicilio>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            domicilios.add(
                Domicilio(
                    id_domicilio = obj.getLong("id_domicilio"),
                    direccion = obj.getString("direccion"),
                    ciudad = obj.getString("ciudad"),
                    referencia = obj.getString("referencia"),
                    id_usuario = obj.getLong("id_usuario")
                )
            )
        }
        return domicilios
    }

    // --- Domiciliarios ---
    fun getDomiciliarios(): List<Domiciliario> {
        val jsonString = prefs.getString("domiciliarios_list", "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        
        if (jsonArray.length() == 0) {
            // Seed default domiciliarios
            val d1 = Domiciliario(1L, "Carlos Arturo Mendoza", "3214567890", "XYZ-12C", "Pulsar NS 200")
            val d2 = Domiciliario(2L, "Luis Fernando Gomez", "3119876543", "ABC-98D", "Yamaha FZN 150")
            
            val seedArray = JSONArray()
            listOf(d1, d2).forEach { dom ->
                val obj = JSONObject().apply {
                    put("id_domiciliario", dom.id_domiciliario)
                    put("nombre", dom.nombre)
                    put("telefono", dom.telefono)
                    put("placa_moto", dom.placa_moto)
                    put("modelo_moto", dom.modelo_moto)
                }
                seedArray.put(obj)
            }
            prefs.edit().putString("domiciliarios_list", seedArray.toString()).apply()
            return listOf(d1, d2)
        }
        
        val domiciliarios = mutableListOf<Domiciliario>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            domiciliarios.add(
                Domiciliario(
                    id_domiciliario = obj.getLong("id_domiciliario"),
                    nombre = obj.getString("nombre"),
                    telefono = obj.getString("telefono"),
                    placa_moto = obj.getString("placa_moto"),
                    modelo_moto = obj.getString("modelo_moto")
                )
            )
        }
        return domiciliarios
    }
    fun resetMedicamentos() {
        prefs.edit().remove("medicamentos_list").apply()
    }

    fun getDomiciliarioById(id: Long): Domiciliario? {
        return getDomiciliarios().find { it.id_domiciliario == id }
    }
}

