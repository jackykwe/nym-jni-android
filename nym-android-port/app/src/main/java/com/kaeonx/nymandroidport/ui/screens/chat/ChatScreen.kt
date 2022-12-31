package com.kaeonx.nymandroidport.ui.screens.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle

@Composable
fun ChatScreen(contactAddress: String) {
    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        Text(
            text = "Conversation between you:",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "<my address>",
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
    }
}