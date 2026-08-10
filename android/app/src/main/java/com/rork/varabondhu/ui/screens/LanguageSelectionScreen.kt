package com.rork.varabondhu.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.varabondhu.ui.components.BrandLogo
import com.rork.varabondhu.ui.components.VaraBondhuWordmark
import com.rork.varabondhu.ui.localization.AppLanguage
import com.rork.varabondhu.ui.theme.BanglaFamily
import com.rork.varabondhu.ui.theme.BrandGreen
import com.rork.varabondhu.ui.theme.CardWhite
import com.rork.varabondhu.ui.theme.FieldBorder
import com.rork.varabondhu.ui.theme.Ink
import com.rork.varabondhu.ui.theme.MintCanvasTop
import com.rork.varabondhu.ui.theme.MintGlow

@Composable
fun LanguageSelectionScreen(
    selectedLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onContinue: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isBanglaSelected: Boolean = selectedLanguage == AppLanguage.BANGLA

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MintCanvasTop, Color(0xFFF5FAF7), Color(0xFFFCFDFC))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (onNavigateBack != null) {
            Surface(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp)
                    .size(44.dp),
                shape = CircleShape,
                color = CardWhite.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, FieldBorder),
                shadowElevation = 1.dp
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = if (isBanglaSelected) "পেছনে যান" else "Go back",
                        tint = Ink,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (onNavigateBack == null) 40.dp else 54.dp))
            BrandLogo(height = 68.dp)
            Spacer(modifier = Modifier.height(8.dp))
            VaraBondhuWordmark(fontSize = 29.sp)
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = if (isBanglaSelected) "পছন্দের ভাষা" else "Preferred language",
                color = Ink,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 25.sp,
                lineHeight = 32.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, FieldBorder),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LanguageOption(
                        title = "বাংলা",
                        isSelected = isBanglaSelected,
                        onClick = { onSelectLanguage(AppLanguage.BANGLA) },
                        modifier = Modifier.clip(
                            RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
                        )
                    )
                    Divider(
                        color = FieldBorder.copy(alpha = 0.85f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    LanguageOption(
                        title = "English",
                        isSelected = !isBanglaSelected,
                        onClick = { onSelectLanguage(AppLanguage.ENGLISH) },
                        modifier = Modifier.clip(
                            RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text(
                    text = if (isBanglaSelected) "বাংলায় চালিয়ে যান" else "Continue in English",
                    color = Color.White,
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.size(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
private fun LanguageOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rowColor: Color by animateColorAsState(
        targetValue = if (isSelected) MintGlow.copy(alpha = 0.78f) else CardWhite,
        animationSpec = spring(),
        label = "language-row"
    )
    val indicatorColor: Color by animateColorAsState(
        targetValue = if (isSelected) BrandGreen else Color(0xFFD4DDD7),
        animationSpec = spring(),
        label = "language-indicator"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(rowColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Ink,
            fontFamily = BanglaFamily,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 25.sp,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(indicatorColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(CardWhite, CircleShape)
                )
            }
        }
    }
}
