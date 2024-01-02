package com.echat_backend.data.data_sources.userDT

import com.echat_backend.data.models.User
import com.echat_backend.data.responses.Person
import com.echat_backend.security.hashing.HashingService
import com.echat_backend.security.hashing.SaltedHash
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.regex

class MongoUserDataSource(
    db: CoroutineDatabase,
    private val hashingService: HashingService,
) : UserDataSource {

    private val users = db.getCollection<User>()

    override suspend fun insertUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun changeAvatar(usernameToFindUser: String, avatar: ByteArray) {
        try {
            val userToUpdate = getUserByUsername(usernameToFindUser)
            if (userToUpdate != null) {
                val updatedUser = userToUpdate.copy(avatar = avatar)
                users.replaceOne(User::username eq usernameToFindUser, updatedUser)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("EXCEPTION: $e")
        }
    }

    override suspend fun getUserByUsername(username: String): User? {
        return try {
            users.findOne(User::username eq username)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getUserById(id: String): User? {
        return try {
            users.findOne(User::id eq id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return try {
            users.findOne(User::email eq email)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getUsersByUsername(username: String): List<Person> {
        return try {
            val regexQuery = "^$username".toRegex(RegexOption.IGNORE_CASE)
            val users = users.find(User::username.regex(regexQuery)).toList()
              val persons = users.map {
                    Person(
                        it.id,
                        it.username,
                        it.name ?: it.username,
                        it.avatar,
                        it.userBio
                    )
                }
                persons
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    override suspend fun changeUsername(usernameToFindUser: String, newUsername: String) {
         try {
            // find user by old username
            val userToUpdate = getUserByUsername(usernameToFindUser)

            if (userToUpdate != null) {
                // update username in found user

                // is username available
                if (getUserByUsername(newUsername) == null) {

                    val updatedUser = userToUpdate.copy(username = newUsername)

                    // update user in database
                    val updateResult = users.replaceOne(User::username eq usernameToFindUser, updatedUser)

                    // check if update finished okay
                    updateResult.wasAcknowledged()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun changeName(usernameToFindUser: String, newName: String) {
        try {
            val userToUpdate = getUserByUsername(usernameToFindUser)

            if (userToUpdate != null) {
                val updatedUser = userToUpdate.copy(name = newName)
                val updateResult = users.replaceOne(User::username eq usernameToFindUser, updatedUser)
                updateResult.wasAcknowledged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

    override suspend fun checkPassword(usernameToFindUser: String, password: String): Boolean {
        return try {
            val user = getUserByUsername(usernameToFindUser)
            if (user != null) {
                hashingService.verify(password, SaltedHash(user.password, user.salt))
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun checkUsername(username: String): Boolean {
        return try {
            val correctUsername = username.removeSurrounding("\"")
            val user = getUserByUsername(correctUsername)
            user == null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun checkEmail(email: String): Boolean {
        return try {
            val correctEmail = email.removeSurrounding("\"")
            val user = getUserByEmail(correctEmail)
            user == null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun changeEmail(usernameToFindUser: String, newEmail: String): Boolean {
        return try {
            val userToUpdate = getUserByUsername(usernameToFindUser)
            if (userToUpdate != null) {
                val updatedUser = userToUpdate.copy(email = newEmail)
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

    override suspend fun changeBio(usernameToFindUser: String, newBio: String): Boolean {
        return try {
            val userToUpdate = getUserByUsername(usernameToFindUser)
            println("USER(BIO): $userToUpdate")
            if (userToUpdate != null) {
                val updatedUser = userToUpdate.copy(userBio = newBio)
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