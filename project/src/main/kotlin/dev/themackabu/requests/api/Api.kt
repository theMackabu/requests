package dev.themackabu.requests.api

import dev.themackabu.requests.Main
import dev.themackabu.requests.utils.Logger
import dev.themackabu.requests.api.FileLogger
import dev.themackabu.requests.api.Player
import dev.themackabu.requests.api.PlayerInterface

import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.application.*
import io.ktor.serialization.gson.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.http.*

import cafe.adriel.satchel.ktx.get
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.logging.Level

fun getPlayer(uuid: String): PlayerInterface {
    var gson = Gson()
    val db = Main.players
    val data = db.get<String>("players.$uuid")
    val json = gson.fromJson(data, Player::class.java)

    return json
}

class Api(port: Int) {
    private var server: NettyApplicationEngine
    var gson = Gson()

    init {
        server = embeddedServer(Netty, port = port) {
            install(DefaultHeaders)
            install(DoubleReceive)
            install(FileLogger)

            install(ContentNegotiation) { gson() }
            install(CORS) { anyHost() }

            routing {
                get("/player/{uuid}") {
                    val uuid = call.parameters["uuid"]
                    val json = getPlayer(uuid as String)

                    call.respond(json)
                }
            }
        }

        server.start()
        Logger.log(Level.INFO, "API server started on port $port.")
    }

    fun start(): NettyApplicationEngine {
        return server
    }
}