package com.rork.varabondhu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rork.varabondhu.R
import com.rork.varabondhu.ui.theme.IllustrationSky

/**
 * Vertical rhythm multiplier for the auth screens. Type and controls keep their size on
 * every device; only the designed gaps tighten, which is what lets both screens fit on
 * one page.
 *
 * @param available page height, i.e. the screen minus the system bars. The keyboard is
 * deliberately *not* part of this: see [AuthPage].
 */
fun authSpacingScale(available: Dp): Float = when {
    available >= 720.dp -> 1f
    available >= 640.dp -> 0.92f
    available >= 560.dp -> 0.84f
    available >= 480.dp -> 0.74f
    else -> 0.66f
}

/**
 * One vertical gap in an auth screen: [base] dp of designed spacing, plus a [weight]
 * share of whatever room the page has left over.
 */
data class AuthGap(val base: Int, val weight: Float)

/** Resolved gap sizes for one page; see [authSpacing]. */
class AuthSpacing internal constructor(private val scale: Float, private val extra: Dp) {
    operator fun get(gap: AuthGap): Dp = (gap.base * scale).dp + extra * gap.weight

    /** Combined height of [gaps] at this page's sizes. */
    fun total(gaps: List<AuthGap>): Dp = gaps.fold(0.dp) { sum, gap -> sum + this[gap] }
}

/**
 * Spreads [room] across [gaps]. Each gap first takes its designed base (tightened by
 * [scale] on short screens) and the leftover is then shared out by weight, so spare
 * space is spent on every gap in proportion instead of piling up into one hole.
 *
 * @param maxExtra ceiling on how much a single weight unit may grow. Past it the gaps
 * stop stretching and the caller keeps the remaining room for something better — a taller
 * illustration, or one deliberate space above the primary action.
 */
fun authSpacing(
    gaps: List<AuthGap>,
    room: Dp,
    scale: Float,
    maxExtra: Dp = Dp.Infinity
): AuthSpacing {
    val base = (gaps.sumOf { it.base } * scale).dp
    val weight = gaps.fold(0f) { total, gap -> total + gap.weight }
    val extra = if (weight > 0f) {
        ((room - base).coerceAtLeast(0.dp) / weight).coerceAtMost(maxExtra)
    } else {
        0.dp
    }
    return AuthSpacing(scale, extra)
}

/**
 * Page shell shared by the auth screens.
 *
 * The keyboard never re-flows the page: [content] is always laid out at the height the
 * screen has with the keyboard **closed**, and the keyboard only shrinks the viewport
 * around it. So the artwork is covered rather than resized, and nothing above it moves.
 *
 * Scrolling is clamped to [keyboardTail] — the height of the trailing block that may sit
 * behind the keyboard (on login, everything below the sign-in button). The page can
 * therefore be pulled up exactly far enough to reveal the primary action and no further,
 * and it is pinned there automatically while the keyboard is open.
 *
 * @param padTop reserve the status bar area, for pages that start with real chrome.
 * Leave it off when artwork is meant to run behind the status bar.
 * @param headerHeight height of [header]; must match what [header] actually draws,
 * because the page height is measured against it.
 * @param header optional sticky row that stays put while the page scrolls.
 * @param keyboardTail trailing height that may hide behind the keyboard.
 * @param content receives the page size to lay itself out against.
 */
@Composable
fun AuthPage(
    modifier: Modifier = Modifier,
    padTop: Boolean = false,
    headerHeight: Dp = 0.dp,
    keyboardTail: (pageWidth: Dp, pageHeight: Dp) -> Dp = { _, _ -> 0.dp },
    header: @Composable (() -> Unit)? = null,
    content: @Composable (pageWidth: Dp, pageHeight: Dp) -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val pageWidth = maxWidth

        // Most devices report the keyboard as an inset and leave the window alone, but
        // some resize the window instead. Latching the tallest height seen at this width
        // makes "keyboard closed" mean the same thing on both.
        val resting = remember(pageWidth) { RestingHeight() }
        val windowPx = with(density) { maxHeight.roundToPx() }
        if (windowPx > resting.px) resting.px = windowPx
        val restingHeight = with(density) { resting.px.toDp() }

        val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val statusInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val imeInset = WindowInsets.ime.asPaddingValues().calculateBottomPadding()

        val topInset = if (padTop) statusInset else 0.dp
        val lostHeight = (restingHeight - maxHeight).coerceAtLeast(0.dp)
        val keyboard = maxOf((imeInset - bottomInset).coerceAtLeast(0.dp), lostHeight)
        val pageHeight = (restingHeight - topInset - bottomInset - headerHeight)
            .coerceAtLeast(0.dp)

        val scrollState = rememberScrollState()
        val isKeyboardOpen = keyboard > 0.dp
        val stableDensity = remember(density.density) {
            Density(density = density.density, fontScale = 1f)
        }

        CompositionLocalProvider(LocalDensity provides stableDensity) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (topInset > 0.dp) {
                    Spacer(modifier = Modifier.height(topInset))
                }
                if (header != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerHeight)
                    ) {
                        header()
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        // The page is locked while the keyboard is closed, preventing
                        // elastic overscroll and accidental whole-screen movement.
                        .padding(bottom = (keyboard - lostHeight).coerceAtLeast(0.dp))
                        .verticalScroll(scrollState, enabled = isKeyboardOpen)
                ) {
                    RestingPage(
                        pageHeight = pageHeight,
                        tail = keyboardTail(pageWidth, pageHeight),
                        content = { content(pageWidth, pageHeight) }
                    )
                }
                if (bottomInset > 0.dp) {
                    Spacer(modifier = Modifier.height(bottomInset))
                }
            }
        }

        LaunchedEffect(isKeyboardOpen, scrollState.maxValue) {
            scrollState.scrollTo(if (isKeyboardOpen) scrollState.maxValue else 0)
        }
    }
}

/**
 * Lays [content] out at the full resting [pageHeight] but reports itself [tail] shorter,
 * so a scroll parent can only ever pull the page up by that much. The tail keeps drawing
 * below the reported bounds and is simply covered by the keyboard.
 */
@Composable
private fun RestingPage(pageHeight: Dp, tail: Dp, content: @Composable () -> Unit) {
    Layout(content = content, modifier = Modifier.fillMaxWidth()) { measurables, constraints ->
        val width = constraints.maxWidth
        val full = pageHeight.roundToPx()
        val placeables = measurables.map { it.measure(Constraints.fixed(width, full)) }
        layout(width, (full - tail.roundToPx()).coerceIn(0, full)) {
            placeables.forEach { it.place(0, 0) }
        }
    }
}

/** Tallest window height seen so far, i.e. the height with no keyboard on screen. */
private class RestingHeight {
    var px: Int = 0
}

/**
 * The city illustration, free of any text. Fills the given [height] with the artwork
 * anchored to the bottom edge so the rickshaw, CNG and bus are never cropped; whatever
 * height is left above it is filled with the artwork's own flat sky colour, so a tall
 * screen simply gets more sky instead of a seam.
 */
@Composable
fun CityHero(height: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(IllustrationSky)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val natural = maxWidth / CITY_ASPECT
            Image(
                painter = painterResource(R.drawable.city_street),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (natural < height) natural else height)
            )
        }
    }
}

/**
 * Two-slot layout for the login page: [sheet] is pinned to the bottom at its natural
 * height and [hero] fills everything above it, overlapping by [overlap] so the sheet's
 * rounded corners sit on top of the artwork.
 *
 * Because the sheet is measured first and anchored to the bottom, the form can never be
 * pushed off the page — when space runs short the artwork gives way instead, down to
 * zero height.
 */
@Composable
fun HeroSheetLayout(
    overlap: Dp,
    hero: @Composable (heroHeight: Dp) -> Unit,
    sheet: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val width = constraints.maxWidth
        val fullHeight = constraints.maxHeight

        val sheetPlaceables = subcompose(AuthSlot.Sheet) { sheet() }.map {
            it.measure(Constraints(minWidth = width, maxWidth = width, minHeight = 0))
        }
        val sheetHeight = sheetPlaceables.maxOfOrNull { it.height } ?: 0
        val heroHeight = (fullHeight - sheetHeight + overlap.roundToPx()).coerceAtLeast(0)

        val heroPlaceables = subcompose(AuthSlot.Hero) { hero(heroHeight.toDp()) }.map {
            it.measure(Constraints.fixed(width, heroHeight))
        }

        layout(width, fullHeight) {
            heroPlaceables.forEach { it.place(0, 0) }
            sheetPlaceables.forEach { it.place(0, fullHeight - sheetHeight) }
        }
    }
}

private enum class AuthSlot { Hero, Sheet }
