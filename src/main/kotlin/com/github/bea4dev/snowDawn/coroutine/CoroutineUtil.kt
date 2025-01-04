package com.github.bea4dev.snowDawn.coroutine

import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.camera.CameraHandler
import com.github.bea4dev.vanilla_source.api.entity.EngineEntity
import com.github.bea4dev.vanilla_source.api.entity.tick.TickThread
import com.github.bea4dev.vanilla_source.api.text.TextBox
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.scope
import kotlinx.coroutines.*
import org.bukkit.Bukkit

fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> {
    val dispatcher = ManualDispatcher()
    val future = VanillaSourceAPI.getInstance().plugin.scope.async(dispatcher, CoroutineStart.DEFAULT, block)
    Bukkit.getScheduler().runTaskAsynchronously(VanillaSourceAPI.getInstance().plugin, dispatcher)
    return future
}

fun <T> EngineEntity.sync(block: suspend CoroutineScope.() -> T): Deferred<T> {
    return this.tickThread.sync(block)
}

fun <T> TickThread.sync(block: suspend CoroutineScope.() -> T): Deferred<T> {
    val dispatcher = TickThreadDispatcher(this)
    return VanillaSourceAPI.getInstance().plugin.scope.async(dispatcher, CoroutineStart.DEFAULT, block)
}

fun TickThread.launch(block: suspend CoroutineScope.() -> Unit): Job {
    val dispatcher = TickThreadDispatcher(this)
    return VanillaSourceAPI.getInstance().plugin.scope.async(dispatcher, CoroutineStart.DEFAULT, block)
}

fun CameraHandler.play(): Deferred<Unit> {
    val dispatcher = ManualDispatcher()
    this.setEndCallBack(dispatcher)
    this.start()
    return VanillaSourceAPI.getInstance().plugin.scope.async(dispatcher, CoroutineStart.DEFAULT) {}
}

object MainThread {
    fun <T> sync(block: suspend CoroutineScope.() -> T): Deferred<T> {
        val dispatcher = ManualDispatcher()
        val future = VanillaSourceAPI.getInstance().plugin.scope.async(dispatcher, CoroutineStart.DEFAULT, block)
        Bukkit.getScheduler().runTask(VanillaSourceAPI.getInstance().plugin, dispatcher)
        return future
    }

    fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        return VanillaSourceAPI.getInstance().plugin.launch(block = block)
    }
}

fun TextBox.play(): Deferred<Unit> {
    val dispatcher = ManualDispatcher()
    this.setEndCallBack(dispatcher)
    this.show()
    return VanillaSourceAPI.getInstance().plugin.scope.async(dispatcher, CoroutineStart.DEFAULT) {}
}