package com.github.bea4dev.snowDawn.coroutine

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.shynixn.mccoroutine.bukkit.scope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.coroutines.CoroutineContext

object CoroutineFlagRegistry {
    val CAMPFIRE_PLACE = PlayerCoroutineFlag()
    val CAMPFIRE_CLICK = PlayerCoroutineFlag()
    val CLOSE_CRAFT_UI = PlayerCoroutineFlag()
}

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

    @Synchronized
    fun future(): Deferred<Unit> {
        return VanillaSourceAPI.getInstance().plugin.scope.async(this, CoroutineStart.DEFAULT) {}
    }

    @Synchronized
    override fun run() {
        super.run()
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