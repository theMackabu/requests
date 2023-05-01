package dev.themackabu.requests.models

interface PlayerInterface {
    val uuid: String
    val username: String
    val level: Int
    val allowFlight: Boolean
    val status: PlayerStatus
}

interface PlayerStatus {
    val online: Boolean
}

data class Status(
    override val online: Boolean
): PlayerStatus

data class Player(
    override val uuid: String,
    override val username: String,
    override val level: Int,
    override val allowFlight: Boolean,
    override val status: Status,
): PlayerInterface
