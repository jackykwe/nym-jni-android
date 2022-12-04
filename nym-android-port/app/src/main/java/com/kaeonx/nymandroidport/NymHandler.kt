package com.kaeonx.nymandroidport

internal fun topLevelInit() = topLevelInitImpl()
private external fun topLevelInitImpl()

internal fun nymInit(storageAbsPath: String, id: String): String = nymInitImpl(storageAbsPath, id)
private external fun nymInitImpl(storageAbsPath: String, id: String): String