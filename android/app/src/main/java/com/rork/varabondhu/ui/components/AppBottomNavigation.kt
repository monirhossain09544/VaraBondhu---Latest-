package com.rork.varabondhu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.varabondhu.ui.theme.BanglaFamily
import com.rork.varabondhu.ui.theme.ButtonGreen
import com.rork.varabondhu.ui.theme.CardWhite
import com.rork.varabondhu.ui.theme.InkMuted

/** Main destinations represented by the persistent bottom navigation. */
enum class MainDestination { HOME, PROFILE }

/** Shared app navigation with the central fare-contribution action. */
@Composable
fun AppBottomNavigation(
    selectedDestination: MainDestination,
    onNavigateHome: () -> Unit,
    onNavigateToVaraDin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navigationBottomInset = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp + navigationBottomInset)
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(70.dp + navigationBottomInset),
            color = CardWhite,
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
            border = BorderStroke(1.dp, Color(0xFFE7ECE8)),
            shadowElevation = 8.dp
        ) {}

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 18.dp)
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 3.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppBottomNavItem(
                icon = Icons.Rounded.Home,
                label = "হোম",
                isSelected = selectedDestination == MainDestination.HOME,
                onClick = onNavigateHome,
                modifier = Modifier.weight(1f)
            )
            AppBottomNavItem(
                icon = Icons.Rounded.BarChart,
                label = "চার্ট রেটিং",
                onClick = null,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f))
            AppBottomNavItem(
                icon = Icons.Rounded.Assessment,
                label = "রিপোর্ট",
                onClick = null,
                modifier = Modifier.weight(1f)
            )
            AppBottomNavItem(
                icon = Icons.Rounded.PersonOutline,
                label = "প্রোফাইল",
                isSelected = selectedDestination == MainDestination.PROFILE,
                onClick = onNavigateToProfile,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(76.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                onClick = onNavigateToVaraDin,
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = ButtonGreen,
                shadowElevation = 7.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "ভাড়া দিন",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Text(
                text = "ভাড়া দিন",
                color = InkMuted,
                fontFamily = BanglaFamily,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AppBottomNavItem(
    icon: ImageVector,
    label: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val itemColor = if (isSelected) Color(0xFF0B7B37) else InkMuted
    Column(
        modifier = modifier
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = itemColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = label,
            color = itemColor,
            fontFamily = BanglaFamily,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}
