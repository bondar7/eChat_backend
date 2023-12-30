package com.echat_backend.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class ChangeAvatarRequest(
    val usernameToFindUser: String,
    val avatar: ByteArray
)