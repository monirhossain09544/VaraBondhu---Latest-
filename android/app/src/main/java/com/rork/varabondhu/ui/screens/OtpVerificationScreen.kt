package com.rork.varabondhu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
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
import com.rork.varabondhu.ui.components.authSpacing
import com.rork.varabondhu.ui.components.authSpacingScale
import com.rork.varabondhu.ui.theme.AppTheme
import com.rork.varabondhu.ui.theme.BanglaFamily
import com.rork.varabondhu.ui.theme.BrandGreen
import com.rork.varabondhu.ui.theme.CardWhite
import com.rork.varabondhu.ui.theme.ErrorStyle
import com.rork.varabondhu.ui.theme.FieldBorder
import com.rork.varabondhu.ui.theme.Ink
import com.rork.varabondhu.ui.theme.InkMuted
import com.rork.varabondhu.ui.theme.PageWhite
import com.rork.varabondhu.ui.theme.TaglineStyle

private val HeaderHeight = 52.dp
private val BackButtonSize = 40.dp
private val ButtonHeight = 52.dp

private val GapTop = AuthGap(base = 12, weight = 1f)
private val GapAfterArt = AuthGap(base = 24, weight = 0f)
private val GapAfterHeading = AuthGap(base = 6, weight = 0f)
private val GapAfterPhone = AuthGap(base = 32, weight = 0f)
private val GapAfterOtp = AuthGap(base = 28, weight = 0f)
private val GapBeforeButton = AuthGap(base = 24, weight = 0f)
private val GapBottom = AuthGap(base = 32, weight = 1.5f)

private val ArtMinHeight = 76.dp
private val ArtMaxHeight = 132.dp
private const val ART_FRACTION = 0.15f
private const val ShieldAspect = 560f / 564f

private class OtpMetrics(
    val spacing: AuthSpacing,
    val artTarget: Dp,
    val airBeforeButton: Dp
)

private fun otpMetrics(room: Dp): OtpMetrics {
    val gaps = listOf(
        GapTop, GapAfterArt, GapAfterHeading, GapAfterPhone,
        GapAfterOtp, GapBeforeButton, GapBottom
    )
    val scale = authSpacingScale(room)
    val spacing = authSpacing(gaps, room, scale, maxExtra = 6.dp)

    val formBlocks = spacing.total(listOf(GapAfterHeading, GapAfterPhone, GapAfterOtp)) +
            19.dp + 22.dp + // Subtitle and Phone Text
            54.dp + // OTP row height
            19.dp // Timer height

    val artTarget = (room * ART_FRACTION).coerceIn(ArtMinHeight, ArtMaxHeight)
    val airBeforeButton = (room - spacing.total(gaps) - formBlocks - ButtonHeight - artTarget).coerceAtLeast(0.dp)

    return OtpMetrics(
        spacing = spacing,
        artTarget = artTarget,
        airBeforeButton = airBeforeButton
    )
}

private fun tailBelowButton(metrics: OtpMetrics): Dp {
    return metrics.spacing[GapBottom]
}

@Composable
fun OtpVerificationScreen(
    phone: String = "+880 1XXX-XXX123",
    onVerifySuccess: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: OtpViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(phone) {
        viewModel.setPhone(phone)
    }

    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified) {
            viewModel.onNavigationHandled()
            onVerifySuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageWhite)
    ) {
        AuthPage(
            padTop = true,
            headerHeight = HeaderHeight,
            keyboardTail = { _, height -> tailBelowButton(otpMetrics(height)) },
            header = { OtpHeader(onBack = onBackClick) }
        ) { _, pageHeight ->
            val metrics = otpMetrics(pageHeight)
            val spacing = metrics.spacing

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(spacing[GapTop]))

                ShieldIllustration(
                    target = metrics.artTarget,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(spacing[GapAfterArt]))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "আপনার মোবাইলে একটি OTP পাঠানো হয়েছে",
                        style = TaglineStyle.copy(
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Normal,
                            color = InkMuted
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(spacing[GapAfterHeading]))

                    Text(
                        text = uiState.phone,
                        style = TaglineStyle.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGreen
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(spacing[GapAfterPhone]))

                    OtpInputRow(
                        digits = uiState.otpDigits,
                        onDigitChange = { index, value ->
                            viewModel.onDigitChange(index, value)
                        },
                        onCompleted = {
                            focusManager.clearFocus()
                            viewModel.verify()
                        }
                    )

                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = uiState.errorMessage!!,
                            style = ErrorStyle,
                            fontFamily = BanglaFamily,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing[GapAfterOtp]))

                    ResendTimerRow(
                        timerSeconds = uiState.timerSeconds,
                        isResendAvailable = uiState.isResendAvailable,
                        onResendClick = viewModel::resendOtp
                    )

                    Spacer(modifier = Modifier.height(metrics.airBeforeButton + spacing[GapBeforeButton]))

                    PrimaryButton(
                        label = "ভেরিফাই করুন",
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.verify()
                        },
                        isLoading = uiState.isVerifying
                    )
                }

                Spacer(modifier = Modifier.height(spacing[GapBottom]))
            }
        }
    }
}

@Composable
private fun OtpHeader(onBack: () -> Unit, modifier: Modifier = Modifier) {
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
            text = "ভেরিফিকেশন করুন",
            style = TaglineStyle.copy(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun OtpInputRow(
    digits: List<String>,
    onDigitChange: (Int, String) -> Unit,
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 6) {
            val isFocused = remember { androidx.compose.runtime.mutableStateOf(false) }
            val digit = digits.getOrElse(i) { "" }

            Box(
                modifier = Modifier
                    .width(46.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (isFocused.value || digit.isNotEmpty()) 1.8.dp else 1.dp,
                        color = if (isFocused.value || digit.isNotEmpty()) BrandGreen else FieldBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(CardWhite),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = digit,
                    onValueChange = { newValue ->
                        if (newValue.length <= 1) {
                            onDigitChange(i, newValue)
                            if (newValue.isNotEmpty() && i < 5) {
                                focusRequesters[i + 1].requestFocus()
                            }
                            if (digits.mapIndexed { idx, s -> if (idx == i) newValue else s }.joinToString("").length == 6) {
                                onCompleted()
                            }
                        }
                    },
                    modifier = Modifier
                        .focusRequester(focusRequesters[i])
                        .onFocusChanged { isFocused.value = it.isFocused }
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.key == Key.Backspace && digit.isEmpty() && i > 0) {
                                focusRequesters[i - 1].requestFocus()
                                true
                            } else {
                                false
                            }
                        },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = BanglaFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = SolidColor(BrandGreen),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = if (i == 5) ImeAction.Done else ImeAction.Next
                    )
                )
            }
        }
    }
}

@Composable
private fun ResendTimerRow(
    timerSeconds: Int,
    isResendAvailable: Boolean,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedTime = String.format("%02d:%02d", timerSeconds / 60, timerSeconds % 60)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "OTP পাননি? ",
            fontFamily = BanglaFamily,
            fontSize = 14.sp,
            color = InkMuted
        )

        if (!isResendAvailable) {
            Text(
                text = "$formattedTime সেকেন্ড পর ",
                fontFamily = BanglaFamily,
                fontSize = 14.sp,
                color = InkMuted
            )
        }

        Text(
            text = "আবার পাঠান",
            fontFamily = BanglaFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (isResendAvailable) BrandGreen else BrandGreen.copy(alpha = 0.5f),
            modifier = Modifier.clickable(enabled = isResendAvailable) {
                onResendClick()
            }
        )
    }
}

@Preview(name = "OtpVerification · normal", showBackground = true, widthDp = 393, heightDp = 851)
@Composable
private fun OtpVerificationScreenPreview() {
    AppTheme {
        OtpVerificationScreen(onVerifySuccess = {}, onBackClick = {})
    }
}

@Preview(name = "OtpVerification · small", showBackground = true, widthDp = 320, heightDp = 600)
@Composable
private fun OtpVerificationScreenSmallPreview() {
    AppTheme {
        OtpVerificationScreen(onVerifySuccess = {}, onBackClick = {})
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
