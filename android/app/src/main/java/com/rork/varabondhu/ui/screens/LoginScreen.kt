package com.rork.varabondhu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rork.varabondhu.R
import com.rork.varabondhu.ui.components.AuthFooterLink
import com.rork.varabondhu.ui.components.AuthGap
import com.rork.varabondhu.ui.components.AuthPage
import com.rork.varabondhu.ui.components.AuthSpacing
import com.rork.varabondhu.ui.components.CityHero
import com.rork.varabondhu.ui.components.ControlHeight
import com.rork.varabondhu.ui.components.FieldHeight
import com.rork.varabondhu.ui.components.HeroSheetLayout
import com.rork.varabondhu.ui.components.LabeledDivider
import com.rork.varabondhu.ui.components.PrimaryButton
import com.rork.varabondhu.ui.components.SocialButton
import com.rork.varabondhu.ui.components.SocialHeight
import com.rork.varabondhu.ui.components.VaraTextField
import com.rork.varabondhu.ui.components.authSpacing
import com.rork.varabondhu.ui.components.authSpacingScale
import com.rork.varabondhu.ui.theme.AppTheme
import com.rork.varabondhu.ui.theme.CardWhite
import com.rork.varabondhu.ui.theme.HeadingStyle
import com.rork.varabondhu.ui.theme.LinkStyle

private val SheetShape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)

/** How far the white form sheet rides up over the road in the artwork. */
private val SheetOverlap = 18.dp

/**
 * Below this much page height the social sign-in block stands down: it is the only
 * optional part of the form, and dropping it is what keeps the fields, the button and
 * the footer on one page on a genuinely small device. The keyboard does not affect this.
 */
private val SocialBlockMinHeight = 540.dp

/**
 * The artwork owns roughly the upper third of a regular phone. CityHero adds matching sky
 * above the full-width image when needed, so the vehicles stay intact instead of stretching.
 */
private const val HERO_FRACTION = 0.32f
private const val HERO_MAX_FRACTION = 0.34f
private val HeroMinHeight = 132.dp

/** Fixed element heights inside the sheet — kept in sync with [LoginSheet]. */
private val HeadingHeight = 36.dp
private val ForgotLinkHeight = 22.dp
private val DividerHeight = 22.dp
private val FooterHeight = 38.dp

// Never stretch this gap: it is the distance users read as artwork → welcome.
private val GapTop = AuthGap(base = 18, weight = 0f)
private val GapAfterHeading = AuthGap(base = 20, weight = 1.1f)
private val GapBetweenFields = AuthGap(base = 14, weight = 0.4f)
private val GapAfterFields = AuthGap(base = 12, weight = 0.25f)
private val GapBeforeButton = AuthGap(base = 20, weight = 1f)
private val GapBeforeDivider = AuthGap(base = 20, weight = 1f)
private val GapAfterDivider = AuthGap(base = 18, weight = 0.8f)
private val GapBeforeFooter = AuthGap(base = 18, weight = 1.2f)
private val GapBottom = AuthGap(base = 8, weight = 0.2f)

private val SocialSheetGaps = listOf(
    GapTop,
    GapAfterHeading,
    GapBetweenFields,
    GapAfterFields,
    GapBeforeButton,
    GapBeforeDivider,
    GapAfterDivider,
    GapBeforeFooter,
    GapBottom
)

private val LeanSheetGaps = listOf(
    GapTop,
    GapAfterHeading,
    GapBetweenFields,
    GapAfterFields,
    GapBeforeButton,
    GapBeforeFooter,
    GapBottom
)

/**
 * Login screen. The white form sheet is pinned to the bottom and the city illustration
 * takes roughly the upper third of a regular phone. The sheet-to-welcome gap is deliberately
 * fixed so spare height can never turn it into a blank block. Opening the keyboard re-flows
 * nothing: it covers the artwork, and the page can be pulled up only to the sign-in button.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            viewModel.onNavigationHandled()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CardWhite)
    ) {
        AuthPage(
            keyboardTail = { _, height -> tailBelowButton(loginMetrics(height)) }
        ) { _, pageHeight ->
            val metrics = loginMetrics(pageHeight)

            HeroSheetLayout(
                overlap = SheetOverlap,
                modifier = Modifier.fillMaxSize(),
                hero = { heroHeight -> CityHero(height = heroHeight) },
                sheet = {
                    LoginSheet(
                        uiState = uiState,
                        spacing = metrics.spacing,
                        showSocial = metrics.showSocial,
                        onPhoneChange = viewModel::onPhoneChange,
                        onPasswordChange = viewModel::onPasswordChange,
                        // Temporary prototype shortcut: test Home without entering credentials.
                        onSubmit = onLoginSuccess,
                        onSocialClick = onLoginSuccess,
                        onNavigateToSignUp = onNavigateToSignUp,
                        onNavigateToForgotPassword = onNavigateToForgotPassword
                    )
                }
            )
        }
    }
}

@Composable
private fun LoginSheet(
    uiState: LoginUiState,
    spacing: AuthSpacing,
    showSocial: Boolean,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSocialClick: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardWhite,
        shape = SheetShape,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(spacing[GapTop]))

            Text(text = "স্বাগতম!", style = HeadingStyle)

            Spacer(modifier = Modifier.height(spacing[GapAfterHeading]))

            VaraTextField(
                value = uiState.phone,
                onValueChange = onPhoneChange,
                placeholder = "মোবাইল নম্বর দিন",
                leadingIcon = Icons.Outlined.PhoneInTalk,
                keyboardType = KeyboardType.Phone,
                errorText = uiState.phoneError
            )

            Spacer(modifier = Modifier.height(spacing[GapBetweenFields]))

            VaraTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                placeholder = "পাসওয়ার্ড দিন",
                leadingIcon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                isPassword = true,
                errorText = uiState.passwordError
            )

            Spacer(modifier = Modifier.height(spacing[GapAfterFields]))

            Text(
                text = "পাসওয়ার্ড ভুলে গেছেন?",
                style = LinkStyle.copy(fontSize = 13.sp),
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToForgotPassword() }
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(spacing[GapBeforeButton]))

            PrimaryButton(
                label = "লগইন করুন",
                onClick = onSubmit,
                isLoading = uiState.isSubmitting
            )

            if (showSocial) {
                Spacer(modifier = Modifier.height(spacing[GapBeforeDivider]))

                LabeledDivider(label = "অথবা")

                Spacer(modifier = Modifier.height(spacing[GapAfterDivider]))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SocialButton(
                        label = "Google",
                        iconRes = R.drawable.ic_google,
                        onClick = onSocialClick,
                        modifier = Modifier.weight(1f)
                    )
                    SocialButton(
                        label = "Facebook",
                        iconRes = R.drawable.ic_facebook,
                        onClick = onSocialClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing[GapBeforeFooter]))

            AuthFooterLink(
                leadingText = "অ্যাকাউন্ট নেই? ",
                linkText = "সাইন আপ করুন",
                onClick = onNavigateToSignUp
            )

            Spacer(modifier = Modifier.height(spacing[GapBottom]))
        }
    }
}

/** Everything the login page needs to lay itself out at a given size. */
private class LoginMetrics(val spacing: AuthSpacing, val showSocial: Boolean)

/**
 * Gives the artwork roughly the upper third of the page and spreads the remainder through
 * the form. The first sheet gap has no weight, so it always stays compact. On short screens
 * the bottom-pinned sheet still wins and the artwork yields enough room for every control.
 */
private fun loginMetrics(pageHeight: Dp): LoginMetrics {
    val showSocial = pageHeight >= SocialBlockMinHeight
    val fixed = HeadingHeight + FieldHeight * 2 + ForgotLinkHeight + ControlHeight +
        FooterHeight + if (showSocial) DividerHeight + SocialHeight else 0.dp
    val gaps = if (showSocial) SocialSheetGaps else LeanSheetGaps

    val heroCap = pageHeight * HERO_MAX_FRACTION
    val heroTarget = (pageHeight * HERO_FRACTION)
        .coerceAtLeast(HeroMinHeight)
        .coerceAtMost(heroCap)

    val room = pageHeight - heroTarget + SheetOverlap - fixed
    return LoginMetrics(
        spacing = authSpacing(gaps, room, authSpacingScale(pageHeight)),
        showSocial = showSocial
    )
}

/**
 * Height of the block below the sign-in button. This is what may hide behind the
 * keyboard, and therefore how far the page is allowed to be pulled up.
 */
private fun tailBelowButton(metrics: LoginMetrics): Dp {
    val spacing = metrics.spacing
    val socialBlock = if (metrics.showSocial) {
        spacing[GapBeforeDivider] + DividerHeight + spacing[GapAfterDivider] + SocialHeight
    } else {
        0.dp
    }
    return socialBlock + spacing[GapBeforeFooter] + FooterHeight + spacing[GapBottom]
}

@Preview(name = "Login · normal", showBackground = true, widthDp = 393, heightDp = 851)
@Composable
private fun LoginScreenPreview() {
    AppTheme {
        LoginScreen(onLoginSuccess = {}, onNavigateToSignUp = {}, onNavigateToForgotPassword = {})
    }
}

@Preview(name = "Login · small", showBackground = true, widthDp = 320, heightDp = 600)
@Composable
private fun LoginScreenSmallPreview() {
    AppTheme {
        LoginScreen(onLoginSuccess = {}, onNavigateToSignUp = {}, onNavigateToForgotPassword = {})
    }
}

@Preview(name = "Login · large", showBackground = true, widthDp = 480, heightDp = 1024)
@Composable
private fun LoginScreenLargePreview() {
    AppTheme {
        LoginScreen(onLoginSuccess = {}, onNavigateToSignUp = {}, onNavigateToForgotPassword = {})
    }
}
