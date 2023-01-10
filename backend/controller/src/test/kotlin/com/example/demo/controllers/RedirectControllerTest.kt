package com.example.demo.controllers

import com.example.demo.model.UrlMapping
import com.example.demo.services.RedirectService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.net.URISyntaxException

@WebMvcTest
@ContextConfiguration(classes = [RedirectController::class])
private class RedirectControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    lateinit var  mockRedirectService: RedirectService

    @BeforeEach
    fun setUp() {

    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should get redirected when url mapping exists`() {
        every { mockRedirectService.getRedirectUrl("1234abcd") } returns UrlMapping(shortUrl="1234abcd", longUrl="https://www.google.com")

        mockMvc.get("/1234abcd")
            .andExpect {
                status { is3xxRedirection() }
                header { string("location", "https://www.google.com") }
            }
        verify(exactly = 1) { mockRedirectService.getRedirectUrl("1234abcd") }
    }

    @Test
    fun `should return 404 error if url mapping does not exist`() {
        every { mockRedirectService.getRedirectUrl("1234abcd") } returns null

        mockMvc.get("/1234abcd")
            .andExpect {
                status { isNotFound() }
                header { doesNotExist("location") }
            }
        verify(exactly = 1) { mockRedirectService.getRedirectUrl("1234abcd") }
    }

    @Test
    fun `should create new url mapping when valid request payload provided and url mapping does not exist`() {
        every { mockRedirectService.getShortUrlByLongUrl("https://www.google.com") } returns null
        every { mockRedirectService.saveUrlMapping("https://www.google.com")} returns UrlMapping(shortUrl="1234abcd", longUrl = "https://www.google.com")

        mockMvc.post("/shorten") {
            content = "{\"url\": \"https://www.google.com\"}"
            contentType = MediaType(MediaType.APPLICATION_JSON)
            }
            .andExpect {
                status { isCreated() }
                content { UrlMapping(shortUrl="1234abcd", longUrl = "https://www.google.com") }
            }
        verify(exactly = 1) { mockRedirectService.getShortUrlByLongUrl("https://www.google.com") }
        verify(exactly = 1) { mockRedirectService.saveUrlMapping("https://www.google.com") }
    }

    @Test
    fun `should reuse existing url mapping when valid request payload provided`() {
        every { mockRedirectService.getShortUrlByLongUrl("https://www.yahoo.com") } returns UrlMapping(shortUrl="1234abcd", longUrl = "https://www.yahoo.com")

        mockMvc.post("/shorten") {
            content = "{\"url\": \"https://www.yahoo.com\"}"
            contentType = MediaType(MediaType.APPLICATION_JSON)
        }
            .andExpect {
                status { isOk() }
                content { UrlMapping(shortUrl="1234abcd", longUrl = "https://www.yahoo.com") }
            }
        verify(exactly = 1) { mockRedirectService.getShortUrlByLongUrl("https://www.yahoo.com") }
    }

    @Test
    fun `should return bad request error when invalid request payload given`() {
        every { mockRedirectService.getShortUrlByLongUrl("http://testing.example.com/<<") } returns null
        every { mockRedirectService.saveUrlMapping("http://testing.example.com/<<")} throws URISyntaxException("error", "error")

        mockMvc.post("/shorten") {
            content = "{\"url\": \"http://testing.example.com/<<\"}"
            contentType = MediaType(MediaType.APPLICATION_JSON)
        }
            .andExpect {
                status { isBadRequest() }
                content { string("Invalid URL provided") }
            }
        verify(exactly = 1) { mockRedirectService.getShortUrlByLongUrl("http://testing.example.com/<<") }
        verify(exactly = 1) { mockRedirectService.saveUrlMapping("http://testing.example.com/<<") }
    }

}