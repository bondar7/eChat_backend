package com.echat_backend.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
val usernameToFindUser: String,
val newPassword: String
)
