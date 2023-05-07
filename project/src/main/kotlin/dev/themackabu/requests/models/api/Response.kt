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

@Serializable(with = DataSerializer::class)
data class AuthenticatedResponse<T>(
    val code: Int,
    val response: ResponseContext,
    val data: T
)


class DataSerializer<T>(private val dataSerializer: KSerializer<T>): KSerializer<AuthenticatedResponse<T>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AuthenticatedResponse") {
        element<Int>("code")
        element<ResponseContext>("response")
        element("data", dataSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: AuthenticatedResponse<T>) {
        val compositeOutput = encoder.beginStructure(descriptor)
        compositeOutput.encodeIntElement(descriptor, 0, value.code)
        compositeOutput.encodeSerializableElement(descriptor, 1, ResponseContext.serializer(), value.response)
        compositeOutput.encodeSerializableElement(descriptor, 2, dataSerializer, value.data)
        compositeOutput.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): AuthenticatedResponse<T> {
        error("Unsupported")
    }
}