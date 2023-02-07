package com.kaeonx.nymandroidport.jni

import com.kaeonx.nymandroidport.services.NymRunForegroundService

internal fun topLevelInit(storageAbsPath: String, configEnvFile: String? = null) =
    topLevelInitImpl(storageAbsPath, configEnvFile)

private external fun topLevelInitImpl(storageAbsPath: String, configEnvFile: String?)

internal fun nymInit(
    id: String,
    gateway: String? = null,
    forceRegisterGateway: Boolean = false,
    nymdValidators: String? = null,
    apiValidators: String? = null,
    disableSocket: Boolean = false,
    port: UShort? = null,
    fastmode: Boolean = false,
    noCover: Boolean = false,
) = nymInitImpl(
    id,
    gateway,
    forceRegisterGateway,
    nymdValidators,
    apiValidators,
    disableSocket,
    port,
    fastmode,
    noCover
)

private external fun nymInitImpl(
    id: String,
    gateway: String?,
    forceRegisterGateway: Boolean,
    nymdValidators: String?,
    apiValidators: String?,
    disableSocket: Boolean,
    port: UShort?,
    fastmode: Boolean,
    noCover: Boolean
)

internal fun nymRun(
    nymRunForegroundService: NymRunForegroundService,
    id: String,
    nymdValidators: String? = null,
    apiValidators: String? = null,
    gateway: String? = null,
    disable_socket: Boolean = false,
    port: UShort? = null,
    fastmode: Boolean = false,
    noCover: Boolean = false
) = nymRunImpl(
    nymRunForegroundService,
    id,
    nymdValidators,
    apiValidators,
    gateway,
    disable_socket,
    port,
    fastmode,
    noCover
)

private external fun nymRunImpl(
    nymRunWorker: NymRunForegroundService,
    id: String,
    nymdValidators: String?,
    apiValidators: String?,
    gateway: String?,
    disable_socket: Boolean,
    port: UShort?,
    fastmode: Boolean,
    noCover: Boolean
)

internal fun getAddress(id: String) = getAddressImpl(id)
private external fun getAddressImpl(id: String): String