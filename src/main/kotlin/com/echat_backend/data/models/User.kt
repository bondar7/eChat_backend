package com.echat_backend.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.sql.Blob

data class User(
    val username: String,
    val name: String? = username,
    val email: String,
    val password: String,
    val userBio: String = "Bio",
    val avatar: ByteArray? = null,
    val salt: String,
    @BsonId val id: ObjectId = ObjectId()
)