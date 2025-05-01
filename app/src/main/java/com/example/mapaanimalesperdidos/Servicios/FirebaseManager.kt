package com.example.mapaanimalesperdidos.Servicios

import com.example.mapaanimalesperdidos.Model.AnimalReport
import com.google.firebase.firestore.FirebaseFirestore
object FirebaseManager {
    private val db = FirebaseFirestore.getInstance()
    private const val COLLECTION = "reportes"

    fun subirReporte(reporte: AnimalReport, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(COLLECTION)
            .add(reporte)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun obtenerReportes(onResult: (List<AnimalReport>) -> Unit) {
        db.collection(COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                val lista = result.mapNotNull { it.toObject(AnimalReport::class.java) }
                onResult(lista)
            }
    }
}
