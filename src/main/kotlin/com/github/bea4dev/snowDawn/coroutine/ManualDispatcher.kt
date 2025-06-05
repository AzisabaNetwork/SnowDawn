package com.github.bea4dev.snowDawn.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext

open class ManualDispatcher: CoroutineDispatcher(), Runnable {
    protected var block: Runnable? = null

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        this.block = block
    }

    override fun run() {
        block?.run()
    }
}