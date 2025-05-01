package com.example.mapaanimalesperdidos.Servicios

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mapaanimalesperdidos.R
import com.example.mapaanimalesperdidos.Reportes.ReporteDialogFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*

class MapaReporte : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val databaseRef = FirebaseDatabase.getInstance().getReference("reportes")

    // Coordenadas por defecto: Cuenca, Ecuador
    private val coordenadasPorDefecto = LatLng(-2.879623198367579, -78.97463107967702)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_reporte)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val miUbicacion: LatLng = if (location != null) {
                LatLng(location.latitude, location.longitude)
            } else {
                coordenadasPorDefecto
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 15f))


            mMap.addMarker(
                MarkerOptions()
                    .position(miUbicacion)
                    .title("Punto de Inicio")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.zoologia))
            )


            cargarTodosLosReportes()
        }
    }

    private fun cargarTodosLosReportes() {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (reporteSnap in snapshot.children) {
                    val latlngString = reporteSnap.child("ubicacion").getValue(String::class.java)
                    val nombre = reporteSnap.child("nombre").getValue(String::class.java)
                    val descripcion = reporteSnap.child("descripcion").getValue(String::class.java)
                    val fotoUrl = reporteSnap.child("fotoUrl").getValue(String::class.java)

                    if (latlngString != null && latlngString.contains("Lat:")) {
                        val partes = latlngString.replace("Lat:", "").replace("Lng:", "").split(",")
                        val lat = partes[0].trim().toDoubleOrNull()
                        val lng = partes[1].trim().toDoubleOrNull()

                        if (lat != null && lng != null) {
                            val pos = LatLng(lat, lng)

                            // Añadir marcador para cada reporte sin verificar la distancia
                            val marker = mMap.addMarker(
                                MarkerOptions()
                                    .position(pos)
                                    .title(nombre ?: "Animal")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.zoologia))
                            )
                            marker?.tag = Triple(nombre, descripcion, fotoUrl)
                        }
                    }
                }

                mMap.setOnMarkerClickListener { marker ->
                    val info = marker.tag as? Triple<*, *, *>
                    if (info != null) {
                        val nombre = info.first as String
                        val descripcion = info.second as String
                        val fotoUrl = info.third as String
                        MostrarDialogoReporte(nombre, descripcion, fotoUrl)
                    }
                    true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapaReporte, "Error al cargar reportes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun MostrarDialogoReporte(nombre: String, descripcion: String, fotoUrl: String?) {
        val dialog = ReporteDialogFragment.newInstance(nombre, descripcion, fotoUrl)
        dialog.show(supportFragmentManager, "reporteDialog")
    }
}
