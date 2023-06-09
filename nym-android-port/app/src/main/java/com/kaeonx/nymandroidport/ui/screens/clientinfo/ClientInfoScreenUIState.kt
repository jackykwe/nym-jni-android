package com.kaeonx.nymandroidport.ui.screens.clientinfo

import com.kaeonx.nymandroidport.utils.NymRunState

internal data class ClientInfoScreenUIState(
    internal val clients: List<String>,
    internal val selectedClientId: String?,
    internal val selectedClientAddress: String?,
    internal val nymRunState: NymRunState
)