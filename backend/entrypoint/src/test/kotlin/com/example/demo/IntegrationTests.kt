package com.example.demo

import com.example.demo.model.UrlDao
import com.example.demo.model.UrlMapping
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import org.apache.commons.lang3.RandomStringUtils
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus


@SpringBootTest(
    classes = [Boot::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var flyway: Flyway

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @AfterAll
    fun cleanup() {
        flyway.clean()
    }

    @Test
    @Order(1)
    fun contextLoads() {
    }

    @Test
    @Order(2)
    fun `should successfully create new url mapping if valid request payload given`() {
        mockkStatic(RandomStringUtils::class)
        every { RandomStringUtils.random(8, true, true) } returns "1234abcd"

        val result = restTemplate.postForEntity("/shorten", UrlDao(url = "https://www.yahoo.com"), UrlMapping::class.java )

        assertNotNull(result)
        assertEquals(HttpStatus.CREATED, result?.statusCode)
        assertEquals("https://www.yahoo.com", result.body?.longUrl)
        assertEquals("1234abcd", result.body?.shortUrl)
    }

    @Test
    @Order(3)
    fun `should reuse existing url mapping if already exists`() {
        val result = restTemplate.postForEntity("/shorten", UrlDao(url = "https://www.yahoo.com"), UrlMapping::class.java )

        assertNotNull(result)
        assertEquals(HttpStatus.OK, result?.statusCode)
        assertEquals("https://www.yahoo.com", result.body?.longUrl)
        assertEquals("1234abcd", result.body?.shortUrl)
    }

    @Test
    @Order(4)
    fun `should successfully retrieve existing url mapping if shortUrl found`() {
        val result = restTemplate.getForEntity("/1234abcd", UrlMapping::class.java)

        assertNotNull(result)
        assertEquals(HttpStatus.MOVED_PERMANENTLY, result?.statusCode)
        assertEquals("https://www.yahoo.com", result.headers.location.toString())
    }
}