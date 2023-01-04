package com.kaeonx.nymandroidport.ui.screens.clientinfo

import androidx.work.WorkInfo

internal data class ClientInfoScreenUIState(
    internal val clients: List<String>,
    internal val selectedClientId: String?,
    internal val selectedClientAddress: String?,
    internal val nymRunState: NymRunState,
    internal val nymRunWorkInfo: WorkInfo?,
)