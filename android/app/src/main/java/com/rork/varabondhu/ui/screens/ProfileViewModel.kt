package com.rork.varabondhu.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** A recent fare report made by the signed-in user. */
data class ProfileContribution(
    val id: String,
    val route: String,
    val vehicle: String,
    val fare: String,
    val date: String,
    val isVerified: Boolean
)

/** A frequently used route kept for quick access. */
data class ProfileSavedRoute(
    val id: String,
    val origin: String,
    val destination: String
)

/** Profile details and preferences shown in the profile experience. */
data class ProfileUiState(
    val name: String = "আরিফ হাসান",
    val phone: String = "০১৭১২ ••• •••",
    val hasNotificationsEnabled: Boolean = true,
    val contributionCount: Int = 12,
    val verifiedCount: Int = 9,
    val helpedCount: Int = 430,
    val contributions: List<ProfileContribution> = listOf(
        ProfileContribution(
            id = "contribution-mirpur-farmgate",
            route = "মিরপুর ১০ → ফার্মগেট",
            vehicle = "রিকশা",
            fare = "৳ ৭০",
            date = "আজ, সকাল ৯:২০",
            isVerified = true
        ),
        ProfileContribution(
            id = "contribution-dhanmondi-kalabagan",
            route = "ধানমন্ডি ২৭ → কলাবাগান",
            vehicle = "সিএনজি",
            fare = "৳ ১২০",
            date = "গতকাল, সন্ধ্যা ৬:১০",
            isVerified = false
        )
    ),
    val savedRoutes: List<ProfileSavedRoute> = listOf(
        ProfileSavedRoute("route-home-office", "মিরপুর ১০", "ফার্মগেট"),
        ProfileSavedRoute("route-university", "মোহাম্মদপুর", "ধানমন্ডি ৩২")
    )
)

/** Holds editable profile state until account persistence is connected. */
class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        val cleanedName = name.trim()
        if (cleanedName.length < 2) return
        _uiState.update { state: ProfileUiState -> state.copy(name = cleanedName) }
    }

    fun toggleNotifications() {
        _uiState.update { state: ProfileUiState ->
            state.copy(hasNotificationsEnabled = !state.hasNotificationsEnabled)
        }
    }

    fun removeSavedRoute(routeId: String) {
        _uiState.update { state: ProfileUiState ->
            state.copy(savedRoutes = state.savedRoutes.filterNot { route -> route.id == routeId })
        }
    }
}
