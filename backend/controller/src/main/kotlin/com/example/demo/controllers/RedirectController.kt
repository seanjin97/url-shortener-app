package com.example.demo.controllers

import com.example.demo.model.UrlDao
import com.example.demo.services.RedexService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.MalformedURLException
import java.net.URI

@RestController
@RequestMapping("/")
class RedirectController(val redexService: RedexService) {

    @GetMapping("/{shortUrl}")
    fun getRedirectMapping(@PathVariable shortUrl: String): ResponseEntity<String> {
        val retrievedMapping = redexService.getRedirectUrl(shortUrl) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(URI(retrievedMapping.longUrl)).build()
    }

    @PostMapping("/shorten")
    fun saveRedirectMapping(@RequestBody newUrl: UrlDao): ResponseEntity<Any> {

        // Check if existing long url exists
        val existingMapping = redexService.getShortUrlByLongUrl(newUrl.url)


        // Return existing shortUrl if mapping already exists
        if (existingMapping != null) {
            return ResponseEntity.ok(existingMapping)
        }

        return try {
            val newMapping = redexService.saveUrlMapping(newUrl.url)
            ResponseEntity.ok(newMapping)
        } catch (e: MalformedURLException) {
            ResponseEntity.badRequest().body("Invalid URL provided")
        }

    }
}