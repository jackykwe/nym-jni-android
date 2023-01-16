package com.kaeonx.nymandroidport.utils

// Similar to the STATE pattern
internal enum class NymRunState {
    IDLE,
    SETTING_UP,
    SOCKET_OPEN,
    TEARING_DOWN;

    internal fun allowSelectRunAndDelete(): Boolean {
        return this == IDLE
    }

    internal fun allowStop(): Boolean {
        return this == SOCKET_OPEN
    }
}