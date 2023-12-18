package com.echat_backend.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class ChangeUsernameRequest(
    val usernameToFindUser: String,
    val newUsername: String
)