package com.rork.varabondhu.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rork.varabondhu.location.LocationTarget
import com.rork.varabondhu.location.LocationViewModel
import com.rork.varabondhu.ui.localization.AppLanguage
import com.rork.varabondhu.ui.screens.FareResultScreen
import com.rork.varabondhu.ui.screens.ForgotPasswordScreen
import com.rork.varabondhu.ui.screens.HomeScreen
import com.rork.varabondhu.ui.screens.LocationPickerScreen
import com.rork.varabondhu.ui.screens.LanguageSelectionScreen
import com.rork.varabondhu.ui.screens.LoginScreen
import com.rork.varabondhu.ui.screens.OtpVerificationScreen
import com.rork.varabondhu.ui.screens.PrivacyPolicyScreen
import com.rork.varabondhu.ui.screens.ProfileScreen
import com.rork.varabondhu.ui.screens.SignUpScreen
import com.rork.varabondhu.ui.screens.SplashScreen
import com.rork.varabondhu.ui.screens.VaraDinScreen


private object Route {
    const val SPLASH = "splash"
    const val LANGUAGE = "language"
    const val LANGUAGE_SETTINGS = "languageSettings"
    const val LOGIN = "login"
    const val SIGN_UP = "signUp"
    const val FORGOT_PASSWORD = "forgotPassword"
    const val OTP = "otp/{phone}"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val PRIVACY_POLICY = "privacyPolicy"
    const val VARA_DIN = "varaDin"
    const val FARE_RESULT = "fareResult"
    const val LOCATION_PICKER = "locationPicker/{target}"

    fun otp(phone: String): String = "otp/$phone"
    fun locationPicker(target: LocationTarget): String = "locationPicker/${target.routeValue}"
}

private const val TRANSITION_MILLIS = 300

@Composable
fun AppNavigation(
    language: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    val navController = rememberNavController()
    val locationViewModel: LocationViewModel = viewModel()
    val locationState by locationViewModel.uiState.collectAsStateWithLifecycle()
    val openLocationPicker: (LocationTarget) -> Unit = { target: LocationTarget ->
        locationViewModel.beginSelection(target)
        navController.navigate(Route.locationPicker(target)) { launchSingleTop = true }
    }

    NavHost(
        navController = navController,
        startDestination = Route.SPLASH,
        enterTransition = { fadeIn(animationSpec = tween(TRANSITION_MILLIS)) },
        exitTransition = { fadeOut(animationSpec = tween(TRANSITION_MILLIS)) }
    ) {
        composable(Route.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Route.LANGUAGE) {
                        popUpTo(Route.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.LANGUAGE) {
            LanguageSelectionScreen(
                selectedLanguage = language,
                onSelectLanguage = onLanguageSelected,
                onContinue = {
                    navController.navigate(Route.LOGIN) {
                        popUpTo(Route.LANGUAGE) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.LANGUAGE_SETTINGS) {
            LanguageSelectionScreen(
                selectedLanguage = language,
                onSelectLanguage = onLanguageSelected,
                onContinue = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.LOGIN,
            enterTransition = { fadeIn(animationSpec = tween(TRANSITION_MILLIS)) },
            exitTransition = { fadeOut(animationSpec = tween(TRANSITION_MILLIS)) }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.HOME) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Route.SIGN_UP) { launchSingleTop = true }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Route.FORGOT_PASSWORD) { launchSingleTop = true }
                }
            )
        }

        composable(
            route = Route.SIGN_UP,
            enterTransition = {
                slideInHorizontally(animationSpec = tween(TRANSITION_MILLIS)) { it / 3 } +
                    fadeIn(animationSpec = tween(TRANSITION_MILLIS))
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(TRANSITION_MILLIS)) { it / 3 } +
                    fadeOut(animationSpec = tween(TRANSITION_MILLIS))
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = tween(TRANSITION_MILLIS)) { it / 3 } +
                    fadeOut(animationSpec = tween(TRANSITION_MILLIS))
            }
        ) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Route.OTP) {
                        launchSingleTop = true
                    }
                },
                onNavigateToLogin = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Route.LOGIN) { launchSingleTop = true }
                    }
                }
            )
        }

        composable(
            route = Route.FORGOT_PASSWORD,
            enterTransition = {
                slideInHorizontally(animationSpec = tween(TRANSITION_MILLIS)) { it / 3 } +
                    fadeIn(animationSpec = tween(TRANSITION_MILLIS))
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(TRANSITION_MILLIS)) { it / 3 } +
                    fadeOut(animationSpec = tween(TRANSITION_MILLIS))
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = tween(TRANSITION_MILLIS)) { it / 3 } +
                    fadeOut(animationSpec = tween(TRANSITION_MILLIS))
            }
        ) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOtp = { phone ->
                    navController.navigate(Route.otp(phone)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Route.OTP,
            enterTransition = {
                slideInHorizontally(animationSpec = tween(TRANSITION_MILLIS)) { it / 3 } +
                    fadeIn(animationSpec = tween(TRANSITION_MILLIS))
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(TRANSITION_MILLIS)) { it / 3 } +
                    fadeOut(animationSpec = tween(TRANSITION_MILLIS))
            }
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: "+880 1XXX-XXX123"
            OtpVerificationScreen(
                phone = phone,
                onVerifySuccess = {
                    navController.navigate(Route.HOME) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Route.HOME) {
            HomeScreen(
                origin = locationState.origin.displayName,
                destination = locationState.destination.displayName,
                onOriginClick = { openLocationPicker(LocationTarget.ORIGIN) },
                onDestinationClick = { openLocationPicker(LocationTarget.DESTINATION) },
                onSwapLocations = locationViewModel::swapRoute,
                onNavigateToVaraDin = { navController.navigate(Route.VARA_DIN) { launchSingleTop = true } },
                onNavigateToFareResult = { navController.navigate(Route.FARE_RESULT) { launchSingleTop = true } },
                onNavigateToProfile = { navController.navigate(Route.PROFILE) { launchSingleTop = true } }
            )
        }

        composable(Route.PROFILE) {
            ProfileScreen(
                onNavigateHome = {
                    navController.popBackStack(Route.HOME, inclusive = false)
                },
                onNavigateToVaraDin = {
                    navController.navigate(Route.VARA_DIN) { launchSingleTop = true }
                },
                onNavigateToPrivacyPolicy = {
                    navController.navigate(Route.PRIVACY_POLICY) { launchSingleTop = true }
                },
                onNavigateToLanguage = {
                    navController.navigate(Route.LANGUAGE_SETTINGS) { launchSingleTop = true }
                },
                onLogout = {
                    navController.navigate(Route.LOGIN) {
                        popUpTo(Route.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Route.PRIVACY_POLICY,
            enterTransition = {
                slideInHorizontally(animationSpec = tween(TRANSITION_MILLIS)) { it / 3 } +
                    fadeIn(animationSpec = tween(TRANSITION_MILLIS))
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = tween(TRANSITION_MILLIS)) { it / 3 } +
                    fadeOut(animationSpec = tween(TRANSITION_MILLIS))
            }
        ) {
            PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Route.VARA_DIN) {
            VaraDinScreen(
                onNavigateBack = { navController.popBackStack() },
                origin = locationState.origin.address.ifBlank { locationState.origin.displayName },
                destination = locationState.destination.address.ifBlank { locationState.destination.displayName },
                onOriginClick = { openLocationPicker(LocationTarget.ORIGIN) },
                onDestinationClick = { openLocationPicker(LocationTarget.DESTINATION) }
            )
        }

        composable(Route.FARE_RESULT) {
            FareResultScreen(
                onNavigateBack = { navController.popBackStack() },
                origin = locationState.origin,
                destination = locationState.destination,
                onSwapLocations = locationViewModel::swapRoute
            )
        }

        composable(Route.LOCATION_PICKER) { backStackEntry ->
            val target = LocationTarget.fromRouteValue(backStackEntry.arguments?.getString("target"))
            LaunchedEffect(target) {
                if (target != locationState.activeTarget) locationViewModel.beginSelection(target)
            }
            LocationPickerScreen(
                state = locationState,
                onQueryChange = locationViewModel::updateQuery,
                onSuggestionSelected = locationViewModel::selectSuggestion,
                onPlaceSelected = locationViewModel::selectPlace,
                onMapPointSelected = locationViewModel::previewMapPoint,
                onLoadNearbyPlaces = locationViewModel::loadNearbyPlaces,
                onUseCurrentLocation = locationViewModel::useCurrentLocation,
                onConfirmMapSelection = locationViewModel::confirmPendingPlace,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
