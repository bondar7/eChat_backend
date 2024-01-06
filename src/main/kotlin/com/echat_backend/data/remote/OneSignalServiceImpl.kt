package com.echat_backend.data.remote

import com.echat_backend.data.remote.dto.Notification
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class OneSignalServiceImpl(
    private val client: OkHttpClient,
    private val apiKey: String
) : OneSignalService {
    @OptIn(InternalAPI::class)
    override suspend fun sendNotification(notification: Notification): Boolean {
        return try {
            val json = Json.encodeToString(notification)
            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(OneSignalService.NOTIFICATIONS)
                .post(requestBody)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Basic $apiKey")
                .addHeader("content-type", "application/json")
                .build()
            val response = client.newCall(request).execute()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}