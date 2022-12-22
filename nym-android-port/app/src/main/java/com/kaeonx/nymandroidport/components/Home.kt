package com.kaeonx.nymandroidport.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun NymAndroidPortHome() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Nym Android Port") }
            )
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            Text(text = "Just migrated to Compose!")
        }
    }
}