package com.vishwajeet.securewebbrowserapplication.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.vishwajeet.securewebbrowserapplication.data.model.AuthResultState

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun authenticateWithFirebaseUsingGoogleToken(
        idToken: String,
        onComplete: (AuthResultState) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val email = authResult.user?.email ?: ""
                onComplete(AuthResultState.Success(email))
            }
            .addOnFailureListener { exception ->
                onComplete(AuthResultState.Error(exception.localizedMessage ?: "Firebase Sign-In Failure"))
            }
    }

    fun executeFirebaseSignOut() {
        firebaseAuth.signOut()
    }
    fun isUserLoggedSessionActive(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}