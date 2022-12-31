package com.kaeonx.nymandroidport.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Courtesy of https://stackoverflow.com/a/71693370
internal inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return f() as T
        }
    }