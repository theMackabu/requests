package dev.themackabu.requests.models.api

import kotlinx.serialization.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.descriptors.*
import dev.themackabu.requests.models.cmd.ResponseContext

@Serializable
data class Response(
    val code: Int,
    val error: String?,
    val message: String?
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class AuthenticatedResponse(
    val code: Int,
    val response: ResponseContext,
    @Serializable(with = DynamicLookupSerializer::class) val data: Any?
)

@Suppress("UNCHECKED_CAST")
@ExperimentalSerializationApi
@OptIn(InternalSerializationApi::class)
class DynamicLookupSerializer: KSerializer<Any> {
    override val descriptor: SerialDescriptor = ContextualSerializer(Any::class, null, emptyArray()).descriptor

    override fun serialize(encoder: Encoder, value: Any) {
        val actualSerializer = encoder.serializersModule.getContextual(value::class) ?: value::class.serializer()
        encoder.encodeSerializableValue(actualSerializer as KSerializer<Any>, value)
    }

    override fun deserialize(decoder: Decoder): Any {
        error("Unsupported")
    }
}