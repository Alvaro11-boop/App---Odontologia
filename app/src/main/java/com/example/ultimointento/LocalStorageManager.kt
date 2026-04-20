package com.example.ultimointento

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class LocalStorageManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("UltimoIntentoPrefs", Context.MODE_PRIVATE)

    // --- Users ---
    fun saveUser(user: User) {
        val users = getAllUsers()
        val index = users.indexOfFirst { it.email == user.email }
        if (index != -1) {
            users[index] = user
        } else {
            users.add(user)
        }
        
        val jsonArray = JSONArray()
        for (u in users) {
            val jsonObject = JSONObject().apply {
                put("id", u.id)
                put("email", u.email)
                put("userType", u.userType.name)
                put("pass", u.pass)
                put("name", u.name)
                put("phone", u.phone)
                put("doc", u.doc)
                put("specialty", u.specialty)
            }
            jsonArray.put(jsonObject)
        }
        prefs.edit().putString("users_list", jsonArray.toString()).apply()
    }

    private fun getAllUsers(): MutableList<User> {
        val jsonString = prefs.getString("users_list", "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val users = mutableListOf<User>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            users.add(
                User(
                    id = obj.getLong("id"),
                    email = obj.getString("email"),
                    userType = UserType.valueOf(obj.getString("userType")),
                    pass = obj.optString("pass", ""),
                    name = obj.optString("name", ""),
                    phone = obj.optString("phone", ""),
                    doc = obj.optString("doc", ""),
                    specialty = obj.optString("specialty", "")
                )
            )
        }
        return users
    }

    fun getUserByEmail(email: String): User? {
        return getAllUsers().find { it.email == email }
    }

    // --- Logged In User ---
    fun saveLoggedInUser(user: User?) {
        if (user == null) {
            prefs.edit().remove("logged_in_email").apply()
        } else {
            prefs.edit().putString("logged_in_email", user.email).apply()
        }
    }

    fun getLoggedInUser(): User? {
        val email = prefs.getString("logged_in_email", null) ?: return null
        return getUserByEmail(email)
    }

    // --- Dentists (Pre-loaded) ---
    fun getDentists(): List<User> {
        val users = getAllUsers().filter { it.userType == UserType.DENTIST }
        if (users.isEmpty()) {
            // Seed some fake dentists
            val d1 = User(1001L, "dentist1@test.com", UserType.DENTIST, "123", "Dr. Juan Pérez", "555-0001", "DOC1", "Odontología General")
            val d2 = User(1002L, "dentist2@test.com", UserType.DENTIST, "123", "Dra. Ana Gómez", "555-0002", "DOC2", "Odontología General")
            saveUser(d1)
            saveUser(d2)
            return listOf(d1, d2)
        }
        return users
    }

    // --- Appointments ---
    fun saveAppointment(appointment: Appointment) {
        val appointments = getAllAppointments()
        val index = appointments.indexOfFirst { it.id == appointment.id }
        if (index != -1) {
            appointments[index] = appointment
        } else {
            appointments.add(appointment)
        }
        saveAppointmentsList(appointments)
    }

    fun getAppointmentsForPatient(patientId: Long): List<Appointment> {
        return getAllAppointments().filter { it.patientId == patientId }
    }

    private fun getAllAppointments(): MutableList<Appointment> {
        val jsonString = prefs.getString("appointments_list", "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val appointments = mutableListOf<Appointment>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            appointments.add(
                Appointment(
                    id = obj.getString("id"),
                    patientId = obj.getLong("patientId"),
                    dentistId = obj.getLong("dentistId"),
                    date = obj.getString("date"),
                    time = obj.getString("time"),
                    dentistName = obj.getString("dentistName"),
                    dentistSpecialty = obj.getString("dentistSpecialty"),
                    status = obj.getString("status")
                )
            )
        }
        return appointments
    }

    private fun saveAppointmentsList(appointments: List<Appointment>) {
        val jsonArray = JSONArray()
        for (a in appointments) {
            val obj = JSONObject().apply {
                put("id", a.id)
                put("patientId", a.patientId)
                put("dentistId", a.dentistId)
                put("date", a.date)
                put("time", a.time)
                put("dentistName", a.dentistName)
                put("dentistSpecialty", a.dentistSpecialty)
                put("status", a.status)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("appointments_list", jsonArray.toString()).apply()
    }
}
