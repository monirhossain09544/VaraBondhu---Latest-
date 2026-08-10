package com.rork.varabondhu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import com.rork.varabondhu.ui.localization.LocalizedText as Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.varabondhu.ui.theme.AppTheme
import com.rork.varabondhu.ui.theme.BanglaFamily
import com.rork.varabondhu.ui.theme.BrandGreen
import com.rork.varabondhu.ui.theme.CardWhite
import com.rork.varabondhu.ui.theme.FieldBorder
import com.rork.varabondhu.ui.theme.Ink
import com.rork.varabondhu.ui.theme.InkMuted
import com.rork.varabondhu.ui.theme.MintGlow

private val PrivacyBackground: Color = Color(0xFFFBFCFB)
private val PrivacyCardShape: RoundedCornerShape = RoundedCornerShape(16.dp)

/** Full privacy policy destination opened from the profile screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PrivacyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "গোপনীয়তা নীতি",
                        color = Ink,
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "ফিরে যান",
                            tint = Ink
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrivacyBackground)
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
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PrivacyIntroCard()

                PrivacyPolicySection(
                    icon = Icons.Outlined.Storage,
                    title = "আমরা কোন তথ্য সংগ্রহ করি"
                ) {
                    PrivacyBullet("অ্যাকাউন্ট চালাতে আপনার নাম, মোবাইল নম্বর এবং প্রোফাইলের তথ্য।")
                    PrivacyBullet("আপনার জমা দেওয়া ভাড়ার রিপোর্ট, রুট এবং পরিবহনের তথ্য।")
                    PrivacyBullet("আপনার অনুমতি থাকলে কাছাকাছি স্থান ও রুট দেখাতে ডিভাইসের লোকেশন।")
                }

                PrivacyPolicySection(
                    icon = Icons.Outlined.CheckCircle,
                    title = "তথ্য কীভাবে ব্যবহার করি"
                ) {
                    PrivacyBullet("সঠিক রুট ও ভাড়ার তথ্য দেখাতে এবং কমিউনিটির রিপোর্ট যাচাই করতে।")
                    PrivacyBullet("অ্যাপের নিরাপত্তা, নির্ভরযোগ্যতা এবং ব্যবহার-অভিজ্ঞতা উন্নত করতে।")
                    PrivacyBullet("আপনার পছন্দ অনুযায়ী গুরুত্বপূর্ণ আপডেট ও নোটিফিকেশন দিতে।")
                }

                PrivacyPolicySection(
                    icon = Icons.Outlined.Share,
                    title = "তথ্য শেয়ার ও প্রকাশ"
                ) {
                    PrivacyBullet("আপনার ব্যক্তিগত শনাক্তকারী তথ্য অন্য ব্যবহারকারীর কাছে প্রকাশ করা হয় না।")
                    PrivacyBullet("ভাড়ার রিপোর্ট পরিচয়বিহীন ও সমন্বিতভাবে কমিউনিটির জন্য দেখানো হতে পারে।")
                    PrivacyBullet("আইনি বাধ্যবাধকতা ছাড়া ব্যক্তিগত তথ্য বিক্রি করা হয় না।")
                }

                PrivacyPolicySection(
                    icon = Icons.Outlined.LocationOn,
                    title = "লোকেশন ও আপনার নিয়ন্ত্রণ"
                ) {
                    PrivacyBullet("লোকেশন কেবল আপনার অনুমতির ভিত্তিতে অ্যাপের প্রাসঙ্গিক সুবিধার জন্য ব্যবহৃত হয়।")
                    PrivacyBullet("ডিভাইসের সেটিংস থেকে যেকোনো সময় লোকেশন অনুমতি পরিবর্তন বা বন্ধ করতে পারবেন।")
                }

                PrivacyPolicySection(
                    icon = Icons.Outlined.Security,
                    title = "তথ্যের নিরাপত্তা ও সংরক্ষণ"
                ) {
                    PrivacyBullet("তথ্য সুরক্ষায় যুক্তিসঙ্গত প্রযুক্তিগত ও পরিচালনাগত ব্যবস্থা নেওয়া হয়।")
                    PrivacyBullet("সেবা প্রদান বা আইনগত প্রয়োজনের চেয়ে বেশি সময় তথ্য রাখা হয় না।")
                    PrivacyBullet("ইন্টারনেটভিত্তিক কোনো ব্যবস্থাই শতভাগ ঝুঁকিমুক্ত নয়।")
                }

                PrivacyPolicySection(
                    icon = Icons.Outlined.DeleteOutline,
                    title = "আপনার অধিকার"
                ) {
                    PrivacyBullet("আপনার প্রোফাইলের তথ্য দেখা ও প্রয়োজন অনুযায়ী সংশোধন করতে পারবেন।")
                    PrivacyBullet("অ্যাকাউন্ট ও সংশ্লিষ্ট ব্যক্তিগত তথ্য মুছে দেওয়ার অনুরোধ করতে পারবেন।")
                    PrivacyBullet("নীতি পরিবর্তন হলে এই পাতায় হালনাগাদের তারিখ জানানো হবে।")
                }

                HorizontalDivider(color = FieldBorder)
                Text(
                    text = "এই নীতি সম্পর্কে প্রশ্ন থাকলে প্রোফাইলের ‘সহায়তা ও সাপোর্ট’ অপশন ব্যবহার করুন।",
                    color = InkMuted,
                    fontFamily = BanglaFamily,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun PrivacyIntroCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BrandGreen,
        shape = PrivacyCardShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(27.dp)
                )
            }
            Spacer(modifier = Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "আপনার তথ্য, আপনার নিয়ন্ত্রণ",
                    color = Color.White,
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    text = "VaraBondhu কীভাবে আপনার তথ্য ব্যবহার ও সুরক্ষিত রাখে তা সহজ ভাষায় জানুন।",
                    color = Color.White.copy(alpha = 0.84f),
                    fontFamily = BanglaFamily,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "সর্বশেষ হালনাগাদ: ৯ আগস্ট ২০২৬",
                    color = Color.White.copy(alpha = 0.72f),
                    fontFamily = BanglaFamily,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun PrivacyPolicySection(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CardWhite,
        shape = PrivacyCardShape,
        border = BorderStroke(1.dp, FieldBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MintGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = Ink,
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
            }
            content()
        }
    }
}

@Composable
private fun PrivacyBullet(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(BrandGreen)
        )
        Spacer(modifier = Modifier.width(9.dp))
        Text(
            text = text,
            color = InkMuted,
            fontFamily = BanglaFamily,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 851)
@Composable
private fun PrivacyPolicyScreenPreview() {
    AppTheme {
        PrivacyPolicyScreen(onNavigateBack = {})
    }
}
