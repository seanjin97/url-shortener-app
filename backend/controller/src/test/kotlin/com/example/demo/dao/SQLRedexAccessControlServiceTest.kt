package com.example.demo.dao

import com.example.demo.model.UrlMapping
import com.example.demo.repositories.UrlMappingRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import java.time.OffsetDateTime
import java.time.ZoneOffset

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SQLRedexAccessControlServiceTest {

    val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)

    private val urlMappingRepository: UrlMappingRepository = mockk()

    private val sqlRedexAccessControlService = SQLRedexAccessControlService(urlMappingRepository)


    private val mockDate: OffsetDateTime = OffsetDateTime.of(
        2023,
        1,
        8,
        10,
        10,
        10,
        10,
        ZoneOffset.UTC
    )

    private val testData = mutableListOf(
        UrlMapping(shortUrl="123abc", longUrl="https://www.google.com"),
        UrlMapping(shortUrl = "987poi", longUrl = "https://www.yahoo.com")
    )

    @Test
    fun `should return url mapping object if shortUrl found`() {
        every { urlMappingRepository.findByIdOrNull("123abc") } returns testData[0]

        val urlMapping = sqlRedexAccessControlService.getRedirectUrl("123abc")

        verify(exactly = 1) { urlMappingRepository.findByIdOrNull("123abc") }
        assertNotNull(urlMapping)
        assertEquals("https://www.google.com", urlMapping?.longUrl)
    }

    @Test
    fun `should return null if shortUrl not found`() {
        every { urlMappingRepository.findByIdOrNull("wrong short url") } returns null

        val urlMapping = sqlRedexAccessControlService.getRedirectUrl("wrong short url")

        verify(exactly = 1) { urlMappingRepository.findByIdOrNull("wrong short url") }
        assertNull(urlMapping)
    }


    @Test
    fun `should successfully save url mapping`() {
        mockkStatic(OffsetDateTime::class)
        every { OffsetDateTime.now(ZoneOffset.UTC) } returns mockDate
        every { urlMappingRepository.save(UrlMapping(shortUrl = "987poi", longUrl = "https://www.yahoo.com", createdDate = mockDate)) } returns testData[1]
        every { urlMappingRepository.findByIdOrNull("987poi") } returns testData[1]

        sqlRedexAccessControlService.saveUrlMapping(UrlMapping(shortUrl = "987poi", longUrl = "https://www.yahoo.com"))
        val newlyCreatedMapping = sqlRedexAccessControlService.getRedirectUrl("987poi")

        verify(exactly = 1) { urlMappingRepository.save(UrlMapping(shortUrl = "987poi", longUrl = "https://www.yahoo.com", createdDate = mockDate)) }
        verify(exactly = 1) { urlMappingRepository.findByIdOrNull("987poi")}
        assertNotNull(newlyCreatedMapping)
        assertEquals("https://www.yahoo.com", newlyCreatedMapping?.longUrl)
    }

}