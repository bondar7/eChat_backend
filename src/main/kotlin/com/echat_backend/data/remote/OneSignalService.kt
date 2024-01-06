package com.echat_backend.data.remote

import com.echat_backend.data.remote.dto.Notification

interface OneSignalService {
    suspend fun sendNotification(notification: Notification): Boolean

    companion object {
        const val NOTIFICATIONS = "https://onesignal.com/api/v1/notifications"
    }
}