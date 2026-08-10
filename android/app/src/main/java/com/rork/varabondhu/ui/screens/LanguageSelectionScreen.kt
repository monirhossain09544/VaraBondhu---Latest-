package com.rork.varabondhu.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.varabondhu.ui.components.BrandLogo
import com.rork.varabondhu.ui.components.VaraBondhuWordmark
import com.rork.varabondhu.ui.localization.AppLanguage
import com.rork.varabondhu.ui.localization.LocalizedText as Text
import com.rork.varabondhu.ui.theme.BanglaFamily
import com.rork.varabondhu.ui.theme.BrandGreen
import com.rork.varabondhu.ui.theme.CardWhite
import com.rork.varabondhu.ui.theme.Ink
import com.rork.varabondhu.ui.theme.InkMuted
import com.rork.varabondhu.ui.theme.MintCanvas
import com.rork.varabondhu.ui.theme.MintGlow

@Composable
fun LanguageSelectionScreen(
    selectedLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onContinue: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF2FFF6), MintCanvas, Color(0xFFFBFDFB))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (onNavigateBack != null) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Ink
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (onNavigateBack == null) 28.dp else 44.dp))
            BrandLogo(height = 76.dp)
            Spacer(modifier = Modifier.height(10.dp))
            VaraBondhuWordmark(fontSize = 30.sp)
            Spacer(modifier = Modifier.height(34.dp))

            Surface(
                color = BrandGreen,
                shape = CircleShape,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Language,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "পছন্দের ভাষা বেছে নিন",
                color = Ink,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "আপনি যেকোনো সময় প্রোফাইল থেকে ভাষা পরিবর্তন করতে পারবেন",
                color = InkMuted,
                fontFamily = BanglaFamily,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))

            LanguageCard(
                title = "বাংলা",
                subtitle = "বাংলায় ব্যবহার করুন",
                sample = "আপনার যাত্রা হোক সহজ",
                isSelected = selectedLanguage == AppLanguage.BANGLA,
                onClick = { onSelectLanguage(AppLanguage.BANGLA) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            LanguageCard(
                title = "English",
                subtitle = "Use the app in English",
                sample = "Make every journey simple",
                isSelected = selectedLanguage == AppLanguage.ENGLISH,
                onClick = { onSelectLanguage(AppLanguage.ENGLISH) }
            )

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text(
                    text = if (selectedLanguage == AppLanguage.BANGLA) {
                        "বাংলায় চালিয়ে যান"
                    } else {
                        "Continue in English"
                    },
                    color = Color.White,
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun LanguageCard(
    title: String,
    subtitle: String,
    sample: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) BrandGreen else Color(0xFFDDE5DF),
        animationSpec = spring(),
        label = "language-border"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MintGlow else CardWhite,
        animationSpec = spring(),
        label = "language-background"
    )
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        shadowElevation = if (isSelected) 3.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Ink,
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = subtitle,
                    color = InkMuted,
                    fontFamily = BanglaFamily,
                    fontSize = 13.sp
                )
                Text(
                    text = sample,
                    color = BrandGreen,
                    fontFamily = BanglaFamily,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Surface(
                color = if (isSelected) BrandGreen else Color.Transparent,
                shape = CircleShape,
                border = BorderStroke(1.5.dp, borderColor),
                modifier = Modifier.size(26.dp)
            ) {
                if (isSelected) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
