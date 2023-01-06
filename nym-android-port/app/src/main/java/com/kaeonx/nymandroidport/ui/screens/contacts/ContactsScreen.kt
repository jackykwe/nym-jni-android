package com.kaeonx.nymandroidport.ui.screens.contacts

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaeonx.nymandroidport.LocalSnackbarHostState
import com.kaeonx.nymandroidport.utils.NymAddress
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    nymAddressSelected: (String) -> Unit,
    contactsViewModel: ContactsViewModel = viewModel()
) {
    val contactsScreenUIState by contactsViewModel.contactsScreenUIState.collectAsState()

    // For AlertDialogs
    var addContactDialogOpen by remember { mutableStateOf(false) }
    var addContactDialogLoading by remember { mutableStateOf(false) }
    var addContactDialogAddress by remember { mutableStateOf(TextFieldValue("")) }

    // For snackbar
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        if (contactsScreenUIState.selectedClientId == null) {
            Text(
                text = "Please run a client on the Clients page first.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            Text(text = "Contacts of \"${contactsScreenUIState.selectedClientId}\":")
            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(1f)
            ) {
                items(
                    items = contactsScreenUIState.contactAddresses,
                    key = { contactAddress -> contactAddress }
                ) { contactAddress ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray)
                            .clickable { nymAddressSelected(contactAddress) }
                    ) {
                        val nymAddress = NymAddress.from(contactAddress)
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = nymAddress.userIdentityKey,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = ". ${nymAddress.userEncryptionKey}",
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "@ ${nymAddress.gatewayIdentityKey}",
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                }
            }
            Button(
                onClick = { addContactDialogOpen = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add Nym contact")
            }
        }
    }

    if (addContactDialogOpen) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Add Nym Contact") },
            text = {
                Column {
                    TextField(
                        value = addContactDialogAddress,
                        onValueChange = {
                            addContactDialogAddress = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("New contact's Nym address") }
                    )
                    Text(
                        text = "Format:",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "<user-identity-key>.<user-encryption-key>@<gateway-identity-key>",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            },
            confirmButton = {
                if (addContactDialogLoading) {
                    CircularProgressIndicator()
                } else {
                    TextButton(
                        onClick = {
                            addContactDialogLoading = true
                            contactsViewModel.addContact(addContactDialogAddress.text.trim()) { success ->
                                if (success) {
                                    addContactDialogOpen = false
                                    addContactDialogAddress = TextFieldValue("")
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Failed to add new contact.")
                                    }
                                }
                                addContactDialogLoading = false
                            }
                        },
                        enabled = addContactDialogAddress.text.matches(Regex("[a-zA-Z0-9]+\\.[a-zA-Z0-9]+@[a-zA-Z0-9]+"))
                    ) {
                        Text("Confirm")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        addContactDialogOpen = false
                        addContactDialogAddress = TextFieldValue("")
                    }
                ) {
                    Text("Cancel")
                }
            })
    }
}