package com.rork.varabondhu.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Form state for the OTP verification screen. */
data class OtpUiState(
    val phone: String = "+880 1XXX-XXX123",
    val otpDigits: List<String> = List(6) { "" },
    val timerSeconds: Int = 45,
    val isResendAvailable: Boolean = false,
    val isVerifying: Boolean = false,
    val isVerified: Boolean = false,
    val errorMessage: String? = null
)

class OtpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startResendTimer()
    }

    fun setPhone(rawPhone: String) {
        val formattedPhone = formatPhone(rawPhone)
        _uiState.update { it.copy(phone = formattedPhone) }
    }

    fun onDigitChange(index: Int, value: String) {
        val digit = value.filter { it.isDigit() }.takeLast(1)
        val currentDigits = _uiState.value.otpDigits.toMutableList()
        currentDigits[index] = digit
        _uiState.update { 
            it.copy(
                otpDigits = currentDigits,
                errorMessage = null
            ) 
        }
    }

    fun startResendTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerSeconds = 45, isResendAvailable = false) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerSeconds > 0) {
                delay(1000L)
                _uiState.update { it.copy(timerSeconds = it.timerSeconds - 1) }
            }
            _uiState.update { it.copy(isResendAvailable = true) }
        }
    }

    fun resendOtp() {
        if (!_uiState.value.isResendAvailable) return
        startResendTimer()
    }

    fun verify() {
        val current = _uiState.value
        if (current.isVerifying) return

        val code = current.otpDigits.joinToString("")
        if (code.length < 6) {
            _uiState.update { it.copy(errorMessage = "৬ ডিজিটের পুরো OTP কোডটি লিখুন") }
            return
        }

        _uiState.update { it.copy(isVerifying = true, errorMessage = null) }
        viewModelScope.launch {
            delay(SIMULATED_VERIFY_MILLIS)
            _uiState.update { it.copy(isVerifying = false, isVerified = true) }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(isVerified = false) }
    }

    private fun formatPhone(raw: String): String {
        if (raw.isBlank()) return "+880 1XXX-XXX123"
        val digits = raw.filter { it.isDigit() }
        return if (digits.length == 11 && digits.startsWith("01")) {
            "+880 ${digits.substring(0, 4)}-${digits.substring(4, 7)}${digits.substring(7)}"
        } else {
            raw
        }
    }

    private companion object {
        const val SIMULATED_VERIFY_MILLIS = 800L
    }
}
