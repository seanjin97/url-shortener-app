package com.example.demo.services

import com.example.demo.dao.UrlMappingDao
import com.example.demo.model.UrlMapping
import io.mockk.*
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.time.OffsetDateTime
import java.time.ZoneOffset

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
private class RedirectServiceTest {

    val mockDao: UrlMappingDao = mockk()

    val redirectService = RedirectService(mockDao)

    val testData = listOf(
        UrlMapping(shortUrl="123abc", longUrl="https://www.google.com"),
        UrlMapping(shortUrl = "987poi", longUrl = "https://www.yahoo.com")
    )

    val mockDate: OffsetDateTime = OffsetDateTime.of(
        2023,
        1,
        8,
        10,
        10,
        10,
        10,
        ZoneOffset.UTC
    )

    @BeforeEach
    fun beforeEach() {
        mockkStatic(OffsetDateTime::class)
        every { OffsetDateTime.now(ZoneOffset.UTC) } returns mockDate
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should return url mapping object if shortUrl found`() {
        every { mockDao.getRedirectUrl("123abc") } returns testData[0]

        val retrievedMapping = redirectService.getRedirectUrl("123abc")

        verify(exactly = 1) { mockDao.getRedirectUrl("123abc") }
        assertNotNull(retrievedMapping)
        assertEquals("https://www.google.com", retrievedMapping?.longUrl)
    }

    @Test
    fun `should create new url mapping object without retries when short url does not collide`() {
        mockkStatic(RandomStringUtils::class)
        every { RandomStringUtils.random(8, true, true) } returns "1234abcd"
        every { mockDao.getRedirectUrl("1234abcd") } returns null
        every { mockDao.saveUrlMapping(UrlMapping(shortUrl = "1234abcd", longUrl = "https://www.google.com", createdDate = mockDate)) } returns UrlMapping(shortUrl = "1234abcd", longUrl = "https://www.google.com", createdDate = mockDate)

        val newUrlMapping = redirectService.saveUrlMapping("https://www.google.com")

        verify(exactly = 1) { RandomStringUtils.random(8, true, true) }
        verify(exactly = 1) { mockDao.getRedirectUrl("1234abcd") }
        verify(exactly = 1) { mockDao.saveUrlMapping(UrlMapping(shortUrl = "1234abcd", longUrl = "https://www.google.com", createdDate = mockDate)) }
        assertNotNull(newUrlMapping)
        assertEquals("1234abcd", newUrlMapping.shortUrl)
        assertEquals("https://www.google.com", newUrlMapping.longUrl)
    }

    @Test
    fun `should create new url mapping object with retries when short url collides`() {
        mockkStatic(RandomStringUtils::class)
        every { RandomStringUtils.random(8, true, true) } returnsMany listOf("1234abcd", "4321dcba")
        every { mockDao.getRedirectUrl("1234abcd") } returns UrlMapping(shortUrl = "1234abcd", longUrl = "https://www.google.com", createdDate = mockDate)
        every { mockDao.getRedirectUrl("4321dcba") } returns null
        every { mockDao.saveUrlMapping(UrlMapping(shortUrl = "4321dcba", longUrl = "https://www.google.com", createdDate = mockDate)) } returns UrlMapping(shortUrl = "4321dcba", longUrl = "https://www.google.com", createdDate = mockDate)

        val newUrlMapping = redirectService.saveUrlMapping("https://www.google.com")

        verify(exactly = 2) { RandomStringUtils.random(8, true, true) }
        verify(exactly = 1) { mockDao.getRedirectUrl("1234abcd") }
        verify(exactly = 1) { mockDao.getRedirectUrl("4321dcba") }
        verify(exactly = 1) { mockDao.saveUrlMapping(UrlMapping(shortUrl = "4321dcba", longUrl = "https://www.google.com", createdDate = mockDate)) }
        assertNotNull(newUrlMapping)
        assertEquals("4321dcba", newUrlMapping.shortUrl)
        assertEquals("https://www.google.com", newUrlMapping.longUrl)
    }

    @Test
    fun `should throw exception when maximum key collision retries reached`() {
        mockkStatic(RandomStringUtils::class)
        every { RandomStringUtils.random(8, true, true) } returnsMany listOf("1234abcd", "4321dcba", "1234qwer", "4321qwer")
        every { mockDao.getRedirectUrl("1234abcd") } returns UrlMapping(shortUrl = "1234abcd", longUrl = "https://www.google.com", createdDate = mockDate)
        every { mockDao.getRedirectUrl("4321dcba") } returns UrlMapping(shortUrl = "4321dcba", longUrl = "https://www.google.com", createdDate = mockDate)
        every { mockDao.getRedirectUrl("1234qwer") } returns UrlMapping(shortUrl = "1234qwer", longUrl = "https://www.google.com", createdDate = mockDate)

        assertThrows<Exception> { redirectService.saveUrlMapping("https://www.google.com") }

        verify(exactly = 4) { RandomStringUtils.random(8, true, true) }
        verify(exactly = 1) { mockDao.getRedirectUrl("1234abcd") }
        verify(exactly = 1) { mockDao.getRedirectUrl("4321dcba") }
        verify(exactly = 1) { mockDao.getRedirectUrl("1234qwer") }
    }

    @Test
    fun `should throw exception when malformed url received`() {
        assertThrows<URISyntaxException> {
            redirectService.saveUrlMapping("http://testing.example.com/<<")
        }
    }

    @Test
    fun `should return url mapping if long url found`() {
        every { mockDao.getShortUrlByLongUrl("https://www.google.com") } returns testData[0]

        val foundMapping = redirectService.getShortUrlByLongUrl("https://www.google.com")

        verify(exactly = 1) { mockDao.getShortUrlByLongUrl("https://www.google.com") }
        assertNotNull(foundMapping)
        assertEquals(testData[0].shortUrl, foundMapping?.shortUrl)

    }

}