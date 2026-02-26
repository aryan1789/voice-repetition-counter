package com.aryan.voicerepetition.utils

object PhraseMatcher {

    fun normalize(input: String): String {
        return input
            .lowercase()
            .replace(Regex("[^a-z\\s]"), "")
            .trim()
            .replace(Regex("\\s+"), " ")
    }

    fun similarity(a: String, b: String): Double {
        val s1 = normalize(a)
        val s2 = normalize(b)

        val distance = levenshteinDistance(s1, s2)
        val maxLength = maxOf(s1.length, s2.length)

        if (maxLength == 0) return 1.0

        return 1.0 - distance.toDouble() / maxLength
    }

    private fun levenshteinDistance(lhs: String, rhs: String): Int {
        val dp = Array(lhs.length + 1) { IntArray(rhs.length + 1) }

        for (i in 0..lhs.length) dp[i][0] = i
        for (j in 0..rhs.length) dp[0][j] = j

        for (i in 1..lhs.length) {
            for (j in 1..rhs.length) {
                val cost = if (lhs[i - 1] == rhs[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }

        return dp[lhs.length][rhs.length]
    }
}