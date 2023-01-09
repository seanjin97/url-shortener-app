package com.example.demo.services

import com.example.demo.dao.UrlMappingDao
import com.example.demo.model.UrlMapping
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.net.URL

@Service
class RedexService(@Qualifier("sqlDataSource") val urlMappingDao: UrlMappingDao) {

    val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)

    fun getRedirectUrl(shortUrl: String): UrlMapping?{
        return urlMappingDao.getRedirectUrl(shortUrl)
    }

    fun saveUrlMapping(redirectUrl: String): UrlMapping {
        val formattedLongUrl = com.example.demo.utils.formatLongUrl(redirectUrl)

        val validatedUrl = URL(formattedLongUrl).toURI()

        val newShortUrl = RandomStringUtils.random(8, true, true)

        return saveUrlMappingWithRetry(validatedUrl.toString(), newShortUrl)

    }

    fun saveUrlMappingWithRetry(longUrl: String, shortUrl: String, retry: Int = 3): UrlMapping {
        logger.info("Attempting to generate url mapping: try " + (4 - retry))
        if (retry <= 0) {
            throw Exception("Maximum number of key collision retries reached!")
        }

        val existingMapping = urlMappingDao.getRedirectUrl(shortUrl)

        if (existingMapping != null) {
            return saveUrlMappingWithRetry(longUrl, RandomStringUtils.random(8, true, true), retry - 1)
        }

        logger.info("Suitable shortUrl generated.")
        return urlMappingDao.saveUrlMapping(UrlMapping(shortUrl, longUrl))

    }

    fun getShortUrlByLongUrl(longUrl: String): UrlMapping? {
        val formattedLongUrl = com.example.demo.utils.formatLongUrl(longUrl)
        return urlMappingDao.getShortUrlByLongUrl(formattedLongUrl)
    }


}