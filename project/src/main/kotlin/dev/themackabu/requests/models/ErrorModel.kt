package dev.themackabu.requests.models

interface ErrorModel {
    val error: String
    val code: Int
    val message: String
}