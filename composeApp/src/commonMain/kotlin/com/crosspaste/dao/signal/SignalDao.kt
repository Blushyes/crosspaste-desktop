package com.crosspaste.dao.signal

interface SignalDao {

    fun existPreKey(preKeyId: Int): Boolean

    fun getSignedPreKey(signedPreKeyId: Int): PasteSignedPreKey?

    fun saveIdentities(identityKeys: List<PasteIdentityKey>)

    fun saveIdentity(
        appInstanceId: String,
        serialized: ByteArray,
    ): Boolean

    fun deleteIdentity(appInstanceId: String)

    fun identity(appInstanceId: String): ByteArray?

    fun loadPreKey(id: Int): ByteArray?

    fun storePreKey(
        id: Int,
        serialized: ByteArray,
    )

    fun removePreKey(id: Int)

    fun loadSession(appInstanceId: String): ByteArray?

    fun loadExistingSessions(): List<ByteArray>

    fun storeSession(
        appInstanceId: String,
        sessionRecord: ByteArray,
    )

    fun containSession(appInstanceId: String): Boolean

    fun deleteSession(appInstanceId: String)

    fun deleteAllSession()

    fun loadSignedPreKey(id: Int): ByteArray?

    fun loadSignedPreKeys(): List<ByteArray>

    fun storeSignedPreKey(
        id: Int,
        serialized: ByteArray,
    )

    fun containsSignedPreKey(id: Int): Boolean

    fun removeSignedPreKey(id: Int)
}
