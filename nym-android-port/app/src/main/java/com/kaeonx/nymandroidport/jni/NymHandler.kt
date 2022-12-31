package com.kaeonx.nymandroidport.jni

internal fun topLevelInit(storageAbsPath: String, configEnvFile: String? = null) =
    topLevelInitImpl(storageAbsPath, configEnvFile)

private external fun topLevelInitImpl(storageAbsPath: String, configEnvFile: String?)

internal fun nymInit(
    id: String,
    gateway: String? = null,
    forceRegisterGateway: Boolean = false,
    validators: String? = null,
    disableSocket: Boolean = false,
    port: UShort? = null,
    fastmode: Boolean? = false,
) = nymInitImpl(
    id, gateway, forceRegisterGateway, validators, disableSocket, port, fastmode
)

private external fun nymInitImpl(
    id: String,
    gateway: String?,
    forceRegisterGateway: Boolean,
    validators: String?,
    disableSocket: Boolean,
    port: UShort?,
    fastmode: Boolean?,
)


internal fun nymRun(
    id: String,
    validators: String? = null,
    gateway: String? = null,
    disable_socket: Boolean = false,
    port: UShort? = null,
) = nymRunImpl(
    id, validators, gateway, disable_socket, port
)

private external fun nymRunImpl(
    id: String,
    validators: String?,
    gateway: String?,
    disable_socket: Boolean,
    port: UShort?,
)

internal fun getAddress(id: String) = getAddressImpl(id)
private external fun getAddressImpl(id: String): String