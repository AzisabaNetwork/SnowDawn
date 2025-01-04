package com.github.bea4dev.snowDawn.coroutine

import com.github.bea4dev.vanilla_source.api.entity.tick.TickThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

class TickThreadDispatcher(private val thread: TickThread): CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        thread.scheduleTask(block)
    }

    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return thread.currentThread!! != Thread.currentThread()
    }
}