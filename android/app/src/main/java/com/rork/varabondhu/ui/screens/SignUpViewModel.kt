package com.rork.varabondhu.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** The two halves of the sign-up form, in order. */
enum class SignUpStep { Basics, Security }

/** Form state for the sign-up screen. */
data class SignUpUiState(
    val step: SignUpStep = SignUpStep.Basics,
    val name: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val hasAcceptedTerms: Boolean = false,
    val termsError: String? = null,
    val isSubmitting: Boolean = false,
    val isRegistered: Boolean = false
)

/**
 * Holds the sign-up form across its two steps. Account creation is not wired to a
 * backend yet, so a valid form resolves after a short simulated round-trip.
 */
class SignUpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onPhoneChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(11)
        _uiState.update { it.copy(phone = digits, phoneError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun onTermsToggle() {
        _uiState.update { it.copy(hasAcceptedTerms = !it.hasAcceptedTerms, termsError = null) }
    }

    /** Validates name and phone, then moves on to the password step. */
    fun continueToSecurity() {
        val current = _uiState.value
        val nameError = validateName(current.name)
        val phoneError = validatePhone(current.phone)

        if (nameError != null || phoneError != null) {
            _uiState.update { it.copy(nameError = nameError, phoneError = phoneError) }
            return
        }

        _uiState.update { it.copy(step = SignUpStep.Security) }
    }

    /** Returns to the first step, keeping everything already typed. */
    fun backToBasics() {
        _uiState.update { it.copy(step = SignUpStep.Basics) }
    }

    fun submit() {
        val current = _uiState.value
        if (current.isSubmitting) return

        val nameError = validateName(current.name)
        val phoneError = validatePhone(current.phone)

        // Should not normally happen, but never register a half-filled account: send the
        // user back to the step that actually holds the problem.
        if (nameError != null || phoneError != null) {
            _uiState.update {
                it.copy(
                    step = SignUpStep.Basics,
                    nameError = nameError,
                    phoneError = phoneError
                )
            }
            return
        }

        val passwordError = validatePassword(current.password)
        val confirmError = when {
            current.confirmPassword.isEmpty() -> "পাসওয়ার্ড আবার দিন"
            current.confirmPassword != current.password -> "পাসওয়ার্ড দুটি মিলছে না"
            else -> null
        }
        val termsError = if (current.hasAcceptedTerms) null else "শর্তাবলীতে সম্মতি দিন"

        if (passwordError != null || confirmError != null || termsError != null) {
            _uiState.update {
                it.copy(
                    passwordError = passwordError,
                    confirmPasswordError = confirmError,
                    termsError = termsError
                )
            }
            return
        }

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            delay(SIMULATED_REQUEST_MILLIS)
            _uiState.update { it.copy(isSubmitting = false, isRegistered = true) }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(isRegistered = false) }
    }

    private fun validateName(name: String): String? =
        if (name.trim().length < 2) "আপনার নাম দিন" else null

    private companion object {
        const val SIMULATED_REQUEST_MILLIS = 850L
    }
}
