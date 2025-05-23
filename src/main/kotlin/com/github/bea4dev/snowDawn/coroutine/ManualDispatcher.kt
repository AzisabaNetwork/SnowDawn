package com.github.bea4dev.snowDawn.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext

class ManualDispatcher: CoroutineDispatcher(), Runnable {
    private lateinit var block: Runnable

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        this.block = block
    }

    override fun run() {
        block.run()
    }
}