package com.example.mapaanimalesperdidos.Ventanas

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mapaanimalesperdidos.R
import java.net.URL

class DetalleReporteActivity : AppCompatActivity() {

    private lateinit var txtNombre: TextView
    private lateinit var txtDescripcion: TextView
    private lateinit var txtUbicacion: TextView
    private lateinit var imgFoto: ImageView
    private lateinit var btnVolver: ImageButton
    private lateinit var btnCompartir: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_reporte)

        // Inicializar vistas
        txtNombre = findViewById(R.id.txtNombre)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        txtUbicacion = findViewById(R.id.txtUbicacion)
        imgFoto = findViewById(R.id.imgFoto)
        btnVolver = findViewById(R.id.btnVolver)
        btnCompartir = findViewById(R.id.btnCompartir)

        // Obtener datos del intent
        val intent = intent
        val nombre = intent.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val ubicacion = intent.getStringExtra("ubicacion")
        val url = intent.getStringExtra("fotoUrl")

        // Asignar texto
        txtNombre.text = nombre
        txtDescripcion.text = descripcion
        txtUbicacion.text = ubicacion

        // Cargar imagen desde URL (en un hilo, sin Glide)
        Thread {
            try {
                val input = URL(url).openStream()
                val bitmap = BitmapFactory.decodeStream(input)
                runOnUiThread {
                    imgFoto.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()

        // Botones
        btnVolver.setOnClickListener {
            onBackPressed()
        }

        btnCompartir.setOnClickListener {
            compartirReporte()
        }
    }

    private fun compartirReporte() {
        val reporteTexto = """
            🐾 Reporte de Animal Perdido
            📌 Nombre: ${txtNombre.text}
            📝 Descripción: ${txtDescripcion.text}
            📍 Ubicación: ${txtUbicacion.text}
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, reporteTexto)
        }
        startActivity(Intent.createChooser(shareIntent, "Compartir reporte"))
    }
}
