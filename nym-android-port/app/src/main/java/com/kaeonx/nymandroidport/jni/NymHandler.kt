package com.kaeonx.nymandroidport.jni

internal fun topLevelInit(configEnvFile: String? = null) = topLevelInitImpl(configEnvFile)
private external fun topLevelInitImpl(configEnvFile: String?)

internal fun nymInit(
    storageAbsPath: String,
    id: String,
    gateway: String? = null,
    forceRegisterGateway: Boolean = false,
    validators: String? = null,
    disableSocket: Boolean = false,
    port: UShort? = null,
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
    port: UShort?,
    fastmode: Boolean?,
)


internal fun nymRun(
    storageAbsPath: String,
    id: String,
    validators: String? = null,
    gateway: String? = null,
    disable_socket: Boolean = false,
    port: UShort? = null,
) = nymRunImpl(
    storageAbsPath, id, validators, gateway, disable_socket, port
)

private external fun nymRunImpl(
    storageAbsPath: String,
    id: String,
    validators: String?,
    gateway: String?,
    disable_socket: Boolean,
    port: UShort?,
)
