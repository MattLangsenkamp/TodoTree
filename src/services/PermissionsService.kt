package com.example.services

import com.example.model.LoggedInUser
import com.example.model.Scope
import com.example.model.Todo
import com.example.model.User
import com.example.repository.ScopeRepository
import com.example.repository.TodoRepository
import com.example.repository.UserRepository
import com.mongodb.client.MongoClient
import org.koin.core.KoinComponent
import org.koin.core.inject

class PermissionsService: KoinComponent {
    private val client: MongoClient by inject()
    private val todoRepo: TodoRepository = TodoRepository(client)
    private val scopeRepo: ScopeRepository = ScopeRepository(client)
    private val userRepo: UserRepository = UserRepository(client)

    fun checkPermissionByUser(loggedInUser: LoggedInUser, userId: String) {

        val user = userRepo.getById(userId)

        if (loggedInUser.permissionLevel == "Admin") return
        if (loggedInUser.id == user.id) return

        error("User is not authorized to perform this action")
    }

    fun checkPermissionByUser(loggedInUser: LoggedInUser, user: User) {
        if (loggedInUser.permissionLevel == "Admin") return
        if (loggedInUser.id == user.id) return

        error("User is not authorized to perform this action")
    }

    fun checkPermissionByScope(loggedInUser: LoggedInUser, scopeId: String) {

    }

    fun checkPermissionByScope(loggedInUser: LoggedInUser, scope: Scope) {

    }

    fun checkPermissionByTodo(loggedInUser: LoggedInUser, todoId: String) {

    }

    fun checkPermissionByTodo(loggedInUser: LoggedInUser, todo: Todo) {

    }
}