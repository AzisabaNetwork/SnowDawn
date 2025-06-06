package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.item.ItemRegistry
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

class DropItemListener : Listener {
    @EventHandler
    fun onPickupItem(event: EntityPickupItemEvent) {
        if (event.entity !is Player) {
            return
        }

        val item = when (event.item.itemStack.type) {
            Material.TORCH -> ItemRegistry.TORCH
            Material.COAL -> ItemRegistry.COAL
            Material.COBBLESTONE -> ItemRegistry.STONE
            else -> return
        }

        event.item.itemStack = item.createItemStack().also { item -> item.amount = event.item.itemStack.amount }
    }
}