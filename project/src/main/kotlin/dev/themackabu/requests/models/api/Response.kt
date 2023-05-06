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
data class AuthenticatedResponse<T>(
    val code: Int,
    val response: ResponseContext,
    val data: T
)

//class DataSerializer<T>(
//    private val dataSerializer: KSerializer<T>
//): KSerializer<AuthenticatedResponse<T>> {
//    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AuthenticatedResponse") {
//        element("code", dataSerializer.descriptor)
//        element("response", dataSerializer.descriptor)
//        element("data", dataSerializer.descriptor)
//    }
//
//    override fun serialize(encoder: Encoder, value: AuthenticatedResponse<T>) {
//        val compositeOutput = encoder.beginStructure(descriptor)
//        compositeOutput.encodeSerializableElement(descriptor, 2, dataSerializer, value.data)
//        compositeOutput.endStructure(descriptor)
//    }
//
//    override fun deserialize(decoder: Decoder): AuthenticatedResponse<T> {
//        var code: Int? = null
//        var response: ResponseContext? = null
//        var data: T? = null
//
//        val compositeInput = decoder.beginStructure(descriptor)
//        loop@ while (true) {
//            when (val index = compositeInput.decodeElementIndex(descriptor)) {
//                CompositeDecoder.DECODE_DONE -> break@loop
//                2 -> data = compositeInput.decodeSerializableElement(descriptor, index, dataSerializer)
//                else -> throw SerializationException("Unknown index $index")
//            }
//        }
//        compositeInput.endStructure(descriptor)
//
//        return AuthenticatedResponse(
//            code ?: throw SerializationException("Missing code"),
//            response ?: throw SerializationException("Missing response"),
//            data ?: throw SerializationException("Missing data")
//        )
//    }
//}