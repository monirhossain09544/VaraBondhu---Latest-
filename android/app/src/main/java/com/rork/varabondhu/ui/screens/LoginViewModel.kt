package com.rork.varabondhu.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Form state for the login screen. */
data class LoginUiState(
    val phone: String = "",
    val password: String = "",
    val phoneError: String? = null,
    val passwordError: String? = null,
    val isSubmitting: Boolean = false,
    val isLoggedIn: Boolean = false
)

/**
 * Holds the login form. Authentication is not wired to a backend yet, so a valid
 * form simply resolves after a short simulated round-trip.
 */
class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPhoneChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(11)
        _uiState.update { it.copy(phone = digits, phoneError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun submit() {
        val current = _uiState.value
        if (current.isSubmitting) return

        val phoneError = validatePhone(current.phone)
        val passwordError = validatePassword(current.password)
        if (phoneError != null || passwordError != null) {
            _uiState.update { it.copy(phoneError = phoneError, passwordError = passwordError) }
            return
        }

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            delay(SIMULATED_REQUEST_MILLIS)
            _uiState.update { it.copy(isSubmitting = false, isLoggedIn = true) }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(isLoggedIn = false) }
    }

    private companion object {
        const val SIMULATED_REQUEST_MILLIS = 750L
    }
}

/** Bangladeshi mobile numbers are 11 digits and start with `01`. */
fun validatePhone(phone: String): String? = when {
    phone.isBlank() -> "মোবাইল নম্বর দিন"
    phone.length != 11 || !phone.startsWith("01") -> "১১ ডিজিটের সঠিক নম্বর দিন"
    else -> null
}

fun validatePassword(password: String): String? = when {
    password.isEmpty() -> "পাসওয়ার্ড দিন"
    password.length < 6 -> "কমপক্ষে ৬ অক্ষরের পাসওয়ার্ড দিন"
    else -> null
}
