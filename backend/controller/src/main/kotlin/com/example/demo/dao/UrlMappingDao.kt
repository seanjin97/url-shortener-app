package com.example.demo.dao

import com.example.demo.model.UrlMapping


interface UrlMappingDao {
    fun getRedirectUrl(shortUrl: String): UrlMapping?

    fun saveUrlMapping(urlMapping: UrlMapping): UrlMapping

    fun getShortUrlByLongUrl(longUrl: String): UrlMapping?
}