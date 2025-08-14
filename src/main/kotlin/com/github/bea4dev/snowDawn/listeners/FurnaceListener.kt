package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.item.getItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.FurnaceBurnEvent
import org.bukkit.event.player.PlayerExpChangeEvent

class FurnaceListener : Listener {
    @EventHandler
    fun onBurn(event: FurnaceBurnEvent) {
        val fuel = event.fuel.getItem() ?: return

        if (fuel != ItemRegistry.FUEL) {
            return
        }

        event.burnTime = 100 * 20
    }

    @EventHandler
    fun onGetExp(event: PlayerExpChangeEvent) {
        event.amount = 0
    }
}