package com.example.repository

import com.example.model.Scope
import com.example.model.Todo
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import org.bson.conversions.Bson
import org.litote.kmongo.*
import java.time.Instant

class TodoRepository(private val client: MongoClient) : RepositoryInterface<Todo> {
    val col: MongoCollection<Todo>

    init {
        val database = client.getDatabase("todo")
        col = database.getCollection<Todo>("Todo")
    }

    override fun getById(id: String): Todo {
        return try {
            col.findOne(Todo::id eq id) ?: error("No todo with that ID exists")
        } catch (t: Throwable) {
            error("Cannot get todo")
        }
    }

    override fun getAll(): List<Todo> {
        return try {
            val res = col.find()
            res.asIterable().map { it }
        } catch (t: Throwable) {
            error("Cannot get all todos")
        }
    }

    override fun delete(id: String): Todo {
        return try {
            val res = col.deleteOne<Todo>(Todo::id eq id)
            Todo(id = "", userId = "", creationTimeStamp = Instant.now(), "", false, "", false, listOf())
            // res.
        } catch (t: Throwable) {
            error("Cannot delete todo")
        }
    }

    override fun add(entry: Todo): Todo {
        return try {
            val res = col.insertOne(entry)
            entry
        } catch (t: Throwable) {
            error("Cannot add todo")
        }
    }

    override fun update(entry: Todo): Todo {
        return try {
            col.updateOne(
                Todo::id eq entry.id,
                Todo::userId setTo entry.userId,
                Todo::creationTimeStamp setTo entry.creationTimeStamp,
                Todo::text setTo entry.text,
                Todo::completed setTo entry.completed,
                Todo::scopeId setTo entry.scopeId,
                Todo::children setTo entry.children,
            )
            entry
        } catch (t: Throwable) {
            error("Cannot update todo")
        }
    }

    fun getTodos(userId: String? = null, scopeId: String? = null, rootTodo: Boolean? = null): List<Todo> {
        val userIdFilter = if (userId != null) Todo::userId eq userId else null
        val scopeIdFiler = if (scopeId != null) Todo::scopeId eq scopeId else null
        val rootTodoFilter = if (rootTodo != null) Todo::rootTodo eq rootTodo else null
        return try {
            val res = col.find(
                userIdFilter,
                scopeIdFiler,
                rootTodoFilter
            )
            res.asIterable().map { it }
        } catch (t: Throwable) {
            error("Cannot get todos")
        }
    }
}