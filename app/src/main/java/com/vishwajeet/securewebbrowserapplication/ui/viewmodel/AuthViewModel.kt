package com.vishwajeet.securewebbrowserapplication.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.vishwajeet.securewebbrowserapplication.data.model.AuthResultState
import com.vishwajeet.securewebbrowserapplication.data.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    var authState by mutableStateOf<AuthResultState>(AuthResultState.Idle)
        private set

    fun handleFirebaseGoogleSignIn(idToken: String, onAuthSuccessRedirect: () -> Unit) {
        authState = AuthResultState.Loading
        authRepository.authenticateWithFirebaseUsingGoogleToken(idToken) { resultState ->
            authState = resultState
            if (resultState is AuthResultState.Success) {
                onAuthSuccessRedirect()
            }
        }
    }

    fun logOut(context: Context) {
        authRepository.executeFirebaseSignOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        googleSignInClient.signOut().addOnCompleteListener {
            authState = AuthResultState.Idle
        }
    }

    fun isUserLoggedSessionActive(): Boolean {
        return authRepository.isUserLoggedSessionActive()
    }

}