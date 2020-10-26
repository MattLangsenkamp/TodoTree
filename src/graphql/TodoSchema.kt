package com.example.graphql

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.example.model.LoggedInUser
import com.example.model.Todo
import com.example.services.TodoService
import java.time.Instant

fun SchemaBuilder.todoSchema(todoService: TodoService) {
    type<Todo>()
    query("todo") {
        resolver { id: String,
                   ctx: Context
            ->
            val user: LoggedInUser = ctx.get<LoggedInUser>() ?: error("Not Logged In")
            todoService.getTodo(id)
        }
    }

    query("todos") {
        resolver { userId: String?,
                   scopeId: String?,
                   rootTodo: Boolean?,
                   ctx: Context
            ->
            val user: LoggedInUser = ctx.get<LoggedInUser>() ?: error("Not Logged In")
            todoService.getAllTodos(userId, scopeId, rootTodo)
        }
    }

    mutation("addTodo") {
        resolver { userId: String,
                   text: String,
                   completed: Boolean,
                   scopeId: String,
                   rootTodo: Boolean,
                   children: List<String>,
                   parentTodoId: String?,
                   ctx: Context
            ->
            val user: LoggedInUser = ctx.get<LoggedInUser>() ?: error("Not Logged In")
            todoService.addTodo(userId, text, completed, scopeId, rootTodo, children, parentTodoId)
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
            val user: LoggedInUser = ctx.get<LoggedInUser>() ?: error("Not Logged In")
            todoService.updateTodo(id, userId, text, completed, scopeId, children)
        }
    }

    mutation("deleteTodo") {
        resolver { id: String,
                   ctx: Context
            ->
            val user: LoggedInUser = ctx.get<LoggedInUser>() ?: error("Not Logged In")
            todoService.deleteTodo(id)
        }
    }
}