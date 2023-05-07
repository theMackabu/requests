package dev.themackabu.requests.api

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.application.ApplicationCall
import dev.themackabu.requests.models.api.Response
import dev.themackabu.requests.models.api.ApiException

suspend fun unauthorizedPage(call: ApplicationCall, status: HttpStatusCode) {
    call.response.status(HttpStatusCode.Unauthorized)
    call.respond(Response(
        code = 401,
        error = "server.unauthorized",
        message = "$status"
    ))
}

suspend fun forbiddenPage(call: ApplicationCall, status: HttpStatusCode) {
    call.response.status(HttpStatusCode.Forbidden)
    call.respond(Response(
        code = 403,
        error = "server.forbidden",
        message = "$status"
    ))
}

suspend fun serverErrorPage(call: ApplicationCall, status: HttpStatusCode) {
    call.response.status(HttpStatusCode.InternalServerError)
    call.respond(Response(
        code = 500,
        error = "server.unauthorized",
        message = "$status"
    ))
}

suspend fun apiError(call: ApplicationCall, cause: ApiException) {
    call.response.status(HttpStatusCode.InternalServerError)
    call.respond(Response(
        code = cause.code,
        error = cause.error,
        message = cause.message
    ))
}

suspend fun serverError(call: ApplicationCall, cause: Throwable) {
    call.response.status(HttpStatusCode.InternalServerError)
    call.respond(Response(
        code = 500,
        error = "server.exception",
        message = "$cause"
    ))
}