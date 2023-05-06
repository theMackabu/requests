package dev.themackabu.requests.helpers

import java.util.Base64
import java.security.SecureRandom
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import com.aventrix.jnanoid.jnanoid.NanoIdUtils

val json = Json { allowStructuredMapKeys = true }
inline fun <reified T> T.toJson(): String = json.encodeToString(this)
inline fun <reified T> String.fromJson(): T = json.decodeFromString(this)

fun nanoid(
    length: Int = 21,
    chars: String = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
): String = NanoIdUtils.randomNanoId(SecureRandom(), chars.toCharArray(), length)

fun String.toBase64(): String = Base64.getEncoder().encodeToString(this.toByteArray())
fun String.fromBase64(): String = String(Base64.getDecoder().decode(this))