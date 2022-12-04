package com.example.demo.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime
import java.time.ZoneOffset


@Entity
@Table(name = "url_mapping")
@EntityListeners(AuditingEntityListener::class)
data class UrlMapping(
    @Id
    @Column(name="short_url", nullable=false)
    val shortUrl: String,

    @Column(name="long_url", nullable=false)
    val longUrl: String,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdDate: OffsetDateTime? = OffsetDateTime.now(ZoneOffset.UTC),
)
