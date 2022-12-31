package com.kaeonx.nymandroidport.ui.screens.contacts

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaeonx.nymandroidport.LocalNymRepository
import com.kaeonx.nymandroidport.LocalSnackbarHostState
import com.kaeonx.nymandroidport.utils.viewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(nymIdSelected: (String) -> Unit) {
    // TODO: This feels odd... ContactsScreen should not be able to talk to repository directly.
    val nymRepository = LocalNymRepository.current
    val applicationContext = LocalContext.current.applicationContext as Application
    // Returns an existing ViewModel or creates a new one
    val contactsViewModel: ContactsViewModel = viewModel(factory = viewModelFactory {
        ContactsViewModel(nymRepository, applicationContext)
    })

    // For AlertDialogs
    var addContactDialogOpen by remember { mutableStateOf(false) }
    var addContactDialogLoading by remember { mutableStateOf(false) }
    var addContactDialogAddress by remember { mutableStateOf(TextFieldValue("")) }

    // For snackbar
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    val contacts by contactsViewModel.contacts.collectAsState()
    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(
                items = contacts,
                key = { contact -> contact.contactAddress }
            ) { contact ->
                Text(text = contact.contactAddress)
            }
        }
        Button(onClick = { addContactDialogOpen = true }) {
            Text(text = "Add Nym contact")
        }
        Button(onClick = { nymIdSelected("1234") }) {
            Text(text = "Open chat for 1234")
        }
    }

    if (addContactDialogOpen) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Create Nym Client") },
            text = {
                Column {
                    TextField(
                        value = addContactDialogAddress,
                        onValueChange = {
                            addContactDialogAddress = it
                        },
                        label = { Text("New contact's Nym address") }
                    )
                    Text(
                        text = "user-identity-key.user-encryption-key@gateway-identity-key",
                        modifier = Modifier.padding(top = 8.dp)
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