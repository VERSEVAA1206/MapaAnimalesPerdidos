package com.example.mapaanimalesperdidos.Ventanas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.mapaanimalesperdidos.Servicios.MapaReporte
import com.example.mapaanimalesperdidos.R
import com.example.mapaanimalesperdidos.Reportes.ReporteActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnIrAReporte: Button
    private lateinit var btnVerLista: Button
    private lateinit var btnCerrarSesion: Button
 private lateinit var btnmapa: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnIrAReporte = findViewById(R.id.btnIrAReporte)
        btnVerLista = findViewById(R.id.btnVerLista)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        btnmapa = findViewById(R.id.btnmapa)
        btnIrAReporte.setOnClickListener {
            startActivity(Intent(this, ReporteActivity::class.java))
        }

        btnVerLista.setOnClickListener {
            startActivity(Intent(this, ListaReportesActivity::class.java))
        }

        btnCerrarSesion.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        btnmapa.setOnClickListener {
            startActivity(Intent(this, MapaReporte::class.java))
            finish()
        }
    }
}
