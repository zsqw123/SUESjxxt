package com.jxxt.sues

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun LifecycleOwner.doAsync(event: () -> Unit) {
    lifecycleScope.launch(Dispatchers.IO) { event() }
}