package com.rork.varabondhu.location

import java.util.Locale
import kotlin.math.min

/** Locally curated Bangladesh institutions that are missing from public map indexes. */
internal object CuratedBangladeshPlaceSearch {
    private val records: List<CuratedPlaceRecord> = listOf(
        CuratedPlaceRecord(
            place = LocationPlace(
                name = "Ibrahimpur Salahuddin Shikhyalaya",
                address = "1381 Shewrapara, Kafrul, Dhaka-1216 · EIIN 108175",
                latitude = 23.7920748,
                longitude = 90.3791061
            ),
            aliases = listOf(
                "Ibrahimpur Salahuddin Shikhyalaya",
                "Ibrahimpur Salahuddin Shikshalaya",
                "Ibrahimpur Salahuddin Shikkhalaya",
                "Ibrahimpur Salahuddin Shikhalaya",
                "Ibrahimpur Salahuddin Shikkhaloya",
                "Ibrahimpur Salahuddin School",
                "Salahuddin Shikhyalaya",
                "Salahuddin Shikshalaya",
                "ইব্রাহিমপুর সালাহউদ্দিন শিক্ষালয়",
                "ইব্রাহীমপুর সালাহউদ্দিন শিক্ষালয়",
                "সালাহউদ্দিন শিক্ষালয়",
                "EIIN 108175",
                "108175"
            )
        )
    )

    fun search(query: String): List<LocationPlace> {
        val normalizedQuery: String = query.normalizedSearchText()
        if (normalizedQuery.length < MINIMUM_QUERY_LENGTH || normalizedQuery.isGenericOnlyQuery()) {
            return emptyList()
        }

        return records.mapNotNull { record: CuratedPlaceRecord ->
            val score: Int = record.aliases.maxOfOrNull { alias: String ->
                matchScore(normalizedQuery, alias.normalizedSearchText())
            } ?: NO_MATCH
            score.takeIf { value: Int -> value > NO_MATCH }?.let { value: Int -> record to value }
        }.sortedByDescending { (_, score: Int) -> score }
            .map { scoredRecord: Pair<CuratedPlaceRecord, Int> -> scoredRecord.first.place }
    }

    private fun matchScore(query: String, candidate: String): Int {
        if (query == candidate) return EXACT_MATCH_SCORE
        if (candidate.startsWith(query)) return PREFIX_MATCH_SCORE + query.length
        if (candidate.contains(query)) return CONTAINS_MATCH_SCORE + query.length

        val queryTokens: List<String> = query.split(' ').filter(String::isNotBlank)
        val candidateTokens: List<String> = candidate.split(' ').filter(String::isNotBlank)
        if (queryTokens.isEmpty() || candidateTokens.isEmpty()) return NO_MATCH

        var totalDistance = 0
        for (queryToken: String in queryTokens) {
            val bestDistance: Int = candidateTokens.minOf { candidateToken: String ->
                tokenDistance(queryToken, candidateToken)
            }
            if (bestDistance > allowedDistance(queryToken.length)) return NO_MATCH
            totalDistance += bestDistance
        }
        return FUZZY_MATCH_SCORE - totalDistance
    }

    private fun tokenDistance(queryToken: String, candidateToken: String): Int {
        if (queryToken == candidateToken ||
            (queryToken.length >= MINIMUM_PREFIX_LENGTH && candidateToken.startsWith(queryToken))
        ) {
            return 0
        }
        return levenshteinDistance(queryToken, candidateToken)
    }

    private fun levenshteinDistance(first: String, second: String): Int {
        if (first == second) return 0
        if (first.isEmpty()) return second.length
        if (second.isEmpty()) return first.length

        var previous: IntArray = IntArray(second.length + 1) { index: Int -> index }
        var current: IntArray = IntArray(second.length + 1)
        first.forEachIndexed { firstIndex: Int, firstCharacter: Char ->
            current[0] = firstIndex + 1
            second.forEachIndexed { secondIndex: Int, secondCharacter: Char ->
                val substitutionCost: Int = if (firstCharacter == secondCharacter) 0 else 1
                current[secondIndex + 1] = min(
                    min(current[secondIndex] + 1, previous[secondIndex + 1] + 1),
                    previous[secondIndex] + substitutionCost
                )
            }
            val swap: IntArray = previous
            previous = current
            current = swap
        }
        return previous[second.length]
    }

    private fun allowedDistance(tokenLength: Int): Int = when {
        tokenLength >= 9 -> 2
        tokenLength >= 5 -> 1
        else -> 0
    }

    private fun String.normalizedSearchText(): String = buildString {
        var hasTrailingSpace = false
        this@normalizedSearchText.lowercase(Locale.ROOT).forEach { character: Char ->
            if (character.isLetterOrDigit()) {
                append(character)
                hasTrailingSpace = false
            } else if (!hasTrailingSpace && isNotEmpty()) {
                append(' ')
                hasTrailingSpace = true
            }
        }
    }.trim()

    private fun String.isGenericOnlyQuery(): Boolean {
        val tokens: List<String> = split(' ').filter(String::isNotBlank)
        return tokens.isNotEmpty() && tokens.all(genericInstitutionTerms::contains)
    }

    private data class CuratedPlaceRecord(
        val place: LocationPlace,
        val aliases: List<String>
    )

    private val genericInstitutionTerms: Set<String> = setOf(
        "school",
        "academy",
        "college",
        "shikhyalaya",
        "shikshalaya",
        "shikkhalaya",
        "বিদ্যালয়",
        "শিক্ষালয়",
        "স্কুল",
        "কলেজ"
    )

    private const val MINIMUM_QUERY_LENGTH = 2
    private const val MINIMUM_PREFIX_LENGTH = 3
    private const val NO_MATCH = -1
    private const val EXACT_MATCH_SCORE = 1_000
    private const val PREFIX_MATCH_SCORE = 900
    private const val CONTAINS_MATCH_SCORE = 800
    private const val FUZZY_MATCH_SCORE = 700
}
