package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Boot

fun main(args: Array<String>) {
    runApplication<Boot>(*args)
}