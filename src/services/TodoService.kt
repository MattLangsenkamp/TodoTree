package com.example.services

import com.example.model.LoggedInUser
import com.example.model.Todo
import com.example.repository.ScopeRepository
import com.example.repository.TodoRepository
import com.mongodb.client.MongoClient
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.time.Instant
import java.util.*

class TodoService : KoinComponent {
    private val client: MongoClient by inject()
    private val repo: TodoRepository = TodoRepository(client)
    private val scopeRepo: ScopeRepository = ScopeRepository(client)
    private val permissionsService = PermissionsService()

    fun getTodo(user: LoggedInUser, id: String): Todo {
        val todo = repo.getById(id)
        permissionsService.checkPermissionByTodo(user, todo)
        return buildTree(todo)
    }

    fun getAllTodos(
        user: LoggedInUser,
        userId: String? = null,
        scopeId: String? = null,
        root: Boolean? = null
    ): List<Todo> {
        when {
            (userId != null) -> permissionsService.checkPermissionByUser(user, userId)
            (scopeId != null) -> permissionsService.checkPermissionByScope(user, scopeId)
            else -> error("Must provide either scopeId or userId")
        }
        return repo.getTodos(userId, scopeId, root).map { buildTree(it) }
    }

    fun addTodo(
        user: LoggedInUser,
        userId: String,
        text: String,
        completed: Boolean,
        scopeId: String,
        rootTodo: Boolean,
        children: List<String>,
        parentTodoId: String? = null
    ): Todo {

        // check to make sure referenced scope exists and we have permissions to touch it
        permissionsService.checkPermissionByScope(user, scopeRepo.getById(scopeId))

        // check to make sure referenced parent exists and we have permissions to touch it
        if (parentTodoId != null) permissionsService.checkPermissionByTodo(user, repo.getById(parentTodoId))

        if (!rootTodo && parentTodoId == null) error("new todo must either be a root todo or have a parent")

        val id = UUID.randomUUID().toString()
        val entry = Todo(
            id = id,
            userId = userId,
            creationTimeStamp = Instant.now(),
            text = text,
            completed = completed,
            scopeId = scopeId,
            rootTodo = false,
            parentTodoId = parentTodoId,
            children = children
        )

        if (parentTodoId != null) {
            val parentTodo = repo.getById(parentTodoId)
            val newParentTodo = parentTodo.copy(children = parentTodo.children + id)
            repo.update(newParentTodo)
        }

        return buildTree(repo.add(entry))
    }

    fun updateTodo(
        user: LoggedInUser,
        id: String,
        userId: String?,
        text: String?,
        completed: Boolean?,
        scopeId: String?,
        children: List<String>?
    ): Todo {

        // check to make sure referenced scope exists
        if (scopeId != null) permissionsService.checkPermissionByScope(user, scopeRepo.getById(scopeId))

        val todoBefore = repo.getById(id)
        permissionsService.checkPermissionByTodo(user, todoBefore)

        val todo = todoBefore.copy(
            userId = userId ?: todoBefore.userId,
            text = text ?: todoBefore.text,
            completed = completed ?: todoBefore.completed,
            scopeId = scopeId ?: todoBefore.scopeId,
            children = children ?: todoBefore.children
        )
        return buildTree(repo.update(todo))
    }

    fun deleteTodo(user: LoggedInUser, id: String, rootDelete: Boolean = true): Todo {
        val todo = repo.getById(id)
        if (rootDelete) permissionsService.checkPermissionByTodo(user, todo)
        val children = todo.children
        val childrenObjects = mutableListOf<Todo>()
        for (c in children) {
            childrenObjects.add(deleteTodo(user, c, false))
        }
        // remove id of "todo_" being deleted as a child from the parent if there is a parent
        if (todo.parentTodoId != null && rootDelete) {
            val parentTodo = repo.getById(todo.parentTodoId)
            val newParentTodo = parentTodo.copy(children = parentTodo.children - id)
            repo.update(newParentTodo)
        }

        repo.delete(id)
        return todo.copy(childrenObjects = childrenObjects)
    }

    private fun buildTree(todo: Todo): Todo {
        val children = todo.children
        val childrenObjects = mutableListOf<Todo>()
        for (c in children) {
            val child = repo.getById(c)
            childrenObjects.add(buildTree(child))
        }
        return todo.copy(childrenObjects = childrenObjects)
    }

}