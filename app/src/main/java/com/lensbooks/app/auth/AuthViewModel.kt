package com.lensbooks.app.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        _currentUser.value = auth.currentUser
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Email and password cannot be empty."
            return
        }
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Authentication failed."
                Log.e("AuthViewModel", "Sign in failed", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, fullName: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
            _error.value = "All fields are required to register."
            return
        }
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Registration failed."
                Log.e("AuthViewModel", "Sign up failed", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun signInWithGoogleCredential(credential: AuthCredential, onSuccess: () -> Unit) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential).await()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Google Sign-In failed."
                Log.e("AuthViewModel", "Google Sign-In failed", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    fun handleGoogleSignInResult(
        resultCode: Int,
        data: android.content.Intent?,
        onAuthSuccess: () -> Unit
    ) {
        _loading.value = true
        _error.value = null
        if (resultCode == Activity.RESULT_OK) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
                    signInWithGoogleCredential(credential, onAuthSuccess)
                } else {
                    _error.value = "Google Sign-In failed: ID token is missing."
                    _loading.value = false
                }
            } catch (e: Exception) {
                _loading.value = false
                val apiException = e as? com.google.android.gms.common.api.ApiException
                val statusCode = apiException?.statusCode ?: -1
                _error.value = "Google Sign-In failed: ${e.localizedMessage ?: "Unknown error"} (Code: $statusCode)"
                Log.e("AuthViewModel", "Google Sign-In ApiException (Code: $statusCode):", e)
            }
        } else {
            _loading.value = false
            if (resultCode == Activity.RESULT_CANCELED) {
                _error.value = "Google Sign-In was cancelled by the user."
            } else {
                _error.value = "Google Sign-In failed with result code: $resultCode."
            }
        }
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }
}