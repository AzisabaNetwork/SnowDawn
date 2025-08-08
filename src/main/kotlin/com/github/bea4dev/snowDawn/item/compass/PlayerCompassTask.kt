package com.github.bea4dev.snowDawn.item.compass

import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

private val map = mutableMapOf<Player, PlayerCompassTask>()

object PlayerCompassTaskManager {
    operator fun get(player: Player): PlayerCompassTask? {
        return map[player]
    }
}

class PlayerCompassTask(private val player: Player) : BukkitRunnable() {
    init {
        map[player] = this
    }

    var delayTick = 0

    override fun run() {
        if (!player.isOnline) {
            cancel()
            return
        }

        if (delayTick == 0) {
            return
        }

        delayTick--
    }
}