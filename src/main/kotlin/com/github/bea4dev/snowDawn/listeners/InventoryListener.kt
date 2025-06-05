package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.coroutine.CoroutineFlagRegistry
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import java.util.concurrent.ConcurrentHashMap

class InventoryListener : Listener {
    companion object {
        val closeCheck = ConcurrentHashMap.newKeySet<Player>()!!
    }

    @EventHandler
    fun onCloseInventory(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        if (!closeCheck.contains(player)) {
            return
        }

        CoroutineFlagRegistry.CLOSE_CRAFT_UI[player].onComplete()
    }
}