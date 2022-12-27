package com.kaeonx.nymandroidport.ui.screens.clientinfo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaeonx.nymandroidport.LocalSnackbarHostState
import kotlinx.coroutines.launch

private const val NONE_OPTION = "<none>"
private const val ADD_NEW_OPTION = "<add new...>"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientInfoScreen(clientInfoViewModel: ClientInfoViewModel = viewModel()) {
    val options = clientInfoViewModel.clients.toMutableList().apply {
        add(0, NONE_OPTION)
        add(ADD_NEW_OPTION)
    }

    // For ExposedDropdownMenuBox
    var clientSelectionExpanded by remember { mutableStateOf(false) }
//    var selectedOption by remember { mutableStateOf(options[0]) }
    val selectedOption by clientInfoViewModel.selectedClient.collectAsState()

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
                value = selectedOption ?: NONE_OPTION,
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
                options.forEach { option ->
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
            if (selectedOption != null) {
                val workInfo by clientInfoViewModel.nymRunWorkInfo.observeAsState()
                Column {
                    workInfo?.forEach {
                        Text(
                            text = "\"$selectedOption\" state: ${it.state} (${
                                it.progress.getInt(
                                    "PROGRESS",
                                    -1
                                )
                            })"
                        )
                    }
                }
            }
        }

        if (selectedOption != null) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { clientInfoViewModel.stopRunningClient() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text(
                    text = "Stop nym Client \"$selectedOption\"",
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { deleteClientDialogOpen = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Delete Nym Client \"$selectedOption\"",
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    if (createClientDialogOpen) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Create Nym Client") },
            text = {
                Column {
                    TextField(value = createClientDialogNewName, onValueChange = {
//                        val beforeSelection = it.getTextBeforeSelection(it.text.length).filter { c -> c.isDigit() || c.isLetter() }.toString()
//                        val afterSelection = it.getTextAfterSelection(it.text.length).filter { c -> c.isDigit() || c.isLetter() }.toString()
//                        createClientDialogNewName = TextFieldValue(
//                            text = StringBuilder().append(beforeSelection).append(afterSelection).toString(),
//                            selection = TextRange(beforeSelection.length, beforeSelection.length),
//                            composition = // STUCK
//                        )
                        createClientDialogNewName = it
                    }, label = { Text("New Nym Client Name") })
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

    if (deleteClientDialogOpen && selectedOption != null) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Delete Nym Client") },
            text = { Text(text = "Information related to the client \"$selectedOption\" is not recoverable after this operation. Continue?") },
            confirmButton = {
                if (deleteClientDialogLoading) {
                    CircularProgressIndicator()
                } else {
                    TextButton(
                        onClick = {
                            deleteClientDialogLoading = true
                            clientInfoViewModel.deleteClient(selectedOption!!) {
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