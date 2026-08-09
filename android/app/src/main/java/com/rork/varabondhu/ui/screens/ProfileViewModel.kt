package com.rork.varabondhu.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Profile identity, trust summary, and account preferences. */
data class ProfileUiState(
    val name: String = "আরিফ হাসান",
    val location: String = "ঢাকা, বাংলাদেশ",
    val memberSince: String = "১২ জানুয়ারি ২০২৪",
    val trustScore: String = "৪.৮",
    val totalReports: Int = 241,
    val acceptedReports: Int = 216,
    val averageRating: String = "৪.৮",
    val contributorRank: String = "শীর্ষ ১২%",
    val hasNotificationsEnabled: Boolean = true
)

/** Holds editable profile state until account persistence is connected. */
class ProfileViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        val cleanedName: String = name.trim()
        if (cleanedName.length < 2) return
        _uiState.update { state: ProfileUiState -> state.copy(name = cleanedName) }
    }

    fun toggleNotifications() {
        _uiState.update { state: ProfileUiState ->
            state.copy(hasNotificationsEnabled = !state.hasNotificationsEnabled)
        }
    }
}
