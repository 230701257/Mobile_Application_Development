//package com.interviewprep.tracker.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.interviewprep.tracker.data.local.UserPreferencesRepository
//import com.interviewprep.tracker.data.remote.AuthRepository
//import com.interviewprep.tracker.model.AuthState
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class AuthViewModel @Inject constructor(
//    private val authRepository: AuthRepository,
//    private val prefsRepository: UserPreferencesRepository
//) : ViewModel() {
//
//    val authState: StateFlow<AuthState> = authRepository.authStateFlow
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Loading)
//
//    private val _loginError = MutableStateFlow<String?>(null)
//    val loginError: StateFlow<String?> = _loginError.asStateFlow()
//
//    private val _registerError = MutableStateFlow<String?>(null)
//    val registerError: StateFlow<String?> = _registerError.asStateFlow()
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
//
//    fun signIn(email: String, password: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _loginError.value = null
//            val result = authRepository.signIn(email.trim(), password)
//            result.onFailure { _loginError.value = it.message ?: "Login failed" }
//            _isLoading.value = false
//        }
//    }
//
//    fun register(email: String, password: String, displayName: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _registerError.value = null
//            val result = authRepository.register(email.trim(), password, displayName.trim())
//            result.onFailure { _registerError.value = it.message ?: "Registration failed" }
//            _isLoading.value = false
//        }
//    }
//
//    fun signOut() {
//        viewModelScope.launch {
//            authRepository.signOut()
//            prefsRepository.clearAll()
//        }
//    }
//
//    fun clearLoginError() { _loginError.value = null }
//    fun clearRegisterError() { _registerError.value = null }
//}

package com.interviewprep.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interviewprep.tracker.data.local.UserPreferencesRepository
import com.interviewprep.tracker.data.remote.AuthRepository
import com.interviewprep.tracker.model.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> =
        authRepository.authStateFlow
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AuthState.Loading
            )

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError.asStateFlow()

    private val _googleError = MutableStateFlow<String?>(null)
    val googleError: StateFlow<String?> = _googleError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun signIn(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _loginError.value = null

            val result = authRepository.signIn(
                email.trim(),
                password
            )

            result.onFailure {
                _loginError.value =
                    it.message ?: "Login failed"
            }

            _isLoading.value = false
        }
    }

    fun register(
        email: String,
        password: String,
        displayName: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _registerError.value = null

            val result = authRepository.register(
                email.trim(),
                password,
                displayName.trim()
            )

            result.onFailure {
                _registerError.value =
                    it.message ?: "Registration failed"
            }

            _isLoading.value = false
        }
    }

    // GOOGLE SIGN IN
    fun signInWithGoogle(
        idToken: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _googleError.value = null

            val result = authRepository
                .signInWithGoogle(idToken)

            result.onFailure {
                _googleError.value =
                    it.message ?: "Google Sign-In failed"
            }

            _isLoading.value = false
        }
    }

    fun signOut() {

        viewModelScope.launch {

            authRepository.signOut()

            prefsRepository.clearAll()
        }
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    fun clearRegisterError() {
        _registerError.value = null
    }

    fun clearGoogleError() {
        _googleError.value = null
    }
}