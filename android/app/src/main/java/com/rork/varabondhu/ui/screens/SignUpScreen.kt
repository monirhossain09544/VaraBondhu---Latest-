package com.rork.varabondhu.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
import com.rork.varabondhu.ui.components.ControlHeight
import com.rork.varabondhu.ui.components.FieldHeight
import com.rork.varabondhu.ui.components.PrimaryButton
import com.rork.varabondhu.ui.components.VaraTextField
import com.rork.varabondhu.ui.components.authSpacing
import com.rork.varabondhu.ui.components.authSpacingScale
import com.rork.varabondhu.ui.theme.AppTheme
import com.rork.varabondhu.ui.theme.BodyMutedStyle
import com.rork.varabondhu.ui.theme.BrandGreen
import com.rork.varabondhu.ui.theme.ButtonGreen
import com.rork.varabondhu.ui.theme.CardWhite
import com.rork.varabondhu.ui.theme.ErrorStyle
import com.rork.varabondhu.ui.theme.FieldBorder
import com.rork.varabondhu.ui.theme.Ink
import com.rork.varabondhu.ui.theme.MintGlow
import com.rork.varabondhu.ui.theme.PageWhite
import com.rork.varabondhu.ui.theme.TaglineStyle

/** Intrinsic width / height of `signup_shield.webp`. */
private const val ShieldAspect = 560f / 564f

/** Sticky header: its height is reserved by the page, so the two must agree. */
private val HeaderHeight = 52.dp
private val BackButtonSize = 44.dp

/** Fixed element heights — kept in sync with the composition below. */
private val HeadingHeight = 28.dp
private val SubtitleHeight = 20.dp
private val TermsRowHeight = 42.dp
private val FooterHeight = 38.dp

/** Share of the page the illustration aims for, and the bounds it stays inside. */
private const val ART_FRACTION = 0.15f
private val ArtMinHeight = 76.dp
private val ArtMaxHeight = 132.dp

/** Below this the illustration slot is too cramped to be worth showing. */
private val ArtMinVisible = 64.dp

/**
 * How far a single gap may stretch. Past it the gaps stop growing and the remaining room
 * becomes one deliberate space above the button, which reads as a docked action rather
 * than a hole in the middle of the form.
 */
private val MaxGapStretch = 12.dp

private val GapTop = AuthGap(base = 8, weight = 0.2f)
private val GapAfterArt = AuthGap(base = 24, weight = 1.4f)
private val GapAfterHeading = AuthGap(base = 6, weight = 0.15f)
private val GapAfterSubtitle = AuthGap(base = 22, weight = 1.2f)
private val GapBetweenFields = AuthGap(base = 16, weight = 0.35f)
private val GapBeforeTerms = AuthGap(base = 14, weight = 0.3f)
private val GapBeforeButton = AuthGap(base = 24, weight = 1.3f)
private val GapBeforeFooter = AuthGap(base = 22, weight = 1.5f)
private val GapBottom = AuthGap(base = 8, weight = 0.2f)

private val SignUpGaps = listOf(
    GapTop,
    GapAfterArt,
    GapAfterHeading,
    GapAfterSubtitle,
    GapBetweenFields,
    GapBeforeTerms,
    GapBeforeButton,
    GapBeforeFooter,
    GapBottom
)

/**
 * Sign-up screen, split into two steps so each one sits comfortably on a single page:
 * name and number first, then the password and consent. The header is sticky, the
 * illustration stays small, and the keyboard covers the page instead of re-flowing it.
 */
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SignUpViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) {
            viewModel.onNavigationHandled()
            onSignUpSuccess()
        }
    }

    val isSecurityStep = uiState.step == SignUpStep.Security
    val goBack: () -> Unit = {
        focusManager.clearFocus()
        if (isSecurityStep) viewModel.backToBasics() else onNavigateToLogin()
    }

    BackHandler(enabled = isSecurityStep) {
        focusManager.clearFocus()
        viewModel.backToBasics()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageWhite)
    ) {
        AuthPage(
            padTop = true,
            headerHeight = HeaderHeight,
            keyboardTail = { _, height -> tailBelowButton(signUpMetrics(height)) },
            header = { SignUpHeader(step = uiState.step, onBack = goBack) }
        ) { _, pageHeight ->
            val metrics = signUpMetrics(pageHeight)
            val spacing = metrics.spacing

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(spacing[GapTop]))

                // Flexible slot: it also absorbs the extra height an inline error adds.
                ShieldIllustration(
                    target = metrics.artTarget,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(spacing[GapAfterArt]))

                AnimatedContent(
                    targetState = uiState.step,
                    transitionSpec = {
                        val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                        val enter = slideInHorizontally(animationSpec = tween(280)) { width ->
                            direction * width / 4
                        } + fadeIn(animationSpec = tween(200))
                        val exit = slideOutHorizontally(animationSpec = tween(280)) { width ->
                            -direction * width / 4
                        } + fadeOut(animationSpec = tween(160))
                        enter togetherWith exit using SizeTransform(
                            clip = true,
                            sizeAnimationSpec = { _, _ -> snap() }
                        )
                    },
                    label = "signUpStep"
                ) { step ->
                    when (step) {
                        SignUpStep.Basics -> StepBlock(
                            heading = "একটু তথ্য দিন, শুরু করা যাক",
                            subtitle = "নাম আর মোবাইল নম্বর দিয়ে শুরু",
                            spacing = spacing,
                            airHeight = metrics.airBeforeButton,
                            buttonLabel = "পরের ধাপ",
                            onSubmit = {
                                focusManager.clearFocus()
                                viewModel.continueToSecurity()
                            }
                        ) {
                            VaraTextField(
                                value = uiState.name,
                                onValueChange = viewModel::onNameChange,
                                placeholder = "আপনার নাম",
                                leadingIcon = Icons.Outlined.Person,
                                errorText = uiState.nameError
                            )

                            Spacer(modifier = Modifier.height(spacing[GapBetweenFields]))

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

                            // Sits where the consent row sits in the next step, so the
                            // button never shifts between the two.
                            PrivacyNote()
                        }

                        SignUpStep.Security -> StepBlock(
                            heading = "এবার একটা পাসওয়ার্ড দিন",
                            subtitle = "কমপক্ষে ৬ অক্ষরের পাসওয়ার্ড রাখুন",
                            spacing = spacing,
                            airHeight = metrics.airBeforeButton,
                            buttonLabel = "সাইন আপ করুন",
                            isLoading = uiState.isSubmitting,
                            onSubmit = viewModel::submit
                        ) {
                            VaraTextField(
                                value = uiState.password,
                                onValueChange = viewModel::onPasswordChange,
                                placeholder = "পাসওয়ার্ড দিন",
                                leadingIcon = Icons.Outlined.Lock,
                                keyboardType = KeyboardType.Password,
                                isPassword = true,
                                errorText = uiState.passwordError
                            )

                            Spacer(modifier = Modifier.height(spacing[GapBetweenFields]))

                            VaraTextField(
                                value = uiState.confirmPassword,
                                onValueChange = viewModel::onConfirmPasswordChange,
                                placeholder = "পাসওয়ার্ড আবার দিন",
                                leadingIcon = Icons.Outlined.Lock,
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done,
                                isPassword = true,
                                errorText = uiState.confirmPasswordError
                            )

                            Spacer(modifier = Modifier.height(spacing[GapBeforeTerms]))

                            TermsRow(
                                isChecked = uiState.hasAcceptedTerms,
                                errorText = uiState.termsError,
                                onToggle = viewModel::onTermsToggle
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing[GapBeforeFooter]))

                AuthFooterLink(
                    leadingText = "ইতিমধ্যে অ্যাকাউন্ট আছে? ",
                    linkText = "লগইন করুন",
                    onClick = onNavigateToLogin
                )

                Spacer(modifier = Modifier.height(spacing[GapBottom]))
            }
        }
    }
}

/**
 * One step of the form: heading, supporting line, the step's own fields, then the primary
 * button. [airHeight] is the room left over on a tall screen and is sized per step so the
 * button never shifts between them.
 */
@Composable
private fun StepBlock(
    heading: String,
    subtitle: String,
    spacing: AuthSpacing,
    airHeight: Dp,
    buttonLabel: String,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    body: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = heading,
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
            text = subtitle,
            style = BodyMutedStyle.copy(fontSize = 13.sp, lineHeight = 19.sp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing[GapAfterSubtitle]))

        body()

        Spacer(modifier = Modifier.height(airHeight + spacing[GapBeforeButton]))

        PrimaryButton(label = buttonLabel, onClick = onSubmit, isLoading = isLoading)
    }
}

/**
 * Why the number is needed, in the same slot the consent row occupies on the next step.
 */
@Composable
private fun PrivacyNote(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(TermsRowHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MintGlow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = BrandGreen,
                modifier = Modifier.size(15.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "নম্বরটি শুধু অ্যাকাউন্ট যাচাইয়ে ব্যবহার হবে, কারো সাথে শেয়ার করা হবে না",
            style = BodyMutedStyle.copy(fontSize = 12.sp, lineHeight = 17.sp),
            maxLines = 2
        )
    }
}

/** Sticky header: back affordance, title, and how far along the form is. */
@Composable
private fun SignUpHeader(step: SignUpStep, onBack: () -> Unit, modifier: Modifier = Modifier) {
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
            text = "সাইন আপ করুন",
            style = TaglineStyle.copy(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1
        )
        Spacer(modifier = Modifier.weight(1f))
        StepDots(step = step)
        Spacer(modifier = Modifier.width(10.dp))
    }
}

/** Two dots: the current step is a green pill, the other a soft grey dot. */
@Composable
private fun StepDots(step: SignUpStep, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SignUpStep.entries.forEach { entry ->
            val isCurrent = entry == step
            val width by animateDpAsState(
                targetValue = if (isCurrent) 20.dp else 8.dp,
                animationSpec = tween(durationMillis = 240),
                label = "stepDotWidth"
            )
            val color by animateColorAsState(
                targetValue = if (isCurrent) BrandGreen else FieldBorder,
                animationSpec = tween(durationMillis = 240),
                label = "stepDotColor"
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

/**
 * The trust illustration, centred in whatever slot the layout can spare. It disappears
 * rather than squashing when that slot gets too small.
 */
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

/** Terms consent row — the label and box share one tap target. */
@Composable
private fun TermsRow(
    isChecked: Boolean,
    errorText: String?,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onToggle),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = ButtonGreen,
                    uncheckedColor = FieldBorder,
                    checkmarkColor = CardWhite
                ),
                modifier = Modifier.size(TermsRowHeight)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = buildAnnotatedString {
                    append("আমি ")
                    withStyle(
                        SpanStyle(color = BrandGreen, fontWeight = FontWeight.Bold)
                    ) { append("শর্তাবলী") }
                    append(" ও ")
                    withStyle(
                        SpanStyle(color = BrandGreen, fontWeight = FontWeight.Bold)
                    ) { append("গোপনীয়তা নীতিমালা") }
                    append(" পড়েছি এবং সম্মত")
                },
                style = TaglineStyle.copy(fontSize = 12.sp, lineHeight = 17.sp)
            )
        }
        if (errorText != null) {
            Text(
                text = errorText,
                style = ErrorStyle,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
            )
        }
    }
}

/** Everything the sign-up page needs to lay itself out at a given size. */
private class SignUpMetrics(
    val artTarget: Dp,
    val airBeforeButton: Dp,
    val spacing: AuthSpacing
)

/**
 * Keeps the illustration small, lets every gap breathe up to a sensible ceiling, and
 * parks whatever is still left above the button so no gap in the form looks stretched.
 */
private fun signUpMetrics(pageHeight: Dp): SignUpMetrics {
    val fixed = HeadingHeight + SubtitleHeight + FieldHeight * 2 + TermsRowHeight +
        ControlHeight + FooterHeight
    val artTarget = (pageHeight * ART_FRACTION).coerceIn(ArtMinHeight, ArtMaxHeight)
    val spacing = authSpacing(
        gaps = SignUpGaps,
        room = pageHeight - fixed - artTarget,
        scale = authSpacingScale(pageHeight),
        maxExtra = MaxGapStretch
    )
    val airBeforeButton = (pageHeight - fixed - artTarget - spacing.total(SignUpGaps))
        .coerceAtLeast(0.dp)

    return SignUpMetrics(
        artTarget = artTarget,
        airBeforeButton = airBeforeButton,
        spacing = spacing
    )
}

/**
 * Height of the block below the primary button — what may hide behind the keyboard, and
 * therefore how far the page is allowed to be pulled up.
 */
private fun tailBelowButton(metrics: SignUpMetrics): Dp {
    val spacing = metrics.spacing
    return spacing[GapBeforeFooter] + FooterHeight + spacing[GapBottom]
}

@Preview(name = "Sign up · normal", showBackground = true, widthDp = 393, heightDp = 851)
@Composable
private fun SignUpScreenPreview() {
    AppTheme {
        SignUpScreen(onSignUpSuccess = {}, onNavigateToLogin = {})
    }
}

@Preview(name = "Sign up · small", showBackground = true, widthDp = 320, heightDp = 600)
@Composable
private fun SignUpScreenSmallPreview() {
    AppTheme {
        SignUpScreen(onSignUpSuccess = {}, onNavigateToLogin = {})
    }
}

@Preview(name = "Sign up · large", showBackground = true, widthDp = 480, heightDp = 1024)
@Composable
private fun SignUpScreenLargePreview() {
    AppTheme {
        SignUpScreen(onSignUpSuccess = {}, onNavigateToLogin = {})
    }
}
