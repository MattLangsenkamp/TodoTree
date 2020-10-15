package com.example.repository
import com.example.model.User
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import org.litote.kmongo.* //NEEDED! import KMongo extensions


class UserRepository(private val client: MongoClient): RepositoryInterface<User> {
    private val col: MongoCollection<User>

    init {
        val database = client.getDatabase("todo") //normal java driver usage
        col = database.getCollection<User>("User")
    }

    override fun getById(id: String): User {
        return try {
            col.findOne(User::id eq id) ?: error("no user with that ID exists")
        } catch (t: Throwable) {
            error("Cannot get user")
        }
    }

    override fun getAll(): List<User> {
        return try {
            val res = col.find()
            res.asIterable().map { it }
        } catch (t: Throwable) {
            error("Cannot get all users")
        }
    }

    override fun delete(id: String): User {
        return try {
            val res = col.deleteOne<User>(User::id eq id)
            User(id = "", email = "", count = 0, hashedPass = byteArrayOf(0x55), permissionLevel = "User")
            // res.
        } catch (t: Throwable) {
            error("Cannot delete user")
        }
    }

    override fun add(entry: User): User {
        return try {
            val res = col.insertOne(entry)
            entry
        } catch (t: Throwable) {
            error("Cannot add user")
        }
    }

    override fun update(entry: User): User {
        return try {
            col.updateOne(
                User::id eq entry.id,
                User::email setTo entry.email,
                User::hashedPass setTo entry.hashedPass,
                )
            entry
        } catch (t: Throwable) {
            error("Cannot update user")
        }
    }

    fun getUsers(permissionLevel: String? = null): List<User> {
        val permissionLevelFilter = if (permissionLevel != null) User::permissionLevel eq permissionLevel else null
        return try {
            val res = col.find(
                permissionLevelFilter,
            )
            res.asIterable().map { it }
        } catch (t: Throwable) {
            error("Cannot get todos")
        }
    }

    fun getUserByEmail(email: String? = null): User {
        return try {
            col.findOne(
                User::email eq email,
            ) ?: error("could not get user with that email")
        } catch (t: Throwable) {
            error("Cannot get user with that email")
        }
    }
}