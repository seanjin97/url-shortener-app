package com.example.demo.repositories

import com.example.demo.model.UrlMapping
import org.springframework.data.repository.CrudRepository

interface UrlMappingRepository : CrudRepository<UrlMapping, String> {
    fun findTopByLongUrl(longUrl: String): UrlMapping?
}