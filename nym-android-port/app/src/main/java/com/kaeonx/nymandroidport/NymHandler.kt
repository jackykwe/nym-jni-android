package com.kaeonx.nymandroidport

internal fun init(testPath: String): String = initImpl(testPath)
private external fun initImpl(testPath: String): String