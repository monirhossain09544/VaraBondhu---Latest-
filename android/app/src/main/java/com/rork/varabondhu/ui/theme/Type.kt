package com.rork.varabondhu.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.rork.varabondhu.R

/** Rounded script used for the VaraBondhu wordmark. */
val PacificoFamily: FontFamily = FontFamily(
    Font(R.font.pacifico_regular, FontWeight.Normal)
)

/** Bengali-capable family used for every piece of UI copy. */
val BanglaFamily: FontFamily = FontFamily(
    Font(R.font.hind_siliguri_regular, FontWeight.Normal),
    Font(R.font.hind_siliguri_semibold, FontWeight.SemiBold),
    Font(R.font.hind_siliguri_bold, FontWeight.Bold)
)

private val NoFontPadding: PlatformTextStyle = PlatformTextStyle(includeFontPadding = false)

private val CenteredLines: LineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None
)

/** "VaraBondhu" lockup — sized so it spans roughly 60% of the screen width. */
val WordmarkStyle: TextStyle = TextStyle(
    fontFamily = PacificoFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 45.sp,
    lineHeight = 62.sp,
    platformStyle = NoFontPadding,
    lineHeightStyle = CenteredLines
)

/** First tagline line, dark charcoal. */
val TaglineStyle: TextStyle = TextStyle(
    fontFamily = BanglaFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 19.sp,
    lineHeight = 28.sp,
    color = Ink,
    platformStyle = NoFontPadding,
    lineHeightStyle = CenteredLines
)

/** Second tagline line, bold brand green. */
val TaglineAccentStyle: TextStyle = TextStyle(
    fontFamily = BanglaFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 29.sp,
    color = BrandGreen,
    platformStyle = NoFontPadding,
    lineHeightStyle = CenteredLines
)

/** Screen heading, e.g. "স্বাগতম!". */
val HeadingStyle: TextStyle = TextStyle(
    fontFamily = BanglaFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 23.sp,
    lineHeight = 32.sp,
    color = BrandGreen,
    platformStyle = NoFontPadding,
    lineHeightStyle = CenteredLines
)

/** Secondary supporting copy. */
val BodyMutedStyle: TextStyle = TextStyle(
    fontFamily = BanglaFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    color = InkMuted,
    platformStyle = NoFontPadding,
    lineHeightStyle = CenteredLines
)

/** Text typed into an input field. */
val FieldTextStyle: TextStyle = TextStyle(
    fontFamily = BanglaFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 21.sp,
    color = Ink,
    platformStyle = NoFontPadding,
    lineHeightStyle = CenteredLines
)

/** Input field placeholder. */
val PlaceholderStyle: TextStyle = FieldTextStyle.copy(
    fontSize = 14.sp,
    color = FieldPlaceholder
)

/** Filled primary button label. */
val ButtonLabelStyle: TextStyle = TextStyle(
    fontFamily = BanglaFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    platformStyle = NoFontPadding,
    lineHeightStyle = CenteredLines
)

/** Tappable inline text such as "সাইন আপ করুন". */
val LinkStyle: TextStyle = TextStyle(
    fontFamily = BanglaFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    color = BrandGreen,
    platformStyle = NoFontPadding,
    lineHeightStyle = CenteredLines
)

/** Inline validation message. */
val ErrorStyle: TextStyle = TextStyle(
    fontFamily = BanglaFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 17.sp,
    color = DangerRed,
    platformStyle = NoFontPadding,
    lineHeightStyle = CenteredLines
)

private val Base: Typography = Typography()

val AppTypography: Typography = Typography(
    displayLarge = Base.displayLarge.copy(fontFamily = BanglaFamily),
    displayMedium = Base.displayMedium.copy(fontFamily = BanglaFamily),
    displaySmall = Base.displaySmall.copy(fontFamily = BanglaFamily),
    headlineLarge = Base.headlineLarge.copy(fontFamily = BanglaFamily),
    headlineMedium = Base.headlineMedium.copy(fontFamily = BanglaFamily),
    headlineSmall = Base.headlineSmall.copy(fontFamily = BanglaFamily),
    titleLarge = Base.titleLarge.copy(fontFamily = BanglaFamily),
    titleMedium = Base.titleMedium.copy(fontFamily = BanglaFamily),
    titleSmall = Base.titleSmall.copy(fontFamily = BanglaFamily),
    bodyLarge = Base.bodyLarge.copy(fontFamily = BanglaFamily),
    bodyMedium = Base.bodyMedium.copy(fontFamily = BanglaFamily),
    bodySmall = Base.bodySmall.copy(fontFamily = BanglaFamily),
    labelLarge = Base.labelLarge.copy(fontFamily = BanglaFamily),
    labelMedium = Base.labelMedium.copy(fontFamily = BanglaFamily),
    labelSmall = Base.labelSmall.copy(fontFamily = BanglaFamily)
)
