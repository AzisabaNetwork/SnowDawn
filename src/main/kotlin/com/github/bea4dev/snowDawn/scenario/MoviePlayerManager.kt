package com.github.bea4dev.snowDawn.scenario

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

object MoviePlayerManager {
    private val snapshotMap = ConcurrentHashMap<Player, PreviousSnapShot>()

    fun onStartPlaying(player: Player) {
        val snapshot = PreviousSnapShot(player.inventory.helmet, player.location)
        snapshotMap[player] = snapshot
    }

    fun onStopPlaying(player: Player) {
        val snapShot = snapshotMap.remove(player)
        if (snapShot != null) {
            player.inventory.helmet = snapShot.helmet
            player.teleport(snapShot.location)
        }
    }
}

private class PreviousSnapShot(
    val helmet: ItemStack?,
    val location: Location,
)