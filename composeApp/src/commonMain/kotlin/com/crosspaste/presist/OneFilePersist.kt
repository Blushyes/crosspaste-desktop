package com.crosspaste.presist

import io.ktor.utils.io.*
import okio.Path
import kotlin.reflect.KClass

interface OneFilePersist {

    val path: Path

    fun <T : Any> read(clazz: KClass<T>): T?

    fun readBytes(): ByteArray?

    fun <T> save(config: T)

    fun saveBytes(bytes: ByteArray)

    fun delete(): Boolean

    suspend fun writeChannel(channel: ByteReadChannel)
}
