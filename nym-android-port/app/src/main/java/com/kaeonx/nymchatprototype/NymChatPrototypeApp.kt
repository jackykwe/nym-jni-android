package com.kaeonx.nymchatprototype

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.kaeonx.nymchatprototype.*

// To pass the snackbarHostState into the hierarchy without manual "prop drilling"
internal val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun NymChatPrototypeApp() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        NymChatPrototypeAppInner()
    } else {
        // Permission
        val notificationPermissionState =
            rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)
        if (notificationPermissionState.status.isGranted) {
            NymChatPrototypeAppInner()
        } else {
            Column(modifier = Modifier.padding(8.dp)) {
                val textToShow = if (notificationPermissionState.status.shouldShowRationale) {
                    // If the user has denied the permission but the rationale can be shown,
                    // then gently explain why the app requires this permission
                    "Without the notifications permission, nym is unable to continuously sync messages in the background. Please grant the permission."
                } else {
                    // If it's the first time the user lands on this feature, or the user
                    // doesn't want to be asked again for this permission, explain that the
                    // permission is required
                    "Nym requires the notifications permission to be able to continuously sync messages in the background. Please grant the permission."
                }
                Text(textToShow)
                Button(
                    onClick = { notificationPermissionState.launchPermissionRequest() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Request permission")
                }
                Text(text = "If the button above doesn't work, please manually enable notifications in your phone's application settings.")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NymChatPrototypeAppInner() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // For snackbars
    val snackbarHostState = remember { SnackbarHostState() }
    // To pass the snackbarHostState into the hierarchy without manual "prop drilling",
    // we use CompositionLocal. Initial hint from: https://stackoverflow.com/a/69905470
    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        if (currentDestination?.route?.startsWith(NAPDestination.Chat.route) == true) {
                            IconButton(
                                onClick = {
                                    navController.popBackStack()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    bottomNavBarItems.forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                            onClick = {
                                navController.navigate(destination.route) {
                                    // Pop up to the start destination of the graph first to avoid
                                    // building up a large stack of destinations on the back stack as
                                    // users select items
                                    popUpTo(navController.graph.findStartDestination().id)
                                    // Avoid multiple copies of the same destination when reselecting
                                    // the same item
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = destination.bottomBarDisplayName) }
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            NAPNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}