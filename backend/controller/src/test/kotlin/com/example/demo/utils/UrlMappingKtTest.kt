package com.example.demo.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.exp

private class UrlMappingKtTest {
    val testCases = mutableListOf(
        "www.google.com",
        "https://www.google.com",
        "http://www.google.com"
    )

    val expectedResults = mutableListOf(
        "https://www.google.com",
        "https://www.google.com",
        "http://www.google.com",
    )

    @Test
    fun `should correctly formatted url`() {
        for (index in testCases.indices) {
            val actual = formatLongUrl(testCases[index])
            assertEquals(expectedResults[index], actual)
        }
    }

}