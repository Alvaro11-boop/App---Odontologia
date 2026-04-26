package com.example.ultimointento.patient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ultimointento.LocalStorageManager
import com.example.ultimointento.R

class DomiciliariosActivity : AppCompatActivity() {

    private lateinit var storage: LocalStorageManager
    private lateinit var rvDomiciliarios: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_domiciliarios)

        storage = LocalStorageManager(this)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        rvDomiciliarios = findViewById(R.id.rvDomiciliarios)
        rvDomiciliarios.layoutManager = LinearLayoutManager(this)

        val lista = storage.getDomiciliarios()
        rvDomiciliarios.adapter = DomiciliarioAdapter(lista)
    }
}
