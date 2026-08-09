package com.rork.varabondhu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.BubbleChart
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.varabondhu.location.LocationDefaults
import com.rork.varabondhu.location.LocationPlace
import com.rork.varabondhu.ui.theme.BanglaFamily
import com.rork.varabondhu.ui.theme.Ink
import com.rork.varabondhu.ui.theme.PageWhite
import com.rork.varabondhu.ui.theme.BrandGreen

@Composable
fun FareResultScreen(
    onNavigateBack: () -> Unit,
    origin: LocationPlace = LocationDefaults.mirpur,
    destination: LocationPlace = LocationDefaults.farmgate,
    onSwapLocations: () -> Unit = {}
) {
    val mockResults = listOf(
        VehicleResult(
            title = "রিকশা",
            imageRes = com.rork.varabondhu.R.drawable.ic_rickshaw,
            badge = "সবচেয়ে জনপ্রিয়",
            badgeColor = com.rork.varabondhu.ui.theme.MintGlow,
            badgeTextColor = BrandGreen,
            primaryPriceTitle = "ন্যায্য ভাড়া",
            primaryPrice = "৳ 70",
            secondaryPriceTitle = "সাধারণ রেঞ্জ",
            secondaryPrice = "৳ 60 - 80",
            reportsCount = "153",
            lastUpdated = "2 ঘণ্টা আগে",
            reliability = "উচ্চ",
            reliabilityColor = BrandGreen
        ),
        VehicleResult(
            title = "অটো / CNG",
            imageRes = com.rork.varabondhu.R.drawable.ic_cng,
            primaryPriceTitle = "মিটার ভাড়া (আনুমানিক)",
            primaryPrice = "৳ 120",
            secondaryPriceTitle = "সাধারণ রেঞ্জ",
            secondaryPrice = "৳ 100 - 140",
            reportsCount = "98",
            lastUpdated = "1 ঘণ্টা আগে",
            reliability = "মাঝারি",
            reliabilityColor = Color(0xFFF39C12) // Orange
        ),
        VehicleResult(
            title = "বাস",
            imageRes = com.rork.varabondhu.R.drawable.ic_bus_new,
            primaryPriceTitle = "আনুমানিক ভাড়া",
            primaryPrice = "৳ 20",
            secondaryPriceTitle = "সম্ভাব্য রুট",
            secondaryPrice = "", // handled by routes
            isSecondaryRoutes = true,
            reportsCount = "212",
            lastUpdated = "30 মিনিট আগে",
            reliability = "উচ্চ",
            reliabilityColor = BrandGreen
        )
    )

    Scaffold(
        topBar = {
            ResultTopAppBar(onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            // Removed StickyInfoBanner as requested
        },
        containerColor = PageWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RouteSummaryCard(
                origin = origin,
                destination = destination,
                onSwapLocations = onSwapLocations
            )
            Spacer(modifier = Modifier.height(12.dp))
            VehicleFilterTabs()
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                mockResults.forEach { result ->
                    FareResultCard(result = result)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

data class VehicleResult(
    val title: String,
    val imageRes: Int,
    val badge: String? = null,
    val badgeColor: Color = Color.Transparent,
    val badgeTextColor: Color = Color.Transparent,
    val primaryPriceTitle: String,
    val primaryPrice: String,
    val secondaryPriceTitle: String,
    val secondaryPrice: String,
    val isSecondaryRoutes: Boolean = false,
    val reportsCount: String,
    val lastUpdated: String,
    val reliability: String,
    val reliabilityColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultTopAppBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "ফলাফল",
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Ink,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Ink
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share",
                    tint = Ink
                )
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Ink
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PageWhite
        )
    )
}

@Composable
fun RouteSummaryCard(
    origin: LocationPlace,
    destination: LocationPlace,
    onSwapLocations: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timeline left
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn, // TODO: change to dot if needed, but mockup shows green pin or dot
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    // Dashed line
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .width(2.dp)
                            .background(Color.LightGray) // We should use a custom dashed line modifier, but solid is fine for quick placeholder
                    )
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Addresses
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = origin.displayName,
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Ink
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = destination.displayName,
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Ink
                    )
                }

                // Swap icon
                Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = BrandGreen.copy(alpha = 0.1f),
                    modifier = Modifier.size(36.dp),
                    onClick = onSwapLocations
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.SwapVert,
                            contentDescription = "Swap",
                            tint = BrandGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = com.rork.varabondhu.ui.theme.FieldBorder
            )

            // Date & Passengers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime, // using time icon for date
                        contentDescription = null,
                        tint = com.rork.varabondhu.ui.theme.FieldPlaceholder,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "আজ, 5:30 PM",
                        fontFamily = BanglaFamily,
                        fontSize = 13.sp,
                        color = com.rork.varabondhu.ui.theme.InkMuted
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = com.rork.varabondhu.ui.theme.FieldPlaceholder,
                        modifier = Modifier.size(16.dp).padding(start = 2.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .width(1.dp)
                        .height(16.dp)
                        .background(com.rork.varabondhu.ui.theme.FieldBorder)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.PersonOutline,
                        contentDescription = null,
                        tint = com.rork.varabondhu.ui.theme.FieldPlaceholder,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "১ জন",
                        fontFamily = BanglaFamily,
                        fontSize = 13.sp,
                        color = com.rork.varabondhu.ui.theme.InkMuted
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = com.rork.varabondhu.ui.theme.FieldPlaceholder,
                        modifier = Modifier.size(16.dp).padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

data class VehicleTabItem(val title: String, val iconRes: Int)

@Composable
fun VehicleFilterTabs() {
    val filters = listOf(
        VehicleTabItem("রিকশা", com.rork.varabondhu.R.drawable.ic_rickshaw),
        VehicleTabItem("অটো / CNG", com.rork.varabondhu.R.drawable.ic_cng),
        VehicleTabItem("বাস", com.rork.varabondhu.R.drawable.ic_bus_new),
        VehicleTabItem("লেগুনা / টেম্পো", com.rork.varabondhu.R.drawable.ic_micro)
    )
    
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters.size) { index ->
            val filter = filters[index]
            val isSelected = index == 0
            
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                color = if (isSelected) BrandGreen else Color.White,
                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, com.rork.varabondhu.ui.theme.FieldBorder) else null,
                onClick = { /* TODO */ }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = filter.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = filter.title,
                        fontFamily = BanglaFamily,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Ink
                    )
                }
            }
        }
    }
}

@Composable
fun FareResultCard(result: VehicleResult) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFAFCFA), // Very slight greenish-white tint
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, com.rork.varabondhu.ui.theme.FieldBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier.width(90.dp).height(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = result.imageRes),
                        contentDescription = result.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = 1.5f,
                                scaleY = 1.5f,
                                translationX = -40f
                            ),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.title,
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Ink
                    )
                    if (result.badge != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            color = result.badgeColor
                        ) {
                            Text(
                                text = result.badge,
                                fontFamily = BanglaFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                color = result.badgeTextColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = com.rork.varabondhu.ui.theme.FieldBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(4.dp))

            // Pricing Area
            if (result.isSecondaryRoutes) {
                // Bus layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                        Text(
                            text = result.primaryPriceTitle,
                            fontFamily = BanglaFamily,
                            fontSize = 12.sp,
                            color = com.rork.varabondhu.ui.theme.InkMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.primaryPrice,
                            fontFamily = BanglaFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = BrandGreen
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(com.rork.varabondhu.ui.theme.FieldBorder)
                    )
                    Column(modifier = Modifier.weight(1.4f).padding(start = 12.dp), horizontalAlignment = Alignment.Start) {
                        Text(
                            text = result.secondaryPriceTitle,
                            fontFamily = BanglaFamily,
                            fontSize = 12.sp,
                            color = com.rork.varabondhu.ui.theme.InkMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("রুট ১", "রুট ২", "রুট ৩").forEach { route ->
                                Surface(
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                    color = Color.Transparent,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, com.rork.varabondhu.ui.theme.FieldBorder)
                                ) {
                                    Text(
                                        text = route,
                                        fontFamily = BanglaFamily,
                                        fontSize = 10.sp,
                                        color = Ink,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                color = BrandGreen.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "+2",
                                    fontFamily = BanglaFamily,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Rickshaw / CNG layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = result.primaryPriceTitle,
                                fontFamily = BanglaFamily,
                                fontSize = 12.sp,
                                color = com.rork.varabondhu.ui.theme.InkMuted
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Outlined.Info,
                                contentDescription = null,
                                tint = com.rork.varabondhu.ui.theme.InkMuted,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.primaryPrice,
                            fontFamily = BanglaFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = BrandGreen
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(com.rork.varabondhu.ui.theme.FieldBorder)
                    )
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp), horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = result.secondaryPriceTitle,
                                fontFamily = BanglaFamily,
                                fontSize = 12.sp,
                                color = com.rork.varabondhu.ui.theme.InkMuted
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Outlined.Info,
                                contentDescription = null,
                                tint = com.rork.varabondhu.ui.theme.InkMuted,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.secondaryPrice,
                            fontFamily = BanglaFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Ink
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = com.rork.varabondhu.ui.theme.FieldBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(4.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    icon = androidx.compose.material.icons.Icons.Outlined.BubbleChart,
                    label = "মোট রিপোর্ট",
                    value = result.reportsCount,
                    valueColor = Ink
                )
                Box(modifier = Modifier.width(1.dp).height(20.dp).background(com.rork.varabondhu.ui.theme.FieldBorder))
                StatItem(
                    icon = androidx.compose.material.icons.Icons.Outlined.AccessTime,
                    label = "আপডেট হয়েছে",
                    value = result.lastUpdated,
                    valueColor = Ink
                )
                Box(modifier = Modifier.width(1.dp).height(20.dp).background(com.rork.varabondhu.ui.theme.FieldBorder))
                StatItem(
                    icon = androidx.compose.material.icons.Icons.Outlined.VerifiedUser,
                    label = "বিশ্বাসযোগ্যতা",
                    value = result.reliability,
                    valueColor = result.reliabilityColor,
                    iconTint = result.reliabilityColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = com.rork.varabondhu.ui.theme.FieldBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(4.dp))

            // Details Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "বিস্তারিত দেখুন ",
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = BrandGreen
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = BrandGreen,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun StatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, valueColor: Color, iconTint: Color = com.rork.varabondhu.ui.theme.InkMuted) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontFamily = BanglaFamily,
                fontSize = 10.sp,
                color = com.rork.varabondhu.ui.theme.InkMuted
            )
            Text(
                text = value,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = valueColor
            )
        }
    }
}

@Composable
fun StickyInfoBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp).windowInsetsPadding(WindowInsets.navigationBars),
        color = BrandGreen,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Outlined.FavoriteBorder, // Shield replacement
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "এই ভাড়াগুলো বাস্তব যাত্রীদের রিপোর্টের উপর ভিত্তি করে। অতিরিক্ত ভাড়া চাইলে দরদাম করুন।",
                fontFamily = BanglaFamily,
                fontSize = 12.sp,
                color = Color.White,
                lineHeight = 16.sp
            )
        }
    }
}
