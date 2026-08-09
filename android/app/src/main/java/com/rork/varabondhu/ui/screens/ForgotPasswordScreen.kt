package com.rork.varabondhu.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.res.painterResource
import com.rork.varabondhu.R
import com.rork.varabondhu.ui.components.AuthGap
import com.rork.varabondhu.ui.components.AuthPage
import com.rork.varabondhu.ui.components.AuthSpacing
import com.rork.varabondhu.ui.components.PrimaryButton
import com.rork.varabondhu.ui.components.VaraTextField
import com.rork.varabondhu.ui.components.authSpacing
import com.rork.varabondhu.ui.components.authSpacingScale
import com.rork.varabondhu.ui.theme.AppTheme
import com.rork.varabondhu.ui.theme.BodyMutedStyle
import com.rork.varabondhu.ui.theme.Ink
import com.rork.varabondhu.ui.theme.PageWhite
import com.rork.varabondhu.ui.theme.TaglineStyle

private val HeaderHeight = 52.dp
private val BackButtonSize = 40.dp
private val TermsRowHeight = 44.dp
private val ButtonHeight = 52.dp

private val GapTop = AuthGap(base = 12, weight = 1f)
private val GapAfterArt = AuthGap(base = 24, weight = 0f)
private val GapAfterHeading = AuthGap(base = 6, weight = 0f)
private val GapAfterSubtitle = AuthGap(base = 24, weight = 0f)
private val GapBeforeTerms = AuthGap(base = 24, weight = 0f)
private val GapBeforeButton = AuthGap(base = 24, weight = 0f)
private val GapBottom = AuthGap(base = 32, weight = 1.5f)

private val ArtMinHeight = 76.dp
private val ArtMaxHeight = 132.dp
private const val ART_FRACTION = 0.15f
private const val ShieldAspect = 560f / 564f

private class ForgotMetrics(
    val spacing: AuthSpacing,
    val artTarget: Dp,
    val airBeforeButton: Dp
)

private fun forgotMetrics(room: Dp): ForgotMetrics {
    val gaps = listOf(
        GapTop, GapAfterArt, GapAfterHeading, GapAfterSubtitle,
        GapBeforeTerms, GapBeforeButton, GapBottom
    )
    val scale = authSpacingScale(room)
    val spacing = authSpacing(gaps, room, scale, maxExtra = 6.dp)

    val formBlocks = spacing.total(listOf(GapAfterHeading, GapAfterSubtitle)) +
            26.dp + 19.dp + // Text lines
            54.dp + // Phone input
            spacing[GapBeforeTerms] + TermsRowHeight

    val artTarget = (room * ART_FRACTION).coerceIn(ArtMinHeight, ArtMaxHeight)
    val airBeforeButton = (room - spacing.total(gaps) - formBlocks - ButtonHeight - artTarget).coerceAtLeast(0.dp)

    return ForgotMetrics(
        spacing = spacing,
        artTarget = artTarget,
        airBeforeButton = airBeforeButton
    )
}

private fun tailBelowButton(metrics: ForgotMetrics): Dp {
    return metrics.spacing[GapBottom]
}

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onNavigateToOtp: (String) -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageWhite)
    ) {
        AuthPage(
            padTop = true,
            headerHeight = HeaderHeight,
            keyboardTail = { _, height -> tailBelowButton(forgotMetrics(height)) },
            header = { ForgotHeader(onBack = onNavigateBack) }
        ) { _, pageHeight ->
            val metrics = forgotMetrics(pageHeight)
            val spacing = metrics.spacing

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(spacing[GapTop]))

                ShieldIllustration(
                    target = metrics.artTarget,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(spacing[GapAfterArt]))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "পাসওয়ার্ড ভুলে গেছেন?",
                        style = TaglineStyle.copy(
                            fontSize = 18.sp,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(spacing[GapAfterHeading]))

                    Text(
                        text = "আপনার রেজিস্টার করা মোবাইল নম্বরটি দিন",
                        style = BodyMutedStyle.copy(fontSize = 13.sp, lineHeight = 19.sp),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(spacing[GapAfterSubtitle]))

                    VaraTextField(
                        value = uiState.phone,
                        onValueChange = viewModel::onPhoneChange,
                        placeholder = "মোবাইল নম্বর",
                        leadingIcon = Icons.Outlined.PhoneInTalk,
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done,
                        errorText = uiState.phoneError
                    )

                    Spacer(modifier = Modifier.height(spacing[GapBeforeTerms]))

                    Spacer(modifier = Modifier.height(TermsRowHeight)) // Placeholder so form matches SignUp height

                    Spacer(modifier = Modifier.height(metrics.airBeforeButton + spacing[GapBeforeButton]))

                    PrimaryButton(
                        label = "পরের ধাপ",
                        onClick = {
                            if (viewModel.submit()) {
                                onNavigateToOtp(uiState.phone)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(spacing[GapBottom]))
            }
        }
    }
}

@Composable
private fun ForgotHeader(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(HeaderHeight)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(BackButtonSize)) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "পিছনে যান",
                tint = Ink
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "পাসওয়ার্ড রিকভারি",
            style = TaglineStyle.copy(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1
        )
    }
}

@Preview(name = "ForgotPassword · normal", showBackground = true, widthDp = 393, heightDp = 851)
@Composable
private fun ForgotPasswordScreenPreview() {
    AppTheme {
        ForgotPasswordScreen(onNavigateBack = {}, onNavigateToOtp = {})
    }
}

@Preview(name = "ForgotPassword · small", showBackground = true, widthDp = 320, heightDp = 600)
@Composable
private fun ForgotPasswordScreenSmallPreview() {
    AppTheme {
        ForgotPasswordScreen(onNavigateBack = {}, onNavigateToOtp = {})
    }
}

@Composable
private fun ShieldIllustration(target: Dp, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val height = minOf(target, maxHeight)
        if (height >= ArtMinVisible) {
            Image(
                painter = painterResource(R.drawable.signup_shield),
                contentDescription = null,
                modifier = Modifier
                    .height(height)
                    .aspectRatio(ShieldAspect)
            )
        }
    }
}

private val ArtMinVisible = 64.dp
