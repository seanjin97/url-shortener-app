package com.example.demo.utils

fun formatLongUrl(url: String): String {
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        return "https://$url"

    }
    return url
}