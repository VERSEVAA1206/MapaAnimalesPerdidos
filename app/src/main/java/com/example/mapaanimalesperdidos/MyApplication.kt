package com.example.mapaanimalesperdidos

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Obtener el ID de cliente desde los recursos
        val clientId = getString(R.string.default_web_client_id)

        // Configuración de GoogleSignInOptions
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)  // Utiliza el ID de cliente correctamente
            .requestEmail()
            .build()

        // Crear el cliente de GoogleSignIn
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }
}
