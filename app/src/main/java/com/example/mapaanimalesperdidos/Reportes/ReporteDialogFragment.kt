package com.example.mapaanimalesperdidos.Reportes

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.app.AlertDialog
import android.app.Dialog
import android.widget.TextView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.mapaanimalesperdidos.R

class ReporteDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(nombre: String, descripcion: String, fotoUrl: String?): ReporteDialogFragment {
            val fragment = ReporteDialogFragment()
            val args = Bundle().apply {
                putString("nombre", nombre)
                putString("descripcion", descripcion)
                putString("fotoUrl", fotoUrl)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Usar requireArguments para garantizar que los datos estén disponibles
        val nombre = requireArguments().getString("nombre") ?: ""
        val descripcion = requireArguments().getString("descripcion") ?: ""
        val fotoUrl = requireArguments().getString("fotoUrl")

        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_reporte, null)

        val txtNombre = view.findViewById<TextView>(R.id.txtNombre)
        val txtDescripcion = view.findViewById<TextView>(R.id.txtDescripcion)
        val imgReporte = view.findViewById<ImageView>(R.id.imgReporte)

        txtNombre.text = nombre
        txtDescripcion.text = descripcion

        // Cargar la imagen con Glide y manejar errores de carga
        if (!fotoUrl.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(fotoUrl)
                .placeholder(R.drawable.ic_share)  // Imagen mientras se carga
                .error(R.drawable.ic_launcher_foreground)  // Imagen en caso de error
                .into(imgReporte)
        }

        builder.setView(view)
        builder.setPositiveButton("Cerrar", null)

        return builder.create()
    }
}
