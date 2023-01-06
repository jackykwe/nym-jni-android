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
import com.kaeonx.nymandroidport.ui.screens.contacts.ContactsScreen

@Composable
fun NAPNavHost(
    navController: NavHostController,  // hoisting navController out of NavHost
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NAPDestination.ClientInfo.route,
        modifier = modifier.padding(16.dp)  // .padding() adds padding, not overwrite
    ) {
        // This pattern of using callbacks to get the navController in NymAndroidPortApp to navigate
        // is exemplified here: <https://developer.android.com/jetpack/compose/navigation#nav-from-composable>
        composable(route = NAPDestination.ClientInfo.route) {
            ClientInfoScreen()
        }
        composable(route = NAPDestination.Contacts.route) {
            ContactsScreen(nymAddressSelected = { nymAddress ->
                navController.navigate("${NAPDestination.Chat.route}/$nymAddress") {
                    launchSingleTop = true
                }
            })
        }
        composable(route = "${NAPDestination.Chat.route}/{contactAddress}") { backStackEntry ->
            ChatScreen(
                contactAddress = backStackEntry.arguments?.getString("contactAddress")!!,
                onContactDeleted = { navController.popBackStack() }
            )
        }
    }
}