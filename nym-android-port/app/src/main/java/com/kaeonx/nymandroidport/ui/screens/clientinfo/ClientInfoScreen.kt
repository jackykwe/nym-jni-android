package com.kaeonx.nymandroidport.ui.screens.clientinfo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaeonx.nymandroidport.LocalSnackbarHostState
import com.kaeonx.nymandroidport.R
import kotlinx.coroutines.launch

private const val NONE_OPTION = "<none>"
private const val ADD_NEW_OPTION = "<add new...>"

fun getDisplayClients(list: List<String>) = list.toMutableList().apply {
    add(0, NONE_OPTION)
    add(ADD_NEW_OPTION)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientInfoScreen(clientInfoViewModel: ClientInfoViewModel = viewModel()) {
    // For ExposedDropdownMenuBox
    var clientSelectionExpanded by remember { mutableStateOf(false) }
    val clientInfoScreenUIState by clientInfoViewModel.clientInfoScreenUIState.collectAsState()

    // For AlertDialogs
    var createClientDialogOpen by remember { mutableStateOf(false) }
    var createClientDialogLoading by remember { mutableStateOf(false) }
    var createClientDialogNewName by remember { mutableStateOf(TextFieldValue("")) }
    var deleteClientDialogOpen by remember { mutableStateOf(false) }
    var deleteClientDialogLoading by remember { mutableStateOf(false) }

    // For snackbar
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        ExposedDropdownMenuBox(
            expanded = clientSelectionExpanded,
            onExpandedChange = { clientSelectionExpanded = !clientSelectionExpanded }) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = clientInfoScreenUIState.selectClientName ?: NONE_OPTION,
                onValueChange = {},
                label = { Text(text = "Active Nym Client") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clientSelectionExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            DropdownMenu(
                expanded = clientSelectionExpanded,
                onDismissRequest = { clientSelectionExpanded = false },
                modifier = Modifier.exposedDropdownSize()  // Bug fix courtesy of: https://stackoverflow.com/a/70683378
            ) {
                getDisplayClients(clientInfoScreenUIState.clients).forEach { option ->
                    DropdownMenuItem(
                        modifier = Modifier,
                        text = { Text(text = option) },
                        onClick = {
                            when (option) {
                                ADD_NEW_OPTION -> createClientDialogOpen = true
                                NONE_OPTION -> clientInfoViewModel.unselectClient()
                                else -> clientInfoViewModel.selectClient(option)
                            }
                            clientSelectionExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
        // Diagnostics
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp, bottom = 8.dp)
        ) {
            Column {
                Text(
                    text = "For every work that has been scheduled before, there will be one entry that shows up below. Since we are using unique work, there should be at most 1. \nStates:\n - RUNNING: in execution in the background (run forever)\n - FAILED: terminated, not running, but some error occurred (typically when stopping the client too soon after running)\n - SUCCEEDED: terminated, not running",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
                val workInfo by clientInfoViewModel.nymRunWorkInfo.observeAsState()
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    workInfo?.forEach {
                        Text(
                            text = "Client \"${clientInfoScreenUIState.selectClientName}\" state: ${it.state}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                val clipboard = LocalClipboardManager.current
                Text(
                    text = "Nym address for \"${clientInfoScreenUIState.selectClientName}\" is",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = clientInfoScreenUIState.selectedClientAddress.toString(),
                        modifier = Modifier.weight(1f),
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall
                    )
                    IconButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(clientInfoScreenUIState.selectedClientAddress!!))
                        },
                        enabled = clientInfoScreenUIState.selectedClientAddress != null
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_content_copy_24),
                            contentDescription = null
                        )
                    }
                }
            }

        }

        if (clientInfoScreenUIState.selectClientName != null) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { deleteClientDialogOpen = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Delete Nym Client \"${clientInfoScreenUIState.selectClientName}\"",
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { clientInfoViewModel.unselectClient() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text(text = "Stop running Nym clients")
        }
    }

    if (createClientDialogOpen) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Create Nym Client") },
            text = {
                Column {
                    TextField(
                        value = createClientDialogNewName,
                        onValueChange = {
                            createClientDialogNewName = it
                        },
                        label = { Text("New Nym Client Name") }
                    )
                    Text(
                        text = "Note that non-alphanumeric characters will be removed on confirm. If empty, default name \"client\" is used.",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                if (createClientDialogLoading) {
                    CircularProgressIndicator()
                } else {
                    TextButton(
                        onClick = {
                            createClientDialogLoading = true
                            clientInfoViewModel.addClient(
                                createClientDialogNewName.text.filter { c -> c.isDigit() || c.isLetter() }
                            ) { success ->
                                if (success) {
                                    createClientDialogOpen = false
                                    createClientDialogNewName = TextFieldValue("")
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Failed to create new client. Retrying should work.")
                                    }
                                }
                                createClientDialogLoading = false
                            }
                        }
                    ) {
                        Text("Confirm")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        createClientDialogOpen = false
                        createClientDialogNewName = TextFieldValue("")
                    }
                ) {
                    Text("Cancel")
                }
            })
    }

    if (deleteClientDialogOpen && clientInfoScreenUIState.selectClientName != null) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Delete Nym Client") },
            text = { Text(text = "Information related to the client \"${clientInfoScreenUIState.selectClientName}\" is not recoverable after this operation. Continue?") },
            confirmButton = {
                if (deleteClientDialogLoading) {
                    CircularProgressIndicator()
                } else {
                    TextButton(
                        onClick = {
                            deleteClientDialogLoading = true
                            clientInfoViewModel.deleteClient(clientInfoScreenUIState.selectClientName!!) {
                                deleteClientDialogOpen = false
                                deleteClientDialogLoading = false
                            }
                        },
                    ) {
                        Text("Confirm")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteClientDialogOpen = false }) {
                    Text("Cancel")
                }
            })
    }
}