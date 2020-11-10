package com.thunderdogge.qread.extensions

fun <T> lazily(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)
