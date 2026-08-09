package com.rork.varabondhu.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ForgotPasswordUiState(
    val phone: String = "",
    val phoneError: String? = null
)

class ForgotPasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun onPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(11)
        _uiState.update { it.copy(phone = digitsOnly, phoneError = null) }
    }

    fun submit(): Boolean {
        val state = _uiState.value
        return if (state.phone.length < 11 || !state.phone.startsWith("01")) {
            _uiState.update { it.copy(phoneError = "সঠিক মোবাইল নম্বর দিন") }
            false
        } else {
            true
        }
    }
}
