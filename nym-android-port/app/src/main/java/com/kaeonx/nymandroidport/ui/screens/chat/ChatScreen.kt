package com.kaeonx.nymandroidport.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaeonx.nymandroidport.LocalSnackbarHostState
import com.kaeonx.nymandroidport.database.Message
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    contactAddress: String,
    onContactDeleted: () -> Unit,
    chatViewModel: ChatViewModel = viewModel()
) {
    chatViewModel.initContactAddress(contactAddress)

    // For text field
    var newMessageContent by remember { mutableStateOf(TextFieldValue("")) }
    var newMessageLoading by remember { mutableStateOf(false) }

    // For AlertDialogs
    var deleteContactDialogOpen by remember { mutableStateOf(false) }
    var deleteContactDialogLoading by remember { mutableStateOf(false) }

    // For managing keyboard focus
    val localFocusManager = LocalFocusManager.current

    // For snackbar
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    val selectedClientAddress by chatViewModel.selectedClientAddress.collectAsState()
    val messages by chatViewModel.messages.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            }
    ) {
        if (selectedClientAddress == null) {
            Text(
                text = "Please run a client on the Clients page first.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            Button(
                onClick = { deleteContactDialogOpen = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Delete this contact and conversation",
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "Conversation between you:",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = selectedClientAddress!!,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "and your contact:",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = contactAddress,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = messages,
                    key = { message -> message.id }
                ) { message ->
                    if (message.fromAddress == selectedClientAddress) {
                        MeMessageCard(message = message)
                    } else {
                        ContactMessageCard(message = message)
                    }
                }
            }

//            Button(
//                onClick = {
//                    chatViewModel.debugGenerateMessage(contactAddress)
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(text = "Generate message from other party")
//            }

            OutlinedTextField(
                value = newMessageContent,
                onValueChange = { newMessageContent = it },
                modifier = Modifier.fillMaxWidth(),
                enabled = !newMessageLoading,
                trailingIcon = {
                    if (newMessageLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                    } else {
                        IconButton(
                            onClick = {
                                newMessageLoading = true
                                localFocusManager.clearFocus()
                                chatViewModel.sendMessage(
                                    toAddress = contactAddress,
                                    message = newMessageContent.text
                                ) { success ->
                                    if (success) {
                                        newMessageContent = TextFieldValue("")
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Failed to send message.")
                                        }
                                    }
                                    newMessageLoading = false
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null
                            )
                        }
                    }

                },
                maxLines = 3
            )
        }
    }

    if (deleteContactDialogOpen) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Delete Contact and Conversation") },
            text = { Text(text = "All messages sent to and received from the contact (\"$contactAddress\") is not recoverable after this operation. Continue?") },
            confirmButton = {
                if (deleteContactDialogLoading) {
                    CircularProgressIndicator()
                } else {
                    TextButton(
                        onClick = {
                            deleteContactDialogLoading = true
                            chatViewModel.deleteContact(contactAddress) {
                                deleteContactDialogOpen = false
                                deleteContactDialogLoading = false
                                onContactDeleted()
                            }
                        },
                    ) {
                        Text("Confirm")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteContactDialogOpen = false }) {
                    Text("Cancel")
                }
            })
    }
}

@Composable
fun ContactMessageCard(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.tertiary,
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .padding(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .border(1.5.dp, MaterialTheme.colorScheme.tertiary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message.fromAddress.substring(0 until 1),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontFamily = FontFamily.Monospace
            )
        }
        Text(
            text = message.message,
            modifier = Modifier
                .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
                .weight(1f),
        )
    }
}

@Composable
fun MeMessageCard(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message.message,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                .weight(1f),
        )
        Box(
            modifier = Modifier
                .size(56.dp)
                .padding(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!message.sent) {
                CircularProgressIndicator()
            }
            Text(
                text = "me",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}