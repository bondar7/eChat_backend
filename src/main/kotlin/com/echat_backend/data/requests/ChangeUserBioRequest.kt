package com.echat_backend.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class ChangeUserBioRequest(
    val usernameToFindUser: String,
    val newBio: String
)