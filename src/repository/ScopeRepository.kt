package com.example.repository
import com.example.model.Scope
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import org.litote.kmongo.* //NEEDED! import KMongo extensions
import java.time.Instant


class ScopeRepository(private val client: MongoClient): RepositoryInterface<Scope> {
    private val col: MongoCollection<Scope>

    init {
        val database = client.getDatabase("todo")
        col = database.getCollection<Scope>("Scope")
    }

    override fun getById(id: String): Scope {
        return try {
            col.findOne(Scope::id eq id) ?: error("no scope with that ID exists")
        } catch (t: Throwable) {
            error("Cannot get scope")
        }
    }

    override fun getAll(): List<Scope> {
        return try {
            val res = col.find()
            res.asIterable().map { it }
        } catch (t: Throwable) {
            error("Cannot get all scopes")
        }
    }

    override fun delete(id: String): Scope {
        return try {
            val res = col.deleteOne<Scope>(Scope::id eq id)
            Scope(id = "",
                userId = "",
                defaultScope=false,
                creationTimeStamp = Instant.now(), "")
        } catch (t: Throwable) {
            error("Cannot delete scope")
        }
    }

    override fun add(entry: Scope): Scope {
        return try {
            val res = col.insertOne(entry)
            entry
        } catch (t: Throwable) {
            error("Cannot add scope")
        }
    }

    override fun update(entry: Scope): Scope {
        return try {
            col.updateOne(
                Scope::id eq entry.id,
                Scope::userId setTo entry.userId,
                Scope::defaultScope setTo entry.defaultScope,
                Scope::creationTimeStamp setTo entry.creationTimeStamp,
                Scope::name setTo entry.name,
                Scope::description setTo entry.description,
                Scope::startTime setTo entry.startTime,
                Scope::endTime setTo entry.endTime
                )
            entry
        } catch (t: Throwable) {
            error("Cannot update todo")
        }
    }

    fun getScopes(userId: String?): List<Scope> {
        val userIdFilter = if (userId != null) Scope::userId eq userId else null
        return try {
            val res = col.find(
                userIdFilter
            )
            res.asIterable().map { it }
        } catch (t: Throwable) {
            error("Cannot get scopes")
        }
    }
}