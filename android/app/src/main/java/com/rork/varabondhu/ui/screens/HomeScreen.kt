package com.rork.varabondhu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.varabondhu.R
import com.rork.varabondhu.ui.theme.AppTheme
import com.rork.varabondhu.ui.theme.BanglaFamily
import com.rork.varabondhu.ui.theme.BrandGreen
import com.rork.varabondhu.ui.theme.ButtonGreen
import com.rork.varabondhu.ui.theme.CardWhite
import com.rork.varabondhu.ui.theme.FieldBorder
import com.rork.varabondhu.ui.theme.FieldPlaceholder
import com.rork.varabondhu.ui.theme.Ink
import com.rork.varabondhu.ui.theme.InkMuted

private val HomeBackground = Color(0xFFFBFCFB)
private val HeroGreen = Color(0xFFD9F5DB)
private val DestinationRed = Color(0xFFE64A3B)
private val NotificationRed = Color(0xFFE62F42)
private val HeroHeight = 224.dp
private val LandscapeHeight = 150.dp
private val SectionGreen = Color(0xFF0B7B37)
private val PromoMint = Color(0xFFE3F5E8)
private val BottomBarBorder = Color(0xFFE7ECE8)

private data class RecentSearch(
    val origin: String,
    val destination: String,
    val time: String
)

private data class PopularRoute(
    val origin: String,
    val destination: String,
    val reports: String,
    val accent: Color,
    val iconBackground: Color
)

private val recentSearches: List<RecentSearch> = listOf(
    RecentSearch("মিরপুর ১০", "ফার্মগেট", "২ ঘণ্টা আগে"),
    RecentSearch("মোহাম্মদপুর", "ধানমন্ডি ৩২", "গতকাল"),
    RecentSearch("উত্তরা সেক্টর ৭", "এয়ারপোর্ট", "২ দিন আগে")
)

private val popularRoutes: List<PopularRoute> = listOf(
    PopularRoute("মিরপুর ১০", "ফার্মগেট", "১২৫+ রিপোর্ট", Color(0xFF149852), Color(0xFFE3F5E9)),
    PopularRoute("মোহাম্মদপুর", "ধানমন্ডি ৩২", "৮৯+ রিপোর্ট", Color(0xFFF19A2A), Color(0xFFFFF0DA)),
    PopularRoute("উত্তরা", "এয়ারপোর্ট", "৭৬+ রিপোর্ট", Color(0xFF745BD7), Color(0xFFEDE8FF)),
    PopularRoute("ফার্মগেট", "মতিঝিল", "৫৮+ রিপোর্ট", Color(0xFFE2515C), Color(0xFFFFE7E9))
)

/** Complete Home experience with route search, discovery content, contribution, and navigation. */
@Composable
fun HomeScreen(
    origin: String = "মিরপুর ১০",
    destination: String = "ফার্মগেট",
    onOriginClick: () -> Unit = {},
    onDestinationClick: () -> Unit = {},
    onSwapLocations: () -> Unit = {},
    onNavigateToVaraDin: () -> Unit = {},
    onNavigateToFareResult: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navigationBottomInset = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HomeBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeHero()

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = CardWhite,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    RouteSearchCard(
                        origin = origin,
                        destination = destination,
                        onOriginClick = onOriginClick,
                        onDestinationClick = onDestinationClick,
                        onSwapLocations = onSwapLocations,
                        onSearch = onNavigateToFareResult,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    RecentSearchSection()
                    Spacer(modifier = Modifier.height(18.dp))
                    PopularRoutesSection()
                    Spacer(modifier = Modifier.height(18.dp))
                    FareContributionBanner(
                        onNavigateToVaraDin = onNavigateToVaraDin
                    )
                    Spacer(modifier = Modifier.height(96.dp + navigationBottomInset))
                }
            }
        }

        HomeBottomNavigation(
            onNavigateToVaraDin = onNavigateToVaraDin,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun HomeHero(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(HeroHeight)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(HeroGreen)
    ) {
        Image(
            painter = painterResource(R.drawable.home_city_landscape),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(LandscapeHeight)
        )

        JourneyMessageCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 10.dp, end = 10.dp, bottom = 8.dp)
        )

        NotificationAction(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 10.dp, end = 14.dp)
        )
    }
}

@Composable
private fun NotificationAction(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        ElevatedCircleButton(
            icon = Icons.Outlined.NotificationsNone,
            contentDescription = "নোটিফিকেশন দেখুন",
            onClick = {}
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-2).dp, y = 2.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(NotificationRed)
        )
    }
}

@Composable
private fun ElevatedCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = CardWhite,
        shadowElevation = 6.dp
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Ink,
                modifier = Modifier.size(23.dp)
            )
        }
    }
}

@Composable
private fun JourneyMessageCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(86.dp),
            color = CardWhite.copy(alpha = 0.98f),
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 98.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = BrandGreen,
                    modifier = Modifier.size(23.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = "আপনার যাত্রা\nসহজ হোক প্রতিদিন",
                        color = Ink,
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "দ্রুত খুঁজুন, জেনে নিন, নিরাপদে পৌঁছান",
                        color = InkMuted,
                        fontFamily = BanglaFamily,
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Image(
            painter = painterResource(R.drawable.home_route_hand),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp)
                .width(98.dp)
                .height(138.dp)
        )
    }
}

@Composable
private fun RouteSearchCard(
    origin: String,
    destination: String,
    onOriginClick: () -> Unit,
    onDestinationClick: () -> Unit,
    onSwapLocations: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = CardWhite,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)) {
            RouteField(
                value = origin,
                placeholder = "আমি কোথা থেকে যাচ্ছি?",
                markerColor = BrandGreen,
                onClick = onOriginClick,
                trailingContent = {
                    RouteTrailingIcon(
                        icon = Icons.Outlined.MyLocation,
                        contentDescription = "বর্তমান অবস্থান নিন",
                        onClick = onOriginClick
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            RouteField(
                value = destination,
                placeholder = "আমি কোথায় যেতে চাই?",
                markerColor = DestinationRed,
                onClick = onDestinationClick,
                trailingContent = {
                    RouteTrailingIcon(
                        icon = Icons.Outlined.SwapVert,
                        contentDescription = "স্থান অদলবদল করুন",
                        onClick = onSwapLocations
                    )
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = CircleShape,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 7.dp,
                    vertical = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonGreen,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "যান খুঁজুন",
                            fontFamily = BanglaFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = "সার্চ শুরু করুন",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteField(
    value: String,
    placeholder: String,
    markerColor: Color,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(11.dp))
            .border(1.dp, FieldBorder, RoundedCornerShape(11.dp))
            .background(CardWhite)
            .clickable(onClick = onClick)
            .padding(start = 10.dp, end = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = markerColor,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = value.ifBlank { placeholder },
            modifier = Modifier.weight(1f),
            color = if (value.isBlank()) FieldPlaceholder else Ink,
            fontFamily = BanglaFamily,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        trailingContent()
    }
}

@Composable
private fun RouteTrailingIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .width(1.dp)
                .height(20.dp)
                .background(FieldBorder)
        )
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = BrandGreen,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun RecentSearchSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = "আপনার সাম্প্রতিক সার্চ",
            action = "সব দেখুন",
            onAction = {}
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            color = CardWhite,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 2.dp
        ) {
            Column {
                recentSearches.forEachIndexed { index: Int, search: RecentSearch ->
                    RecentSearchRow(search = search)
                    if (index < recentSearches.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 42.dp, end = 12.dp),
                            thickness = 1.dp,
                            color = FieldBorder
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: String? = null,
    onAction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .padding(start = 10.dp, end = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Ink,
            fontFamily = BanglaFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
        if (action != null) {
            TextButton(onClick = onAction) {
                Text(
                    text = action,
                    color = SectionGreen,
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun RecentSearchRow(
    search: RecentSearch,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = {},
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = SectionGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(9.dp))
            Text(
                text = search.origin,
                modifier = Modifier.weight(0.86f),
                color = Ink,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "→",
                modifier = Modifier.padding(horizontal = 5.dp),
                color = FieldPlaceholder,
                fontSize = 14.sp
            )
            Text(
                text = search.destination,
                modifier = Modifier.weight(1f),
                color = Ink,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = search.time,
                modifier = Modifier.width(58.dp),
                color = InkMuted,
                fontFamily = BanglaFamily,
                fontSize = 9.sp,
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "রুটটি খুলুন",
                tint = FieldPlaceholder,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun PopularRoutesSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(title = "জনপ্রিয় রুট")
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            popularRoutes.forEach { route: PopularRoute ->
                PopularRouteCard(route = route)
            }
        }
    }
}

@Composable
private fun PopularRouteCard(
    route: PopularRoute,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = {},
        modifier = modifier
            .width(112.dp)
            .height(118.dp),
        color = CardWhite,
        shape = RoundedCornerShape(15.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F2F0)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(route.iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.DirectionsBus,
                    contentDescription = null,
                    tint = route.accent,
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = route.origin,
                color = Ink,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "→",
                color = FieldPlaceholder,
                fontSize = 11.sp,
                lineHeight = 11.sp
            )
            Text(
                text = route.destination,
                color = Ink,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = route.reports,
                color = InkMuted,
                fontFamily = BanglaFamily,
                fontSize = 9.sp,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun FareContributionBanner(
    onNavigateToVaraDin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(124.dp),
        color = PromoMint,
        shape = RoundedCornerShape(17.dp)
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.home_fare_contribution),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                alignment = Alignment.BottomStart,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .width(112.dp)
                    .height(108.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 90.dp, end = 90.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "আপনার ভাড়া দিন, সবার উপকার করুন",
                    color = Ink,
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "আপনার দেওয়া সঠিক তথ্য অন্যদের\nসঠিক সিদ্ধান্ত নিতে সাহায্য করবে।",
                    color = Ink,
                    fontFamily = BanglaFamily,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
            
            Button(
                onClick = onNavigateToVaraDin,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 20.dp)
                    .width(78.dp)
                    .height(34.dp),
                shape = RoundedCornerShape(9.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "ভাড়া দিন",
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun HomeBottomNavigation(
    onNavigateToVaraDin: () -> Unit,
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
            border = androidx.compose.foundation.BorderStroke(1.dp, BottomBarBorder),
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
            BottomNavItem(
                icon = Icons.Rounded.Home,
                label = "হোম",
                isSelected = true,
                modifier = Modifier.weight(1f)
            )
            BottomNavItem(
                icon = Icons.Rounded.BarChart,
                label = "চার্ট রেটিং",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f))
            BottomNavItem(
                icon = Icons.Rounded.Assessment,
                label = "রিপোর্ট",
                modifier = Modifier.weight(1f)
            )
            BottomNavItem(
                icon = Icons.Rounded.PersonOutline,
                label = "প্রোফাইল",
                modifier = Modifier.weight(1f)
            )
        }

        AddFareNavItem(
            onClick = onNavigateToVaraDin,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(76.dp)
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val itemColor: Color = if (isSelected) SectionGreen else InkMuted
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {},
            modifier = Modifier.size(36.dp)
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

@Composable
private fun AddFareNavItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = onClick,
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

@Preview(showBackground = true, widthDp = 393, heightDp = 851)
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen()
    }
}
