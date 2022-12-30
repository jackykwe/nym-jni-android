package com.kaeonx.nymandroidport.ui.screens.contacts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ContactsScreen(nymIdSelected: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        Text(text = "Hello, contacts screen")
        Button(onClick = { nymIdSelected("1234") }) {
            Text(text = "Open chat for 1234")
        }
    }
}