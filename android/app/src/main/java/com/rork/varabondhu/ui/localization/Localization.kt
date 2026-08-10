package com.rork.varabondhu.ui.localization

import android.app.Application
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Languages available throughout VaraBondhu. */
enum class AppLanguage(val storageValue: String) {
    BANGLA("bn"),
    ENGLISH("en")
}

val LocalAppLanguage = staticCompositionLocalOf<AppLanguage> { AppLanguage.BANGLA }

@Composable
fun AppLanguageProvider(language: AppLanguage, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppLanguage provides language, content = content)
}

/** Persists and exposes the active language for the complete app session. */
class LanguageViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences(PREFERENCES_NAME, 0)
    private val _language = MutableStateFlow(
        AppLanguage.entries.firstOrNull {
            it.storageValue == preferences.getString(LANGUAGE_KEY, AppLanguage.BANGLA.storageValue)
        } ?: AppLanguage.BANGLA
    )
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun selectLanguage(language: AppLanguage) {
        if (_language.value == language) return
        preferences.edit().putString(LANGUAGE_KEY, language.storageValue).apply()
        _language.value = language
    }

    private companion object {
        const val PREFERENCES_NAME = "vara_bondhu_preferences"
        const val LANGUAGE_KEY = "app_language"
    }
}

/** Drop-in Material text that translates legacy and dynamic UI copy at render time. */
@Composable
fun LocalizedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    MaterialText(
        text = translateText(text, LocalAppLanguage.current),
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}

/** Annotated copy keeps its spans in Bangla and becomes a localized plain string in English. */
@Composable
fun LocalizedText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val localized = if (LocalAppLanguage.current == AppLanguage.BANGLA) {
        text
    } else {
        AnnotatedString(translateText(text.text, AppLanguage.ENGLISH))
    }
    MaterialText(
        text = localized,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}

fun translateText(source: String, language: AppLanguage): String {
    if (language == AppLanguage.BANGLA || source.isBlank()) return source
    EnglishCopy[source]?.let { return it }
    var translated = source
    EnglishReplacements.forEach { (bangla, english) -> translated = translated.replace(bangla, english) }
    BanglaDigits.forEach { (bangla, latin) -> translated = translated.replace(bangla, latin) }
    return translated
}

private val BanglaDigits: Map<Char, Char> = mapOf(
    '০' to '0', '১' to '1', '২' to '2', '৩' to '3', '৪' to '4',
    '৫' to '5', '৬' to '6', '৭' to '7', '৮' to '8', '৯' to '9'
)

private val EnglishCopy: Map<String, String> = mapOf(
    "যাত্রার আগে ন্যায্য ভাড়া জানুন," to "Know the fair fare before you travel,",
    "অন্যকে বাঁচাতে তথ্য দিন" to "Share fare info to help others",
    "স্বাগতম!" to "Welcome!",
    "মোবাইল নম্বর দিন" to "Enter mobile number",
    "পাসওয়ার্ড দিন" to "Enter password",
    "পাসওয়ার্ড ভুলে গেছেন?" to "Forgot password?",
    "লগইন করুন" to "Log in",
    "অথবা" to "or",
    "অ্যাকাউন্ট নেই? সাইন আপ করুন" to "Don't have an account? Sign up",
    "অ্যাকাউন্ট নেই? " to "Don't have an account? ",
    "সাইন আপ করুন" to "Sign up",
    "একটু তথ্য দিন, শুরু করা যাক" to "Tell us a little about yourself",
    "নাম আর মোবাইল নম্বর দিয়ে শুরু" to "Start with your name and mobile number",
    "আপনার নাম" to "Your name",
    "মোবাইল নম্বর" to "Mobile number",
    "পরের ধাপ" to "Continue",
    "এবার একটা পাসওয়ার্ড দিন" to "Now create a password",
    "কমপক্ষে ৬ অক্ষরের পাসওয়ার্ড রাখুন" to "Use at least 6 characters",
    "পাসওয়ার্ড আবার দিন" to "Enter password again",
    "ইতিমধ্যে অ্যাকাউন্ট আছে? " to "Already have an account? ",
    "নম্বরটি শুধু অ্যাকাউন্ট যাচাইয়ে ব্যবহার হবে, কারো সাথে শেয়ার করা হবে না" to "Your number is only used to verify your account and is never shared",
    "আমি শর্তাবলী ও গোপনীয়তা নীতিমালা পড়েছি এবং সম্মত" to "I have read and agree to the Terms and Privacy Policy",
    "পাসওয়ার্ড রিকভারি" to "Password recovery",
    "আপনার রেজিস্টার করা মোবাইল নম্বরটি দিন" to "Enter your registered mobile number",
    "আপনার মোবাইলে একটি OTP পাঠানো হয়েছে" to "An OTP has been sent to your mobile",
    "ভেরিফাই করুন" to "Verify",
    "ভেরিফিকেশন করুন" to "Verification",
    "OTP পাননি? " to "Didn't receive the OTP? ",
    "আবার পাঠান" to "Resend",
    "আপনার যাত্রা\nসহজ হোক প্রতিদিন" to "Make every journey\nsimple, every day",
    "দ্রুত খুঁজুন, জেনে নিন, নিরাপদে পৌঁছান" to "Search quickly, know the fare, arrive safely",
    "আমি কোথা থেকে যাচ্ছি?" to "Where are you travelling from?",
    "আমি কোথায় যেতে চাই?" to "Where do you want to go?",
    "যান খুঁজুন" to "Find transport",
    "আপনার সাম্প্রতিক সার্চ" to "Your recent searches",
    "সব দেখুন" to "See all",
    "জনপ্রিয় রুট" to "Popular routes",
    "আপনার ভাড়া দিন, সবার উপকার করুন" to "Share your fare and help everyone",
    "আপনার দেওয়া সঠিক তথ্য অন্যদের\nসঠিক সিদ্ধান্ত নিতে সাহায্য করবে।" to "Accurate information from you helps others\nmake better decisions.",
    "ভাড়া দিন" to "Add fare",
    "ভাড়া দিন" to "Add fare",
    "হোম" to "Home",
    "চার্ট রেটিং" to "Ratings",
    "রিপোর্ট" to "Reports",
    "প্রোফাইল" to "Profile",
    "ফলাফল" to "Results",
    "রিকশা" to "Rickshaw",
    "অটো / CNG" to "Auto / CNG",
    "বাস" to "Bus",
    "লেগুনা / টেম্পো" to "Leguna / Tempo",
    "মাইক্রো / টেম্পো" to "Micro / Tempo",
    "অন্যান্য" to "Other",
    "সবচেয়ে জনপ্রিয়" to "Most popular",
    "ন্যায্য ভাড়া" to "Fair fare",
    "সাধারণ রেঞ্জ" to "Typical range",
    "মিটার ভাড়া (আনুমানিক)" to "Meter fare (estimated)",
    "আনুমানিক ভাড়া" to "Estimated fare",
    "সম্ভাব্য রুট" to "Possible routes",
    "উচ্চ" to "High",
    "মাঝারি" to "Medium",
    "মোট রিপোর্ট" to "Total reports",
    "আপডেট হয়েছে" to "Updated",
    "বিশ্বাসযোগ্যতা" to "Reliability",
    "বিস্তারিত দেখুন " to "View details ",
    "এই ভাড়াগুলো বাস্তব যাত্রীদের রিপোর্টের উপর ভিত্তি করে। অতিরিক্ত ভাড়া চাইলে দরদাম করুন।" to "These fares are based on reports from real passengers. Negotiate if you are asked to pay more.",
    "কিভাবে কাজ করে?" to "How does it work?",
    "ভাড়া জমা দিন" to "Submit fare",
    "যাত্রার শুরু" to "Starting point",
    "পরিবর্তন" to "Change",
    "গন্তব্য স্থান" to "Destination",
    "যানবাহন নির্বাচন করুন" to "Select a vehicle",
    "আপনি কত ভাড়া দিয়েছেন?" to "How much fare did you pay?",
    "পরিমাণ লিখুন" to "Enter amount",
    "কখন যাত্রা করেছেন?" to "When did you travel?",
    "অতিরিক্ত নোট (ঐচ্ছিক)" to "Additional notes (optional)",
    "যেমন: বৃষ্টি ছিল, জ্যাম ছিল ইত্যাদি..." to "For example: rain, traffic, etc...",
    "প্রমাণ ছবি (ঐচ্ছিক)" to "Proof photo (optional)",
    "ভাড়ার রসিদ বা স্ক্রিনশট আপলোড করুন" to "Upload a fare receipt or screenshot",
    "ছবি যোগ করুন" to "Add photo",
    "আপনার তথ্য গোপন রাখা হবে" to "Your information will stay private",
    "আপনার নাম বা ব্যক্তিগত তথ্য অন্যদের কাছে প্রকাশ করা হবে না।" to "Your name or personal details will not be shown to others.",
    "রুট ও যান নির্বাচন" to "Choose route and vehicle",
    "কোথা থেকে কোথায় গেছেন এবং কি যানে চড়েছেন তা নির্বাচন করুন।" to "Select where you travelled and which vehicle you used.",
    "ভাড়ার পরিমাণ" to "Fare amount",
    "আপনি কত টাকা ভাড়া দিয়েছেন তা সঠিকভাবে উল্লেখ করুন।" to "Enter the exact fare you paid.",
    "যাত্রার সময়" to "Journey time",
    "কখন যাত্রা করেছেন তার সঠিক তারিখ ও সময় নির্বাচন করুন।" to "Select the exact date and time of your journey.",
    "তথ্য যাচাই ও গোপনীয়তা" to "Verification and privacy",
    "আপনার দেওয়া তথ্য অন্যান্য যাত্রীদের সাহায্য করবে এবং আপনার পরিচয় গোপন থাকবে।" to "Your information will help other passengers while your identity stays private.",
    "বুঝতে পেরেছি" to "Got it",
    "বুঝেছি" to "Got it",
    "যাত্রার তারিখ" to "Journey date",
    "যেদিন যাত্রা করেছেন সেই তারিখটি বেছে নিন" to "Choose the date you travelled",
    "একটি তারিখ বেছে নিন" to "Choose a date",
    "যে সময়ে যাত্রা করেছিলেন সেটি নির্ধারণ করুন" to "Set the time you travelled",
    "বাতিল" to "Cancel",
    "নিশ্চিত করুন" to "Confirm",
    "লোকেশন নির্বাচন করুন" to "Choose location",
    "এলাকা, রাস্তা বা ল্যান্ডমার্ক লিখুন" to "Enter an area, road or landmark",
    "সার্চের ফলাফল" to "Search results",
    "কমপক্ষে দুইটি অক্ষর লিখুন" to "Enter at least two characters",
    "বর্তমান লোকেশন" to "Current location",
    "আশেপাশের লোকেশন" to "Nearby locations",
    "বর্তমান অবস্থান শনাক্ত করা হচ্ছে…" to "Finding your current location…",
    "আশেপাশের স্থান খোঁজা হচ্ছে…" to "Finding nearby places…",
    "ম্যাপ থেকে নির্বাচন করুন" to "Choose from map",
    "ম্যাপ খুলুন" to "Open map",
    "ঠিকানা খোঁজা হচ্ছে…" to "Finding address…",
    "এই স্থানটি নিশ্চিত করুন" to "Confirm this place",
    "সম্পাদনা" to "Edit",
    "অ্যাকাউন্ট ও সেটিংস" to "Account & settings",
    "প্রোফাইল সম্পাদনা" to "Edit profile",
    "নোটিফিকেশন" to "Notifications",
    "চালু" to "On",
    "বন্ধ" to "Off",
    "ভাষা" to "Language",
    "বাংলা" to "Bangla",
    "ইংরেজি" to "English",
    "সহায়তা ও সাপোর্ট" to "Help & support",
    "গোপনীয়তা ও নিরাপত্তা" to "Privacy & security",
    "নীতি, শর্তাবলি, অ্যাকাউন্ট" to "Policy, terms, account",
    "গোপনীয়তা নীতি" to "Privacy policy",
    "শর্তাবলি" to "Terms",
    "অ্যাকাউন্ট মুছুন" to "Delete account",
    "আমার রিপোর্ট" to "My reports",
    "সেভ করা রুট" to "Saved routes",
    "অ্যাক্টিভিটি" to "Activity",
    "ব্যাজ" to "Badges",
    "যাচাইকৃত অবদানকারী" to "Verified contributor",
    "ট্রাস্ট স্কোর" to "Trust score",
    "গৃহীত" to "Accepted",
    "রেটিং" to "Rating",
    "কমিউনিটি র‍্যাংক" to "Community rank",
    "আমার অবদান" to "My contributions",
    "লগআউট" to "Log out",
    "সংরক্ষণ করুন" to "Save",
    "ভাড়ার আপডেট" to "Fare updates",
    "নতুন ভাড়া ও রিপোর্ট যাচাইয়ের খবর পান" to "Get updates about new fares and report verification",
    "সম্পন্ন" to "Done",
    "ঠিক আছে" to "OK",
    "আমরা কোন তথ্য সংগ্রহ করি" to "Information we collect",
    "তথ্য কীভাবে ব্যবহার করি" to "How we use information",
    "তথ্য শেয়ার ও প্রকাশ" to "Information sharing and disclosure",
    "লোকেশন ও আপনার নিয়ন্ত্রণ" to "Location and your control",
    "তথ্যের নিরাপত্তা ও সংরক্ষণ" to "Information security and retention",
    "আপনার অধিকার" to "Your rights",
    "আপনার তথ্য, আপনার নিয়ন্ত্রণ" to "Your information, your control",
    "VaraBondhu কীভাবে আপনার তথ্য ব্যবহার ও সুরক্ষিত রাখে তা সহজ ভাষায় জানুন।" to "Learn in simple language how VaraBondhu uses and protects your information.",
    "সর্বশেষ হালনাগাদ: ৯ আগস্ট ২০২৬" to "Last updated: 9 August 2026",
    "কোনো স্থান পাওয়া যায়নি" to "No places found",
    "স্থান খোঁজা যাচ্ছে না। আবার চেষ্টা করুন।" to "Unable to search for places. Please try again.",
    "স্থানটি নির্বাচন করা যায়নি।" to "This place could not be selected.",
    "আশেপাশের স্থান দেখতে লোকেশন চালু করুন।" to "Turn on location to see nearby places.",
    "আপনার বর্তমান অবস্থান" to "Your current location",
    "আশেপাশে কোনো পরিচিত স্থান পাওয়া যায়নি।" to "No nearby places were found.",
    "আশেপাশের স্থান লোড করা যাচ্ছে না।" to "Unable to load nearby places.",
    "বর্তমান অবস্থান পাওয়া যায়নি। লোকেশন চালু করে আবার চেষ্টা করুন।" to "Current location was not found. Turn on location and try again.",
    "ম্যাপে নির্বাচিত স্থান" to "Place selected on map",
    "ঠিকানা পাওয়া যায়নি—পিনের অবস্থান ব্যবহার হবে।" to "Address not found—the pin location will be used.",
    "স্থান খোঁজার সেবা এখন পাওয়া যাচ্ছে না।" to "Place search is currently unavailable.",
    "সঠিক মোবাইল নম্বর দিন" to "Enter a valid mobile number",
    "১১ ডিজিটের সঠিক নম্বর দিন" to "Enter a valid 11-digit number",
    "কমপক্ষে ৬ অক্ষরের পাসওয়ার্ড দিন" to "Enter a password with at least 6 characters",
    "৬ ডিজিটের পুরো OTP কোডটি লিখুন" to "Enter the complete 6-digit OTP",
    "পাসওয়ার্ড দুটি মিলছে না" to "Passwords do not match",
    "শর্তাবলীতে সম্মতি দিন" to "Please agree to the terms",
    "আপনার নাম দিন" to "Enter your name",
    "ঢাকা, বাংলাদেশ" to "Dhaka, Bangladesh",
    "শীর্ষ ১২%" to "Top 12%",
    "আরিফ হাসান" to "Arif Hasan",
    "আ" to "A",
    "মিরপুর ১০" to "Mirpur 10",
    "মিরপুর ১০, ঢাকা" to "Mirpur 10, Dhaka",
    "ফার্মগেট" to "Farmgate",
    "ফার্মগেট, ঢাকা" to "Farmgate, Dhaka",
    "মোহাম্মদপুর" to "Mohammadpur",
    "ধানমন্ডি ৩২" to "Dhanmondi 32",
    "উত্তরা সেক্টর ৭" to "Uttara Sector 7",
    "উত্তরা" to "Uttara",
    "এয়ারপোর্ট" to "Airport",
    "মতিঝিল" to "Motijheel",
    "গতকাল" to "Yesterday",
    "আজ, 5:30 PM" to "Today, 5:30 PM",
    "পছন্দের ভাষা বেছে নিন" to "Choose your preferred language",
    "আপনি যেকোনো সময় প্রোফাইল থেকে ভাষা পরিবর্তন করতে পারবেন" to "You can change the language anytime from your profile",
    "বাংলায় চালিয়ে যান" to "Continue in Bangla",
    "Continue in English" to "Continue in English",
    "বাংলায় ব্যবহার করুন" to "Use the app in Bangla",
    "আপনার যাত্রা হোক সহজ" to "Make every journey simple",
    "অ্যাকাউন্ট চালাতে আপনার নাম, মোবাইল নম্বর এবং প্রোফাইলের তথ্য।" to "Your name, mobile number and profile information to operate your account.",
    "আপনার জমা দেওয়া ভাড়ার রিপোর্ট, রুট এবং পরিবহনের তথ্য।" to "The fare reports, routes and transport information you submit.",
    "আপনার অনুমতি থাকলে কাছাকাছি স্থান ও রুট দেখাতে ডিভাইসের লোকেশন।" to "Device location, with your permission, to show nearby places and routes.",
    "সঠিক রুট ও ভাড়ার তথ্য দেখাতে এবং কমিউনিটির রিপোর্ট যাচাই করতে।" to "To show accurate route and fare information and verify community reports.",
    "অ্যাপের নিরাপত্তা, নির্ভরযোগ্যতা এবং ব্যবহার-অভিজ্ঞতা উন্নত করতে।" to "To improve app security, reliability and user experience.",
    "আপনার পছন্দ অনুযায়ী গুরুত্বপূর্ণ আপডেট ও নোটিফিকেশন দিতে।" to "To provide important updates and notifications based on your preferences.",
    "আপনার ব্যক্তিগত শনাক্তকারী তথ্য অন্য ব্যবহারকারীর কাছে প্রকাশ করা হয় না।" to "Your personally identifying information is not disclosed to other users.",
    "ভাড়ার রিপোর্ট পরিচয়বিহীন ও সমন্বিতভাবে কমিউনিটির জন্য দেখানো হতে পারে।" to "Fare reports may be shown anonymously and in aggregate to the community.",
    "আইনি বাধ্যবাধকতা ছাড়া ব্যক্তিগত তথ্য বিক্রি করা হয় না।" to "Personal information is not sold unless legally required.",
    "লোকেশন কেবল আপনার অনুমতির ভিত্তিতে অ্যাপের প্রাসঙ্গিক সুবিধার জন্য ব্যবহৃত হয়।" to "Location is used only with your permission for relevant app features.",
    "ডিভাইসের সেটিংস থেকে যেকোনো সময় লোকেশন অনুমতি পরিবর্তন বা বন্ধ করতে পারবেন।" to "You can change or disable location permission anytime in device settings.",
    "তথ্য সুরক্ষায় যুক্তিসঙ্গত প্রযুক্তিগত ও পরিচালনাগত ব্যবস্থা নেওয়া হয়।" to "Reasonable technical and operational measures are used to protect information.",
    "সেবা প্রদান বা আইনগত প্রয়োজনের চেয়ে বেশি সময় তথ্য রাখা হয় না।" to "Information is not retained longer than required to provide the service or meet legal needs.",
    "ইন্টারনেটভিত্তিক কোনো ব্যবস্থাই শতভাগ ঝুঁকিমুক্ত নয়।" to "No internet-based system is completely risk-free.",
    "আপনার প্রোফাইলের তথ্য দেখা ও প্রয়োজন অনুযায়ী সংশোধন করতে পারবেন।" to "You can view and update your profile information as needed.",
    "অ্যাকাউন্ট ও সংশ্লিষ্ট ব্যক্তিগত তথ্য মুছে দেওয়ার অনুরোধ করতে পারবেন।" to "You can request deletion of your account and related personal information.",
    "নীতি পরিবর্তন হলে এই পাতায় হালনাগাদের তারিখ জানানো হবে।" to "If this policy changes, the updated date will be shown on this page.",
    "এই নীতি সম্পর্কে প্রশ্ন থাকলে প্রোফাইলের ‘সহায়তা ও সাপোর্ট’ অপশন ব্যবহার করুন।" to "For questions about this policy, use Help & Support in your profile.",
    "লোকেশন, ভাড়া খোঁজা বা ভাড়া জমা দিতে সমস্যা হলে আবার চেষ্টা করুন। সরাসরি সাপোর্ট সুবিধা শিগগিরই যুক্ত হবে।" to "If you have trouble with location, finding fares or submitting a fare, please try again. Direct support is coming soon.",
    "VaraBondhu ব্যবহার করে আপনি সঠিক ও বাস্তব ভাড়ার তথ্য দিতে সম্মত হচ্ছেন। ভুল তথ্য বারবার দিলে অ্যাকাউন্টে সীমাবদ্ধতা আসতে পারে।" to "By using VaraBondhu, you agree to provide accurate, real fare information. Repeated false reports may lead to account restrictions.",
    "অ্যাকাউন্ট মুছবেন?" to "Delete account?",
    "অ্যাকাউন্ট মুছে ফেললে আপনার প্রোফাইল ও দেওয়া তথ্য আর ফিরে পাওয়া যাবে না। এই সুবিধাটি শিগগিরই চালু হবে।" to "Deleting your account will permanently remove your profile and submitted information. This feature is coming soon.",
    "লগআউট করবেন?" to "Log out?",
    "আবার ব্যবহার করতে আপনার মোবাইল নম্বর ও পাসওয়ার্ড দিয়ে লগইন করতে হবে।" to "You will need to log in again with your mobile number and password.",
    "পাসওয়ার্ড লুকান" to "Hide password",
    "পাসওয়ার্ড দেখান" to "Show password"
)

private val EnglishReplacements: List<Pair<String, String>> = EnglishCopy.entries
    .sortedByDescending { it.key.length }
    .map { it.key to it.value } + listOf(
        " ঘণ্টা আগে" to " hours ago",
        " ঘণ্টা" to " hours",
        " মিনিট আগে" to " minutes ago",
        " দিন আগে" to " days ago",
        " সেকেন্ড পর " to " seconds later ",
        " রিপোর্ট" to " reports",
        "টি" to "",
        " জন" to " person",
        "রুট " to "Route ",
        "জানুয়ারি" to "January", "ফেব্রুয়ারি" to "February", "মার্চ" to "March",
        "এপ্রিল" to "April", "মে" to "May", "জুন" to "June", "জুলাই" to "July",
        "আগস্ট" to "August", "সেপ্টেম্বর" to "September", "অক্টোবর" to "October",
        "নভেম্বর" to "November", "ডিসেম্বর" to "December",
        "পূর্বাহ্ণ" to "AM", "অপরাহ্ণ" to "PM"
    )
