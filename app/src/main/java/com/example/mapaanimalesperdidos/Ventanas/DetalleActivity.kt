package com.example.mapaanimalesperdidos.Ventanas

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mapaanimalesperdidos.Model.AnimalReport
import com.example.mapaanimalesperdidos.R

class DetalleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        val reporte = intent.getSerializableExtra("reporte") as? AnimalReport
            ?: return

        val img = findViewById<ImageView>(R.id.imgDetalle)
        val nombre = findViewById<TextView>(R.id.tvNombre)
        val tipo = findViewById<TextView>(R.id.tvTipo)
        val descripcion = findViewById<TextView>(R.id.tvDescripcion)

        Glide.with(this).load(reporte.fotoUrl).into(img)
        nombre.text = reporte.nombre
        tipo.text = "Tipo: ${reporte.tipo}"
        descripcion.text = reporte.descripcion
    }
}
