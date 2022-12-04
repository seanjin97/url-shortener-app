package com.example.demo.controllers

import com.example.demo.model.UrlDao
import com.example.demo.services.RedexService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.MalformedURLException
import java.net.URI

@RestController
@RequestMapping("/")
class RedirectController(val redexService: RedexService) {

    val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)

    @GetMapping("/{shortUrl}")
    fun getRedirectMapping(@PathVariable shortUrl: String): ResponseEntity<String> {
        logger.info("Redirect called - $shortUrl.")
        val retrievedMapping = redexService.getRedirectUrl(shortUrl) ?: return ResponseEntity.notFound().build()
        logger.info("Redirect mapping resolved for $shortUrl: ${retrievedMapping.longUrl}")
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(URI(retrievedMapping.longUrl)).build()
    }

    @PostMapping("/shorten")
    fun saveRedirectMapping(@RequestBody newUrl: UrlDao): ResponseEntity<Any> {
        logger.info("Create new url mapping process started.")
        // Check if existing long url exists
        val existingMapping = redexService.getShortUrlByLongUrl(newUrl.url)


        // Return existing shortUrl if mapping already exists
        if (existingMapping != null) {
            logger.info("Returning existing mapping - $existingMapping")
            return ResponseEntity.ok(existingMapping)
        }

        return try {
            val newMapping = redexService.saveUrlMapping(newUrl.url)
            logger.info("Successfully created new url mapping - $newMapping.")
            ResponseEntity.ok(newMapping)
        } catch (e: MalformedURLException) {
            logger.error("Invalid URL provided. ${e.message}")
            ResponseEntity.badRequest().body("Invalid URL provided")
        }

    }
}