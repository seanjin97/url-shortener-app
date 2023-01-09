package com.example.demo.dao

import com.example.demo.model.UrlMapping
import com.example.demo.repositories.UrlMappingRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository("sqlDataSource")
class SQLRedexAccessControlService(val urlMappingRepository: UrlMappingRepository) : UrlMappingDao {
    override fun getRedirectUrl(shortUrl: String): UrlMapping? {
        return urlMappingRepository.findByIdOrNull(shortUrl)
    }

    override fun saveUrlMapping(urlMapping: UrlMapping): UrlMapping {
        return urlMappingRepository.save(urlMapping)
    }

    override fun getShortUrlByLongUrl(longUrl: String): UrlMapping? {
        return urlMappingRepository.findTopByLongUrl(longUrl)
    }
}