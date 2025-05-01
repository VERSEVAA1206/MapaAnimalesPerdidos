package com.example.mapaanimalesperdidos.Ventanas

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mapaanimalesperdidos.R
import com.example.mapaanimalesperdidos.Reportes.Reporte
import com.google.firebase.database.*

class ListaReportesActivity : AppCompatActivity() {

    private lateinit var listaReportes: ListView
    private lateinit var adapter: ArrayAdapter<Reporte>
    private lateinit var lista: ArrayList<Reporte>
    private lateinit var refReportes: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_reportes)

        listaReportes = findViewById(R.id.listaReportes)
        lista = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista)
        listaReportes.adapter = adapter

        refReportes = FirebaseDatabase.getInstance().getReference("reportes")

        refReportes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                for (dato in snapshot.children) {
                    val r = dato.getValue(Reporte::class.java)
                    if (r != null) {
                        lista.add(r)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ListaReportesActivity,
                    "Error al cargar datos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        listaReportes.setOnItemClickListener { _, _, position, _ ->
            val seleccionado = lista[position]
            val i = Intent(this, DetalleReporteActivity::class.java)
            i.putExtra("nombre", seleccionado.nombre)
            i.putExtra("descripcion", seleccionado.descripcion)
            i.putExtra("ubicacion", seleccionado.ubicacion)
            i.putExtra("fotoUrl", seleccionado.fotoUrl)
            startActivity(i)
        }
    }
}
