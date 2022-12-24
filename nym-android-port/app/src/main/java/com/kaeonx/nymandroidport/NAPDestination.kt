package com.kaeonx.nymandroidport

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NAPDestination(
    val route: String,
    val displayName: String,
    val icon: ImageVector,
) {
    object ClientInfo : NAPDestination("clientinfo", "Clients", Icons.Default.Info)
    object Chat : NAPDestination("chat", "Chat", Icons.Default.MailOutline)
}

val bottomNavBarItems = listOf(
    NAPDestination.ClientInfo,
    NAPDestination.Chat
)