package com.rork.varabondhu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rork.varabondhu.ui.components.AppBottomNavigation
import com.rork.varabondhu.ui.components.MainDestination
import com.rork.varabondhu.ui.theme.AppTheme
import com.rork.varabondhu.ui.theme.BanglaFamily
import com.rork.varabondhu.ui.theme.BrandGreen
import com.rork.varabondhu.ui.theme.ButtonGreen
import com.rork.varabondhu.ui.theme.CardWhite
import com.rork.varabondhu.ui.theme.DangerRed
import com.rork.varabondhu.ui.theme.FieldBorder
import com.rork.varabondhu.ui.theme.Ink
import com.rork.varabondhu.ui.theme.InkMuted
import com.rork.varabondhu.ui.theme.MintGlow

private val ProfileBackground: Color = Color(0xFFFBFCFB)
private val VerifiedGreen: Color = Color(0xFF087B38)
private val BadgeGreen: Color = Color(0xFFE5F5E9)
private val MetricBlue: Color = Color(0xFF386BC7)
private val MetricAmber: Color = Color(0xFFF5A623)
private val MetricPurple: Color = Color(0xFF6B3CC4)
private val ProfileCardShape: RoundedCornerShape = RoundedCornerShape(16.dp)

private enum class ProfileDialog {
    EDIT_PROFILE,
    NOTIFICATIONS,
    LANGUAGE,
    HELP,
    LOGOUT
}

/** Compact Bengali-first profile with trust metrics and account settings. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateHome: () -> Unit,
    onNavigateToVaraDin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState: ProfileUiState by viewModel.uiState.collectAsStateWithLifecycle()
    var activeDialog: ProfileDialog? by rememberSaveable { mutableStateOf(null) }
    var editedName: String by rememberSaveable(uiState.name) { mutableStateOf(uiState.name) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ProfileBackground,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "প্রোফাইল",
                        color = Ink,
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ProfileBackground)
            )
        },
        bottomBar = {
            AppBottomNavigation(
                selectedDestination = MainDestination.PROFILE,
                onNavigateHome = onNavigateHome,
                onNavigateToVaraDin = onNavigateToVaraDin,
                onNavigateToProfile = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding: PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileSummaryCard(uiState = uiState)
                ProfileMetricsCard(uiState = uiState)

                Text(
                    text = "অ্যাকাউন্ট ও সেটিংস",
                    color = Ink,
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(start = 2.dp, top = 4.dp)
                )

                SettingsCard(
                    onEditProfile = { activeDialog = ProfileDialog.EDIT_PROFILE },
                    onNotifications = { activeDialog = ProfileDialog.NOTIFICATIONS },
                    onLanguage = { activeDialog = ProfileDialog.LANGUAGE },
                    onHelp = { activeDialog = ProfileDialog.HELP },
                    onLogout = { activeDialog = ProfileDialog.LOGOUT }
                )
            }
        }
    }

    when (activeDialog) {
        ProfileDialog.EDIT_PROFILE -> EditProfileDialog(
            name = editedName,
            onNameChange = { value: String -> editedName = value.take(50) },
            onDismiss = {
                editedName = uiState.name
                activeDialog = null
            },
            onSave = {
                viewModel.updateName(editedName)
                activeDialog = null
            }
        )

        ProfileDialog.NOTIFICATIONS -> NotificationSettingsDialog(
            isEnabled = uiState.hasNotificationsEnabled,
            onToggle = viewModel::toggleNotifications,
            onDismiss = { activeDialog = null }
        )

        ProfileDialog.LANGUAGE -> InformationDialog(
            icon = Icons.Outlined.Language,
            title = "ভাষা",
            message = "VaraBondhu বর্তমানে বাংলা ভাষায় ব্যবহার করা যাচ্ছে। ইংরেজি ভাষা শিগগিরই যুক্ত হবে।",
            onDismiss = { activeDialog = null }
        )

        ProfileDialog.HELP -> InformationDialog(
            icon = Icons.AutoMirrored.Outlined.HelpOutline,
            title = "সহায়তা ও সাপোর্ট",
            message = "লোকেশন, ভাড়া খোঁজা বা ভাড়া জমা দিতে সমস্যা হলে আবার চেষ্টা করুন। সরাসরি সাপোর্ট সুবিধা শিগগিরই যুক্ত হবে।",
            onDismiss = { activeDialog = null }
        )

        ProfileDialog.LOGOUT -> LogoutDialog(
            onDismiss = { activeDialog = null },
            onLogout = onLogout
        )

        null -> Unit
    }
}

@Composable
private fun ProfileSummaryCard(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isCompact: Boolean = maxWidth < 350.dp
        val avatarSize: Dp = if (isCompact) 62.dp else 72.dp
        val trustCardWidth: Dp = if (isCompact) 68.dp else 78.dp

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardWhite,
            shape = ProfileCardShape,
            border = BorderStroke(1.dp, FieldBorder),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(MintGlow)
                        .border(2.dp, BrandGreen.copy(alpha = 0.16f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(avatarSize - 12.dp)
                            .clip(CircleShape)
                            .background(BrandGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.name.trim().take(1).ifBlank { "আ" },
                            color = Color.White,
                            fontFamily = BanglaFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompact) 25.sp else 29.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(if (isCompact) 9.dp else 12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.name,
                        color = Ink,
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isCompact) 17.sp else 19.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Surface(
                        color = BadgeGreen,
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = VerifiedGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "যাচাইকৃত অবদানকারী",
                                color = VerifiedGreen,
                                fontFamily = BanglaFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.location,
                        color = InkMuted,
                        fontFamily = BanglaFamily,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "সদস্য হয়েছেন: ${uiState.memberSince}",
                        color = InkMuted,
                        fontFamily = BanglaFamily,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(if (isCompact) 6.dp else 10.dp))

                Surface(
                    modifier = Modifier
                        .width(trustCardWidth)
                        .height(94.dp),
                    color = CardWhite,
                    shape = RoundedCornerShape(13.dp),
                    border = BorderStroke(1.dp, FieldBorder),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = VerifiedGreen,
                            modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = uiState.trustScore,
                            color = Ink,
                            fontFamily = BanglaFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "বিশ্বাসযোগ্যতা\nস্কোর",
                            color = InkMuted,
                            fontFamily = BanglaFamily,
                            fontSize = 9.sp,
                            lineHeight = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileMetricsCard(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier
) {
    val metrics: List<ProfileMetric> = listOf(
        ProfileMetric(Icons.Outlined.Description, uiState.totalReports.toBanglaDigits(), "মোট রিপোর্ট", VerifiedGreen),
        ProfileMetric(Icons.Outlined.VerifiedUser, uiState.acceptedReports.toBanglaDigits(), "গৃহীত রিপোর্ট", MetricBlue),
        ProfileMetric(Icons.Outlined.Star, uiState.averageRating, "গড় রেটিং", MetricAmber),
        ProfileMetric(Icons.Outlined.Groups, uiState.contributorRank, "সেরা অবদানকারী", MetricPurple)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CardWhite,
        shape = ProfileCardShape,
        border = BorderStroke(1.dp, FieldBorder),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            metrics.forEachIndexed { index: Int, metric: ProfileMetric ->
                if (index > 0) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(58.dp)
                            .background(FieldBorder)
                    )
                }
                MetricItem(
                    metric = metric,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private data class ProfileMetric(
    val icon: ImageVector,
    val value: String,
    val label: String,
    val color: Color
)

@Composable
private fun MetricItem(
    metric: ProfileMetric,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = metric.icon,
            contentDescription = null,
            tint = metric.color,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = metric.value,
            color = Ink,
            fontFamily = BanglaFamily,
            fontWeight = FontWeight.Bold,
            fontSize = if (metric.value.length > 5) 14.sp else 17.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = metric.label,
            color = InkMuted,
            fontFamily = BanglaFamily,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SettingsCard(
    onEditProfile: () -> Unit,
    onNotifications: () -> Unit,
    onLanguage: () -> Unit,
    onHelp: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CardWhite,
        shape = ProfileCardShape,
        border = BorderStroke(1.dp, FieldBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ProfileSettingRow(
                icon = Icons.Outlined.PersonOutline,
                label = "প্রোফাইল সম্পাদনা",
                onClick = onEditProfile
            )
            SettingDivider()
            ProfileSettingRow(
                icon = Icons.Outlined.NotificationsNone,
                label = "নোটিফিকেশন সেটিংস",
                onClick = onNotifications
            )
            SettingDivider()
            ProfileSettingRow(
                icon = Icons.Outlined.Language,
                label = "ভাষা",
                trailingText = "বাংলা",
                onClick = onLanguage
            )
            SettingDivider()
            ProfileSettingRow(
                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                label = "সহায়তা ও সাপোর্ট",
                onClick = onHelp
            )
            SettingDivider()
            ProfileSettingRow(
                icon = Icons.AutoMirrored.Outlined.Logout,
                label = "লগআউট",
                iconTint = DangerRed,
                labelColor = DangerRed,
                onClick = onLogout
            )
        }
    }
}

@Composable
private fun ProfileSettingRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
    iconTint: Color = InkMuted,
    labelColor: Color = Ink
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 54.dp)
                .padding(start = 15.dp, end = 11.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                color = labelColor,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            trailingText?.let { value: String ->
                Text(
                    text = value,
                    color = InkMuted,
                    fontFamily = BanglaFamily,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = InkMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(start = 51.dp),
        color = FieldBorder
    )
}

@Composable
private fun EditProfileDialog(
    name: String,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.PersonOutline,
                contentDescription = null,
                tint = BrandGreen
            )
        },
        title = {
            Text(
                text = "প্রোফাইল সম্পাদনা",
                color = Ink,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("আপনার নাম", fontFamily = BanglaFamily) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = name.trim().length >= 2,
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen)
            ) {
                Text("সংরক্ষণ করুন", fontFamily = BanglaFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = InkMuted, fontFamily = BanglaFamily)
            }
        },
        containerColor = CardWhite
    )
}

@Composable
private fun NotificationSettingsDialog(
    isEnabled: Boolean,
    onToggle: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = null,
                tint = BrandGreen
            )
        },
        title = {
            Text(
                text = "নোটিফিকেশন সেটিংস",
                color = Ink,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ভাড়ার আপডেট",
                        color = Ink,
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "নতুন ভাড়া ও রিপোর্ট যাচাইয়ের খবর পান",
                        color = InkMuted,
                        fontFamily = BanglaFamily,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BrandGreen
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("সম্পন্ন", color = BrandGreen, fontFamily = BanglaFamily)
            }
        },
        containerColor = CardWhite
    )
}

@Composable
private fun InformationDialog(
    icon: ImageVector,
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandGreen
            )
        },
        title = {
            Text(
                text = title,
                color = Ink,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                color = InkMuted,
                fontFamily = BanglaFamily,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("ঠিক আছে", color = BrandGreen, fontFamily = BanglaFamily)
            }
        },
        containerColor = CardWhite
    )
}

@Composable
private fun LogoutDialog(
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = null,
                tint = DangerRed
            )
        },
        title = {
            Text(
                text = "লগআউট করবেন?",
                color = Ink,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "আবার ব্যবহার করতে আপনার মোবাইল নম্বর ও পাসওয়ার্ড দিয়ে লগইন করতে হবে।",
                color = InkMuted,
                fontFamily = BanglaFamily
            )
        },
        confirmButton = {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
            ) {
                Text("লগআউট", fontFamily = BanglaFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("থাকুন", color = BrandGreen, fontFamily = BanglaFamily)
            }
        },
        containerColor = CardWhite
    )
}

private fun Int.toBanglaDigits(): String = toString().map { digit: Char ->
    when (digit) {
        '0' -> '০'
        '1' -> '১'
        '2' -> '২'
        '3' -> '৩'
        '4' -> '৪'
        '5' -> '৫'
        '6' -> '৬'
        '7' -> '৭'
        '8' -> '৮'
        '9' -> '৯'
        else -> digit
    }
}.joinToString(separator = "")

@Preview(showBackground = true, widthDp = 393, heightDp = 851)
@Composable
private fun ProfileScreenPreview() {
    AppTheme {
        ProfileScreen(
            onNavigateHome = {},
            onNavigateToVaraDin = {},
            onLogout = {}
        )
    }
}
