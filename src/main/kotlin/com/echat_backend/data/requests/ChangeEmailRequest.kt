package com.echat_backend.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class ChangeEmailRequest(
    val usernameToFindUser: String,
    val newEmail: String
)
