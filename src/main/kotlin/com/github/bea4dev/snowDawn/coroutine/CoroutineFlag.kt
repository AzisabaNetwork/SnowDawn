package com.github.bea4dev.snowDawn.coroutine

import com.github.bea4dev.snowDawn.SnowDawn
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.coroutines.CoroutineContext

class CoroutineFlag(private var isCompleted: Boolean) : ManualDispatcher() {
    @Synchronized
    fun onComplete() {
        if (!this.isCompleted) {
            Bukkit.getScheduler().runTask(SnowDawn.plugin, this)
            this.isCompleted = true
        }
    }

    @Synchronized
    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return !this.isCompleted
    }
}

class PlayerCoroutineFlag {
    private val map = mutableMapOf<Player, CoroutineFlag>()

    @Synchronized
    fun onComplete(player: Player) {
        map.computeIfAbsent(player) { CoroutineFlag(true) }.onComplete()
    }

    @Synchronized
    operator fun get(player: Player): CoroutineFlag {
        return map.computeIfAbsent(player) { CoroutineFlag(false) }
    }
}