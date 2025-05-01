package com.example.mapaanimalesperdidos.Reportes

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.mapaanimalesperdidos.R
import com.example.mapaanimalesperdidos.Ventanas.LoginActivity

import java.util.*

class ReporteActivity : AppCompatActivity() {

    private lateinit var editNombreAnimal: EditText
    private lateinit var editDescripcion: EditText
    private lateinit var editUbicacion: EditText
    private lateinit var imagePreview: ImageView
    private var imagenUriSeleccionada: Uri? = null

    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        obtenerUbicacionActual()


        // Inicialización de Firebase
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        // Si el usuario no está autenticado, lo redirige al login
        if (currentUser == null) {
            Toast.makeText(this, "Debes iniciar sesión para enviar un reporte", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        databaseRef = FirebaseDatabase.getInstance().getReference("reportes")
        storageRef = FirebaseStorage.getInstance().getReference("imagenes_animales")

        editNombreAnimal = findViewById(R.id.editNombreAnimal)
        editDescripcion = findViewById(R.id.editDescripcion)
        editUbicacion = findViewById(R.id.editUbicacion)
        imagePreview = findViewById(R.id.imagePreview)

        val btnSeleccionarFoto = findViewById<Button>(R.id.btnSeleccionarFoto)
        val btnEnviarReporte = findViewById<Button>(R.id.btnEnviarReporte)

        btnSeleccionarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, SELECT_IMAGE_REQUEST)
        }

        btnEnviarReporte.setOnClickListener {
            enviarReporte()
        }
    }
    private fun obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Solicita los permisos si no están concedidos
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitud = location.latitude
                val longitud = location.longitude
                editUbicacion.setText("Lat: $latitud, Lng: $longitud")
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarReporte() {
        val nombre = editNombreAnimal.text.toString().trim()
        val descripcion = editDescripcion.text.toString().trim()
        val ubicacion = editUbicacion.text.toString().trim()

        if (nombre.isEmpty() || descripcion.isEmpty() || ubicacion.isEmpty() || imagenUriSeleccionada == null) {
            Toast.makeText(this, "Completa todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Sesión caducada. Inicia sesión de nuevo.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Subiendo reporte...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val imageName = UUID.randomUUID().toString()
        val imageRef = storageRef.child(imageName)

        imageRef.putFile(imagenUriSeleccionada!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageRef.downloadUrl.addOnSuccessListener { uri: Uri ->
                    val urlImagen = uri.toString()
                    val idReporte = databaseRef.push().key ?: UUID.randomUUID().toString()

                    val datosReporte = hashMapOf(
                        "id" to idReporte,
                        "nombre" to nombre,
                        "descripcion" to descripcion,
                        "ubicacion" to ubicacion,
                        "fotoUrl" to urlImagen,
                        "usuarioId" to user.uid,
                        "timestamp" to System.currentTimeMillis()
                    )

                    databaseRef.child(idReporte).setValue(datosReporte).addOnCompleteListener { task ->
                        progressDialog.dismiss()
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Reporte enviado correctamente", Toast.LENGTH_LONG).show()
                            limpiarCampos()
                        } else {
                            Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error al obtener URL de imagen", Toast.LENGTH_SHORT).show()
                }
            } else {
                progressDialog.dismiss()
                Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun limpiarCampos() {
        editNombreAnimal.setText("")
        editDescripcion.setText("")
        editUbicacion.setText("")
        imagePreview.setImageResource(0)
        imagenUriSeleccionada = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imagenUriSeleccionada = data.data
            imagePreview.setImageURI(imagenUriSeleccionada)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionActual()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val SELECT_IMAGE_REQUEST = 100
    }
}
