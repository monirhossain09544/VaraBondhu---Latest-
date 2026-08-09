package com.rork.varabondhu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.varabondhu.R
import com.rork.varabondhu.ui.components.BrandLogo
import com.rork.varabondhu.ui.components.CITY_ASPECT
import com.rork.varabondhu.ui.components.VaraBondhuWordmark
import com.rork.varabondhu.ui.theme.AppTheme
import com.rork.varabondhu.ui.theme.MintCanvas
import com.rork.varabondhu.ui.theme.MintCanvasTop
import com.rork.varabondhu.ui.theme.RoadGrey
import com.rork.varabondhu.ui.theme.TaglineAccentStyle
import com.rork.varabondhu.ui.theme.TaglineStyle
import kotlinx.coroutines.delay

private const val SplashHoldMillis = 2200L

private const val TaglineLine1 = "যাত্রার আগে ন্যায্য ভাড়া জানুন,"
private const val TaglineLine2 = "অন্যকে বাঁচাতে তথ্য দিন"

private val CanvasWash = Brush.verticalGradient(
    0f to MintCanvasTop,
    0.45f to MintCanvas,
    1f to MintCanvas
)

/**
 * Static opening screen: pin logo, wordmark, Bengali tagline and the city street
 * illustration anchored flush to the bottom edge. Holds still, then hands over to
 * [onFinished] — no entrance animation by design.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(SplashHoldMillis)
        onFinished()
    }

    SplashContent()
}

@Composable
private fun SplashContent(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasWash)
    ) {
        // The illustration's own road colour continues below it so the artwork always
        // reaches the very bottom edge, including under the gesture navigation bar.
        val roadBand = (maxHeight * 0.128f).coerceIn(56.dp, 132.dp)

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.city_street),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(CITY_ASPECT)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(roadBand)
                    .background(RoadGrey)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.25f))

            BrandLogo(height = 120.dp)

            Spacer(modifier = Modifier.height(16.dp))

            VaraBondhuWordmark(fontSize = 36.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = TaglineLine1,
                style = TaglineStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 28.dp)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = TaglineLine2,
                style = TaglineAccentStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 28.dp)
            )

            Spacer(modifier = Modifier.weight(0.79f))
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 879)
@Composable
private fun SplashScreenPreview() {
    AppTheme {
        SplashContent()
    }
}
