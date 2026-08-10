package com.rork.varabondhu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.rork.varabondhu.R
import com.rork.varabondhu.ui.theme.BrandGreen
import com.rork.varabondhu.ui.theme.Ink
import com.rork.varabondhu.ui.theme.WordmarkStyle

/** Intrinsic width / height of the lossless, tightly trimmed `logo_pin.png`. */
const val LOGO_ASPECT: Float = 389f / 512f

/** Intrinsic width / height of `city_street.webp`. */
const val CITY_ASPECT: Float = 1420f / 820f

/** The green rickshaw map-pin, sized by height so it never distorts. */
@Composable
fun BrandLogo(height: Dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo_pin),
        contentDescription = "VaraBondhu",
        modifier = modifier
            .height(height)
            .aspectRatio(LOGO_ASPECT)
    )
}

/** "Vara" in charcoal + "Bondhu" in brand green, in the bundled script face. */
@Composable
fun VaraBondhuWordmark(
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    lineHeightRatio: Float = 1.38f
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = Ink)) { append("Vara") }
            withStyle(SpanStyle(color = BrandGreen)) { append("Bondhu") }
        },
        style = WordmarkStyle.copy(
            fontSize = fontSize,
            lineHeight = fontSize * lineHeightRatio
        ),
        maxLines = 1,
        modifier = modifier
    )
}
