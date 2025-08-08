package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.item.compass.Compass
import com.github.bea4dev.snowDawn.item.getItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class PlayerCompassListener : Listener {
    @EventHandler
    fun onPLayerClick(event: PlayerInteractEvent) {
        val player = event.player

        if (event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.LEFT_CLICK_AIR) {
            return
        }

        val item = player.inventory.itemInMainHand
        val compass = item.getItem() as? Compass ?: return

        compass.onRightClick(player)
    }
}