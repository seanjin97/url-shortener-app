package com.example.demo.repositories

import com.example.demo.model.UrlMapping
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UrlMappingRepository : CrudRepository<UrlMapping, String> {
    fun findTopByLongUrl(longUrl: String): UrlMapping?
}