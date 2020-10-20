package com.example.services

import com.example.model.Scope
import com.example.repository.ScopeRepository
import com.mongodb.client.MongoClient
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.ext.scope
import java.time.Instant
import java.util.*

class ScopeService(private val todoService: TodoService) : KoinComponent {
    private val client: MongoClient by inject()
    private val repo: ScopeRepository = ScopeRepository(client)

    fun getScope(id: String): Scope {
        return repo.getById(id)
    }

    fun getAllScopes(userId: String): List<Scope> {
        return repo.getScopes(userId)
    }

    fun addScope(
        userId: String,
        defaultScope: Boolean,
        name: String,
        description: String?,
        startTime: Long?,
        endTime: Long?
    ): Scope {
        val id = UUID.randomUUID().toString()
        val creationTimeStamp = Instant.now()
        val startTimeInstant = if (startTime == null)
            null else Instant.ofEpochMilli(startTime*1000L)
        val endTimeInstant = if (endTime == null)
            null else Instant.ofEpochMilli(endTime*1000L)
        val scope = Scope(
            id,
            userId,
            defaultScope,
            creationTimeStamp,
            name,
            description,
            startTimeInstant,
            endTimeInstant
        )
        return repo.add(scope)
    }

    fun updateScope(
        id: String,
        userId: String? = null,
        defaultScope: Boolean? = null,
        name: String? = null,
        description: String? = null,
        startTime: Long? = null,
        endTime: Long? = null
    ): Scope {
        val scopeBefore = repo.getById(id)
        val startTimeInstant = if (startTime == null)
            scopeBefore.startTime else Instant.ofEpochMilli(startTime*1000L)
        val endTimeInstant = if (endTime == null)
            scopeBefore.endTime else Instant.ofEpochMilli(endTime*1000L)

        return repo.update(
            Scope(
                id = id,
                userId = userId ?: scopeBefore.userId,
                defaultScope = defaultScope ?: scopeBefore.defaultScope,
                creationTimeStamp = scopeBefore.creationTimeStamp,
                name = name ?: scopeBefore.name,
                description = description ?: scopeBefore.description,
                startTime = startTimeInstant,
                endTime = endTimeInstant
            )
        )
    }

    fun deleteScope(scopeId: String): Scope {
        val rootTodos = todoService.getAllTodos(scopeId = scopeId)
        for (root in rootTodos) {
              repo.delete(root.id)
        }
        return repo.delete(scopeId)
    }
}