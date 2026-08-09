package com.rork.varabondhu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
import com.rork.varabondhu.ui.theme.PageWhite

private val ProfileHeroGreen = Color(0xFFDDF4E3)
private val VerifiedGreen = Color(0xFF087B38)
private val PendingOrange = Color(0xFFB8690B)
private val ProfileCardShape = RoundedCornerShape(18.dp)

private enum class ProfileInfoDialog(
    val title: String,
    val message: String,
    val icon: ImageVector
) {
    PHONE(
        title = "মোবাইল নম্বর পরিবর্তন",
        message = "আপনার অ্যাকাউন্ট নিরাপদ রাখতে নতুন নম্বরটি OTP দিয়ে যাচাই করতে হবে। এই সুবিধাটি শিগগিরই এখানে পাওয়া যাবে।",
        icon = Icons.Outlined.VerifiedUser
    ),
    PRIVACY(
        title = "গোপনীয়তা ও নিরাপত্তা",
        message = "আপনার দেওয়া ভাড়ার তথ্য সবার উপকারে ব্যবহার হয়, তবে আপনার নাম বা মোবাইল নম্বর অন্য ব্যবহারকারীদের দেখানো হয় না।",
        icon = Icons.Outlined.Security
    ),
    HELP(
        title = "সাহায্য ও যোগাযোগ",
        message = "লোকেশন, ভাড়া খোঁজা বা রিপোর্ট জমা দিতে সমস্যা হলে অ্যাপের তথ্য যাচাই করে আবার চেষ্টা করুন। জরুরি সহায়তার জন্য সাপোর্ট সুবিধা শিগগিরই যুক্ত হবে।",
        icon = Icons.AutoMirrored.Outlined.HelpOutline
    ),
    ABOUT(
        title = "VaraBondhu সম্পর্কে",
        message = "VaraBondhu মানুষের দেওয়া সাম্প্রতিক তথ্য থেকে ন্যায্য ভাড়া বুঝতে সাহায্য করে। আপনার একটি সঠিক রিপোর্ট অন্য অনেক যাত্রীর সিদ্ধান্ত সহজ করতে পারে।",
        icon = Icons.Outlined.Info
    )
}

/** Bengali-first account, contribution history, saved routes, preferences, and support screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateHome: () -> Unit,
    onNavigateToVaraDin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isEditingName by rememberSaveable { mutableStateOf(false) }
    var isConfirmingLogout by rememberSaveable { mutableStateOf(false) }
    var infoDialog by remember { mutableStateOf<ProfileInfoDialog?>(null) }
    var editedName by rememberSaveable(uiState.name) { mutableStateOf(uiState.name) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PageWhite,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "প্রোফাইল",
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Ink
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PageWhite)
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 4.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "identity") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ProfileHeroGreen, Color(0xFFF4FBF6))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 32.dp, y = (-28).dp)
                            .size(118.dp)
                            .clip(CircleShape)
                            .background(BrandGreen.copy(alpha = 0.06f))
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(BrandGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.name.trim().take(1).ifBlank { "আ" },
                                color = Color.White,
                                fontFamily = BanglaFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.name,
                                color = Ink,
                                fontFamily = BanglaFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.phone,
                                color = InkMuted,
                                fontFamily = BanglaFamily,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(7.dp))
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = CardWhite.copy(alpha = 0.88f),
                                border = BorderStroke(1.dp, BrandGreen.copy(alpha = 0.15f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.VerifiedUser,
                                        contentDescription = null,
                                        tint = VerifiedGreen,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "যাচাইকৃত সদস্য",
                                        color = VerifiedGreen,
                                        fontFamily = BanglaFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = { isEditingName = true },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CardWhite.copy(alpha = 0.9f))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "নাম পরিবর্তন করুন",
                                tint = BrandGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            item(key = "impact-heading") {
                Column(modifier = Modifier.padding(top = 4.dp, start = 4.dp)) {
                    Text(
                        text = "আপনার অবদান",
                        color = Ink,
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "আপনার তথ্য কতটা কাজে এসেছে",
                        color = InkMuted,
                        fontFamily = BanglaFamily,
                        fontSize = 12.sp
                    )
                }
            }

            item(key = "impact-card") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CardWhite,
                    shape = ProfileCardShape,
                    border = BorderStroke(1.dp, FieldBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val impactItems = listOf(
                            Triple(Icons.Outlined.History, uiState.contributionCount, "ভাড়া দিয়েছেন"),
                            Triple(Icons.Outlined.CheckCircleOutline, uiState.verifiedCount, "যাচাই হয়েছে"),
                            Triple(Icons.Outlined.VerifiedUser, uiState.helpedCount, "মানুষ উপকৃত")
                        )
                        impactItems.forEachIndexed { index, (icon, value, label) ->
                            if (index > 0) {
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(48.dp)
                                        .background(FieldBorder)
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = BrandGreen,
                                    modifier = Modifier.size(19.dp)
                                )
                                Text(
                                    text = value.toBanglaDigits(),
                                    color = Ink,
                                    fontFamily = BanglaFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = label,
                                    color = InkMuted,
                                    fontFamily = BanglaFamily,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            item(key = "contribution-heading") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সাম্প্রতিক দেওয়া ভাড়া",
                        color = Ink,
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            items(uiState.contributions, key = ProfileContribution::id) { contribution ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CardWhite,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, FieldBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(MintGlow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DirectionsBus,
                                contentDescription = null,
                                tint = BrandGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(11.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contribution.route,
                                color = Ink,
                                fontFamily = BanglaFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${contribution.vehicle}  •  ${contribution.date}",
                                color = InkMuted,
                                fontFamily = BanglaFamily,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = if (contribution.isVerified) "যাচাই হয়েছে" else "যাচাই চলছে",
                                color = if (contribution.isVerified) VerifiedGreen else PendingOrange,
                                fontFamily = BanglaFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = contribution.fare,
                            color = BrandGreen,
                            fontFamily = BanglaFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                }
            }

            item(key = "saved-heading") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, start = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সেভ করা রুট",
                        color = Ink,
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            }

            if (uiState.savedRoutes.isEmpty()) {
                item(key = "saved-empty") {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = CardWhite,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, FieldBorder)
                    ) {
                        Text(
                            text = "সেভ করা কোনো রুট নেই",
                            modifier = Modifier.padding(18.dp),
                            color = InkMuted,
                            fontFamily = BanglaFamily,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(uiState.savedRoutes, key = ProfileSavedRoute::id) { route ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = CardWhite,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, FieldBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 14.dp, top = 11.dp, bottom = 11.dp, end = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MintGlow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FavoriteBorder,
                                    contentDescription = null,
                                    tint = BrandGreen,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(11.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = route.origin,
                                    color = Ink,
                                    fontFamily = BanglaFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "থেকে ${route.destination}",
                                    color = InkMuted,
                                    fontFamily = BanglaFamily,
                                    fontSize = 12.sp
                                )
                            }
                            IconButton(onClick = { viewModel.removeSavedRoute(route.id) }) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = "রুটটি সরান",
                                    tint = InkMuted,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }
            }

            item(key = "settings-heading") {
                Text(
                    text = "অ্যাকাউন্ট ও সহায়তা",
                    color = Ink,
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                )
            }

            item(key = "settings-card") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CardWhite,
                    shape = ProfileCardShape,
                    border = BorderStroke(1.dp, FieldBorder)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val settings = listOf(
                            Triple(Icons.Outlined.VerifiedUser, "মোবাইল নম্বর", ProfileInfoDialog.PHONE),
                            Triple(Icons.Outlined.Security, "গোপনীয়তা ও নিরাপত্তা", ProfileInfoDialog.PRIVACY),
                            Triple(Icons.AutoMirrored.Outlined.HelpOutline, "সাহায্য ও যোগাযোগ", ProfileInfoDialog.HELP),
                            Triple(Icons.Outlined.Info, "অ্যাপ সম্পর্কে", ProfileInfoDialog.ABOUT)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MintGlow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.NotificationsNone,
                                    contentDescription = null,
                                    tint = BrandGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(11.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "নোটিফিকেশন",
                                    color = Ink,
                                    fontFamily = BanglaFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "রুটের নতুন ভাড়া ও যাচাইয়ের আপডেট",
                                    color = InkMuted,
                                    fontFamily = BanglaFamily,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = uiState.hasNotificationsEnabled,
                                onCheckedChange = { viewModel.toggleNotifications() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BrandGreen
                                )
                            )
                        }

                        settings.forEach { (icon, label, dialog) ->
                            HorizontalDivider(color = FieldBorder, modifier = Modifier.padding(start = 63.dp))
                            Surface(
                                onClick = { infoDialog = dialog },
                                color = Color.Transparent,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(MintGlow),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = BrandGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(11.dp))
                                    Text(
                                        text = label,
                                        color = Ink,
                                        fontFamily = BanglaFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Outlined.ChevronRight,
                                        contentDescription = null,
                                        tint = InkMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item(key = "logout") {
                Surface(
                    onClick = { isConfirmingLogout = true },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFFF7F6),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.16f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null,
                            tint = DangerRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "লগ আউট",
                            color = DangerRed,
                            fontFamily = BanglaFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            item(key = "version") {
                Text(
                    text = "VaraBondhu  •  সংস্করণ ১.০",
                    color = InkMuted,
                    fontFamily = BanglaFamily,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, bottom = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }

    if (isEditingName) {
        AlertDialog(
            onDismissRequest = { isEditingName = false },
            title = {
                Text(
                    text = "নাম পরিবর্তন করুন",
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it.take(50) },
                    label = { Text("আপনার নাম", fontFamily = BanglaFamily) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateName(editedName)
                        isEditingName = false
                    },
                    enabled = editedName.trim().length >= 2,
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen)
                ) {
                    Text("সংরক্ষণ করুন", fontFamily = BanglaFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditingName = false }) {
                    Text("বাতিল", color = InkMuted, fontFamily = BanglaFamily)
                }
            },
            containerColor = CardWhite
        )
    }

    infoDialog?.let { dialog ->
        AlertDialog(
            onDismissRequest = { infoDialog = null },
            icon = {
                Icon(
                    imageVector = dialog.icon,
                    contentDescription = null,
                    tint = BrandGreen
                )
            },
            title = {
                Text(
                    text = dialog.title,
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
            },
            text = {
                Text(
                    text = dialog.message,
                    fontFamily = BanglaFamily,
                    color = InkMuted,
                    lineHeight = 21.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { infoDialog = null }) {
                    Text("ঠিক আছে", color = BrandGreen, fontFamily = BanglaFamily)
                }
            },
            containerColor = CardWhite
        )
    }

    if (isConfirmingLogout) {
        AlertDialog(
            onDismissRequest = { isConfirmingLogout = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = null,
                    tint = DangerRed
                )
            },
            title = {
                Text(
                    text = "লগ আউট করবেন?",
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
            },
            text = {
                Text(
                    text = "আবার ব্যবহার করতে আপনার মোবাইল নম্বর ও পাসওয়ার্ড দিয়ে লগইন করতে হবে।",
                    fontFamily = BanglaFamily,
                    color = InkMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("লগ আউট", fontFamily = BanglaFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { isConfirmingLogout = false }) {
                    Text("থাকুন", color = BrandGreen, fontFamily = BanglaFamily)
                }
            },
            containerColor = CardWhite
        )
    }
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
