package com.rork.varabondhu.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.varabondhu.ui.theme.BodyMutedStyle
import com.rork.varabondhu.ui.theme.BrandGreen
import com.rork.varabondhu.ui.theme.ButtonGreen
import com.rork.varabondhu.ui.theme.ButtonLabelStyle
import com.rork.varabondhu.ui.theme.CardWhite
import com.rork.varabondhu.ui.theme.DangerRed
import com.rork.varabondhu.ui.theme.ErrorStyle
import com.rork.varabondhu.ui.theme.FieldBorder
import com.rork.varabondhu.ui.theme.FieldIcon
import com.rork.varabondhu.ui.theme.FieldTextStyle
import com.rork.varabondhu.ui.theme.Ink
import com.rork.varabondhu.ui.theme.InkMuted
import com.rork.varabondhu.ui.theme.PlaceholderStyle

private val FieldShape = RoundedCornerShape(12.dp)

/** Height of a text input row. */
val FieldHeight: Dp = 50.dp

/** Height of the filled primary action button. */
val ControlHeight: Dp = 50.dp

/** Height of a social sign-in button. */
val SocialHeight: Dp = 48.dp

/**
 * Bordered single-line input matching the VaraBondhu auth screens: leading glyph,
 * placeholder-only label, optional password reveal toggle, and a green border while
 * focused. [errorText] is rendered under the field when present.
 */
@Composable
fun VaraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isPassword: Boolean = false,
    errorText: String? = null
) {
    var isRevealed by rememberSaveable { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = when {
            errorText != null -> DangerRed
            isFocused -> BrandGreen
            else -> FieldBorder
        },
        animationSpec = tween(durationMillis = 180),
        label = "fieldBorder"
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(FieldHeight)
                .clip(FieldShape)
                .background(CardWhite)
                .border(width = 1.dp, color = borderColor, shape = FieldShape)
                .padding(start = 14.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = FieldIcon,
                modifier = Modifier.size(21.dp)
            )
            Spacer(modifier = Modifier.width(11.dp))
            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = FieldTextStyle,
                    cursorBrush = SolidColor(BrandGreen),
                    visualTransformation = if (isPassword && !isRevealed) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused },
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(text = placeholder, style = PlaceholderStyle, maxLines = 1)
                        }
                        innerTextField()
                    }
                )
            }
            if (isPassword) {
                IconButton(onClick = { isRevealed = !isRevealed }) {
                    Icon(
                        imageVector = if (isRevealed) {
                            Icons.Outlined.Visibility
                        } else {
                            Icons.Outlined.VisibilityOff
                        },
                        contentDescription = if (isRevealed) {
                            "পাসওয়ার্ড লুকান"
                        } else {
                            "পাসওয়ার্ড দেখান"
                        },
                        tint = FieldIcon,
                        modifier = Modifier.size(21.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(6.dp))
            }
        }
        if (errorText != null) {
            Text(
                text = errorText,
                style = ErrorStyle,
                modifier = Modifier.padding(start = 6.dp, top = 6.dp)
            )
        }
    }
}

/** Full-width filled green action button with a press-scale nudge and busy state. */
@Composable
fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }

    Button(
        onClick = onClick,
        enabled = !isLoading,
        shape = FieldShape,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonGreen,
            contentColor = Color.White,
            disabledContainerColor = ButtonGreen,
            disabledContentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(ControlHeight)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Text(text = label, style = ButtonLabelStyle)
        }
    }
}

/**
 * Outlined white button carrying a brand logo, used for Google / Facebook sign-in.
 * It is built to share a row with a sibling, so pass `Modifier.weight(1f)`.
 */
@Composable
fun SocialButton(
    label: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Button(
        onClick = onClick,
        shape = FieldShape,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = CardWhite,
            contentColor = Ink
        ),
        border = BorderStroke(width = 1.dp, color = FieldBorder),
        elevation = null,
        contentPadding = PaddingValues(horizontal = 8.dp),
        modifier = modifier.height(SocialHeight)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(9.dp))
            Text(
                text = label,
                style = ButtonLabelStyle.copy(fontSize = 14.sp, color = Ink),
                maxLines = 1
            )
        }
    }
}

/**
 * Centred footer line such as "অ্যাকাউন্ট নেই? সাইন আপ করুন" — the whole row is tappable so the
 * touch target stays comfortable even though only part of the text looks like a link.
 */
@Composable
fun AuthFooterLink(
    leadingText: String,
    linkText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = InkMuted)) { append(leadingText) }
                withStyle(SpanStyle(color = BrandGreen, fontWeight = FontWeight.Bold)) {
                    append(linkText)
                }
            },
            style = BodyMutedStyle,
            textAlign = TextAlign.Center
        )
    }
}

/** Horizontal rule with a centred label, e.g. "অথবা". */
@Composable
fun LabeledDivider(label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(color = FieldBorder, modifier = Modifier.weight(1f))
        Text(
            text = label,
            style = BodyMutedStyle,
            modifier = Modifier.padding(horizontal = 14.dp)
        )
        HorizontalDivider(color = FieldBorder, modifier = Modifier.weight(1f))
    }
}
