package com.petcaresuite.management.application.security

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

@Service
class LoginAttemptService (
    private val request: HttpServletRequest
) : ILoginAttemptService {
    companion object {
        const val MAX_ATTEMPT = 10
        private val logger = LoggerFactory.getLogger(LoginAttemptService::class.java)
    }

    private val attemptsCache: LoadingCache<String, Int> = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.DAYS)
        .build(object : CacheLoader<String, Int>() {
            override fun load(key: String): Int = 0
        })

    override fun loginFailed(key: String) {
        var attempts = try {
            attemptsCache.get(key)
        } catch (e: ExecutionException) {
            0
        }
        attempts++
        attemptsCache.put(key, attempts)
    }

    override fun isBlocked(): Boolean {
        val attempts = attemptsCache.get(getClientIP())
        val ip = attemptsCache.get(getClientIP())
        return try {
            if (attempts >= MAX_ATTEMPT) {
                logger.warn("$ip has been blocked too many login attempts.")
                true
            } else {
                false
            }
        } catch (e: ExecutionException) {
            false
        }
    }

    private fun getClientIP(): String {
        val xfHeader = request.getHeader("X-Forwarded-For")
        return if (xfHeader != null) {
            xfHeader.split(",")[0]
        } else {
            request.remoteAddr
        }
    }
}