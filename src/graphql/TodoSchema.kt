package com.example.graphql

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.example.model.LoggedInUser
import com.example.model.Todo
import com.example.services.PermissionsService
import com.example.services.TodoService

fun SchemaBuilder.todoSchema(todoService: TodoService) {

    type<Todo>()
    query("todo") {
        resolver { id: String,
                   ctx: Context
            ->
            val user: LoggedInUser = ctx.get() ?: error("Not Logged In")
            todoService.getTodo(user, id)
        }
    }

    query("todos") {
        resolver { userId: String?,
                   scopeId: String?,
                   rootTodo: Boolean?,
                   ctx: Context
            ->
            val user: LoggedInUser = ctx.get() ?: error("Not Logged In")
            todoService.getAllTodos(user, userId, scopeId, rootTodo)
        }
    }

    mutation("addTodo") {
        resolver { text: String,
                   completed: Boolean,
                   scopeId: String,
                   rootTodo: Boolean,
                   children: List<String>,
                   parentTodoId: String?,
                   ctx: Context
            ->
            val user: LoggedInUser = ctx.get() ?: error("Not Logged In")
            todoService.addTodo(user, user.id, text, completed, scopeId, rootTodo, children, parentTodoId)
        }
    }

    mutation("updateTodo") {
        resolver { id: String,
                   userId: String?,
                   text: String?,
                   completed: Boolean?,
                   scopeId: String?,
                   children: List<String>?,
                   ctx: Context
            ->
            val user: LoggedInUser = ctx.get() ?: error("Not Logged In")
            todoService.updateTodo(user, id, userId, text, completed, scopeId, children)
        }
    }

    mutation("deleteTodo") {
        resolver { id: String,
                   ctx: Context
            ->
            val user: LoggedInUser = ctx.get() ?: error("Not Logged In")
            todoService.deleteTodo(user, id)
        }
    }
}