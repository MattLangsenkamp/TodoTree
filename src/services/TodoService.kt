package com.example.services

import com.example.model.Todo
import com.example.repository.TodoRepository
import com.mongodb.client.MongoClient
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.litote.kmongo.text
import java.time.Instant
import java.util.*

class TodoService : KoinComponent {
    private val client: MongoClient by inject()
    private val repo: TodoRepository = TodoRepository(client)

    fun getTodo(id: String): Todo {
        return buildTree(repo.getById(id))
    }

    fun getAllTodos(userId: String? = null, scopeId: String? = null, root: Boolean? = null) : List<Todo> {
        if (userId == null && scopeId == null && root == null) error("must provide one or more of scopeId, root, userId")
        return repo.getTodos(userId, scopeId).map { buildTree(it) }
    }

    fun addTodo(
        userId: String,
        text: String,
        completed: Boolean,
        scopeId: String,
        children: List<String>,
        parentTodoId: String
    ): Todo {

        val id = UUID.randomUUID().toString()
        val entry = Todo(
            id = id,
            userId = userId,
            creationTimeStamp = Instant.now(),
            text = text,
            completed = completed,
            scopeId = scopeId, false,
            children = children
        )

        val parentTodo = repo.getById(parentTodoId)
        val newParentTodo = parentTodo.copy(children = parentTodo.children + id)
        repo.update(newParentTodo)

        return buildTree(repo.add(entry))
    }

    fun updateTodo(
        id: String,
        userId: String?,
        text: String?,
        completed: Boolean?,
        scopeId: String?,
        children: List<String>?
    ): Todo {

        val todoBefore = repo.getById(id)
        val todo = Todo(
            todoBefore.id,
            userId ?: todoBefore.userId,
            todoBefore.creationTimeStamp,
            text ?: todoBefore.text,
            completed ?: todoBefore.completed,
            scopeId ?: todoBefore.scopeId, false,
            children ?: todoBefore.children
        )
        return buildTree(repo.update(todo))
    }

    fun deleteTodo(id: String): Todo {
        val todo = repo.getById(id)
        val children = todo.children
        val childrenObjects = mutableListOf<Todo>()
        for (c in children) {
            val child = repo.getById(c)
            childrenObjects.add(buildTree(child))
            repo.delete(c)
        }
        return Todo(
            id = todo.id,
            userId = todo.userId,
            creationTimeStamp = todo.creationTimeStamp,
            text = todo.text,
            completed = todo.completed,
            scopeId = todo.scopeId,
            rootTodo = todo.rootTodo,
            children = todo.children,
            childrenObjects = todo.childrenObjects
        )
    }

     private fun buildTree(todo: Todo): Todo {
        val children = todo.children
        val childrenObjects = mutableListOf<Todo>()
        for (c in children) {
            val child = repo.getById(c)
            childrenObjects.add(buildTree(child))
        }
        return Todo(
            id = todo.id,
            userId = todo.userId,
            creationTimeStamp = todo.creationTimeStamp,
            text = todo.text,
            completed = todo.completed,
            scopeId = todo.scopeId,
            rootTodo = todo.rootTodo,
            children = todo.children,
            childrenObjects = todo.childrenObjects
        )
    }

}