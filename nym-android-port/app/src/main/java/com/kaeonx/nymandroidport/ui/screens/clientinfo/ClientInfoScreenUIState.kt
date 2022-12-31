package com.kaeonx.nymandroidport.ui.screens.clientinfo

data class ClientInfoScreenUIState(
    val clients: List<String>,
    val selectedClientId: String?,
    val selectedClientAddress: String?
)