package com.echat_backend.data.data_sources

import com.echat_backend.data.models.User
import com.echat_backend.security.hashing.HashingService
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserDataSource(
    db: CoroutineDatabase,
    private val hashingService: HashingService,
) : UserDataSource {

    private val users = db.getCollection<User>()

    override suspend fun insertUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun getUserByUsername(username: String): User? {
        return try {
            users.findOne(User::username eq username)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun changeUsername(usernameToFindUser: String, newUsername: String): Boolean {
        return try {
            // find user by old username
            val userToUpdate = getUserByUsername(usernameToFindUser)

            if (userToUpdate != null) {
                // update username in found user
                val updatedUser = userToUpdate.copy(username = newUsername)

                // update user in database
                val updateResult = users.replaceOne(User::username eq usernameToFindUser, updatedUser)

                // check if update finished okay
                updateResult.wasAcknowledged()
            } else {
                // user was not found by old nickname
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun changePassword(usernameToFindUser: String, newPassword: String): Boolean {
        return try {
            val userToUpdate = getUserByUsername(usernameToFindUser)
            if (userToUpdate != null) {
                val saltedHashPw = hashingService.generateSaltedHash(newPassword, 32)
                val updatedUser = userToUpdate.copy(password = saltedHashPw.hash, salt = saltedHashPw.salt)
                val updateResult = users.replaceOne(User::username eq usernameToFindUser, updatedUser)
                updateResult.wasAcknowledged()
            } else {
                false

            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun changePhoneNumber(usernameToFindUser: String, newPhoneNumber: String): Boolean {
        return try {
            val userToUpdate = getUserByUsername(usernameToFindUser)
            if (userToUpdate != null) {
                val updatedUser = userToUpdate.copy(phoneNumber = newPhoneNumber)
                val updateResult = users.replaceOne(User::username eq usernameToFindUser, updatedUser)
                updateResult.wasAcknowledged()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}