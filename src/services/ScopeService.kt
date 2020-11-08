package com.example.services

import com.example.model.LoggedInUser
import com.example.model.Scope
import com.example.repository.ScopeRepository
import com.example.repository.TodoRepository
import com.example.repository.UserRepository
import com.mongodb.client.MongoClient
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.ext.scope
import java.time.Instant
import java.util.*

class ScopeService(private val todoService: TodoService) : KoinComponent {
    private val client: MongoClient by inject()
    private val repo: ScopeRepository = ScopeRepository(client)
    private val userRepo: UserRepository = UserRepository(client)
    private val todoRepo: TodoRepository = TodoRepository(client)
    private val permissionsService = PermissionsService()

    fun getScope(user: LoggedInUser, id: String): Scope {
        return repo.getById(id)
    }

    fun getAllScopes(user: LoggedInUser, userId: String): List<Scope> {
        return repo.getScopes(userId)
    }

    fun addScope(
        user: LoggedInUser,
        userId: String,
        defaultScope: Boolean,
        name: String,
        description: String?,
        startTime: Long?,
        endTime: Long?
    ): Scope {

        // checks to see if referenced use exists
        permissionsService.checkPermissionByUser(user, userRepo.getById(userId))

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
        user: LoggedInUser,
        id: String,
        userId: String? = null,
        defaultScope: Boolean? = null,
        name: String? = null,
        description: String? = null,
        startTime: Long? = null,
        endTime: Long? = null
    ): Scope {

        // checks to see if referenced user exists and we have rights to touch it
        if (userId != null) permissionsService.checkPermissionByUser(user,userRepo.getById(userId))

        val scopeBefore = repo.getById(id)
        permissionsService.checkPermissionByScope(user, scopeBefore)
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

    fun deleteScope(user: LoggedInUser, scopeId: String): Scope {

        permissionsService.checkPermissionByScope(user, scopeId)

        val rootTodos = todoRepo.getTodos(scopeId = scopeId, rootTodo = true)
        for (root in rootTodos) {
              todoService.deleteTodo(user, root.id)
        }
        return repo.delete(scopeId)
    }
}