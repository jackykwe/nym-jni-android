package com.kaeonx.nymandroidport

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kaeonx.nymandroidport.ui.screens.chat.ChatScreen
import com.kaeonx.nymandroidport.ui.screens.clientinfo.ClientInfoScreen

@Composable
fun NAPNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NAPDestination.ClientInfo.route,
        modifier = modifier.padding(16.dp)  // .padding() adds padding, not overwrite
    ) {
        composable(route = NAPDestination.ClientInfo.route) {
            ClientInfoScreen()
        }
        composable(route = NAPDestination.Chat.route) {
            ChatScreen()
        }
    }
}