package com.example.ultimointento.patient

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ultimointento.Appointment
import com.example.ultimointento.LocalStorageManager
import com.example.ultimointento.R
import java.util.Calendar
import java.util.UUID

class BookAppointmentActivity : AppCompatActivity() {

    private lateinit var storage: LocalStorageManager
    private var dentistId: Long = 0
    private var dentistName: String = ""
    private var dentistSpecialty: String = ""

    private val CHANNEL_ID = "dental_appointments_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)

        createNotificationChannel()

        storage = LocalStorageManager(this)
        dentistId = intent.getLongExtra("DENTIST_ID", 0)
        dentistName = intent.getStringExtra("DENTIST_NAME") ?: ""
        dentistSpecialty = intent.getStringExtra("DENTIST_SPECIALTY") ?: ""

        findViewById<TextView>(R.id.tvDentistInfo).text = "Con: $dentistName\nEspecialidad: $dentistSpecialty"

        val etDate = findViewById<EditText>(R.id.etDate)
        val etTime = findViewById<EditText>(R.id.etTime)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmAppointment)

        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, { _, y, m, d ->
                etDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
            }, year, month, day).show()
        }

        etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            TimePickerDialog(this, { _, h, m ->
                etTime.setText(String.format("%02d:%02d", h, m))
            }, hour, minute, true).show()
        }

        btnConfirm.setOnClickListener {
            val date = etDate.text.toString()
            val time = etTime.text.toString()

            if (date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Por favor, seleccione fecha y hora", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = storage.getLoggedInUser()
            if (user == null) {
                finish()
                return@setOnClickListener
            }

            val appointment = Appointment(
                id = UUID.randomUUID().toString(),
                patientId = user.id,
                dentistId = dentistId,
                date = date,
                time = time,
                dentistName = dentistName,
                dentistSpecialty = dentistSpecialty,
                status = "CONFIRMED"
            )

            storage.saveAppointment(appointment)
            sendNotification("Cita Confirmada", "Tienes una cita el $date a las $time con $dentistName")
            
            Toast.makeText(this, "Cita agendada correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Citas Odontológicas"
            val descriptionText = "Notificaciones para citas agendadas"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, content: String) {
        val intent = Intent(this, PatientDashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, pendingIntentFlags)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@BookAppointmentActivity,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            } else {
                // Si estamos en Android 13+ y no tenemos permiso, lo pedimos (normalmente se pide en el Dashboard o antes, pero aquí intentamos)
                ActivityCompat.requestPermissions(
                    this@BookAppointmentActivity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }
}
