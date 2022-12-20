package com.kaeonx.nymandroidport.jni

internal fun test(arg: UInt?): UInt? = testImpl(arg)
private external fun testImpl(arg: UInt?): UInt?

internal fun topLevelInit(configEnvFile: String? = null) = topLevelInitImpl(configEnvFile)
private external fun topLevelInitImpl(configEnvFile: String?)

internal fun nymInit(
    storageAbsPath: String,
    id: String,
    gateway: String? = null,
    forceRegisterGateway: Boolean = false,
    validators: String? = null,
    disableSocket: Boolean = false,
    port: Int? = null,
    fastmode: Boolean? = false,
) = nymInitImpl(
    storageAbsPath, id, gateway, forceRegisterGateway, validators, disableSocket, port, fastmode
)

private external fun nymInitImpl(
    storageAbsPath: String,
    id: String,
    gateway: String?,
    forceRegisterGateway: Boolean,
    validators: String?,
    disableSocket: Boolean,
    port: Int?,
    fastmode: Boolean?,
)