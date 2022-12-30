package com.kaeonx.nymandroidport.ui.screens.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ChatScreen(nymId: String) {
    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        Text(text = "Hello, chat screen for $nymId")
    }
}