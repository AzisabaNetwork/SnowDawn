package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.craft.CraftGUI
import com.github.bea4dev.snowDawn.item.Item
import com.github.bea4dev.snowDawn.item.ItemRegistry
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent

class ItemReplaceListener : Listener {
    @EventHandler
    fun onPickupItem(event: EntityPickupItemEvent) {
        if (event.entity !is Player) {
            return
        }
        val item = replace(event.item.itemStack.type) ?: return
        event.item.itemStack = item.createItemStack().also { item -> item.amount = event.item.itemStack.amount }
    }

    @EventHandler
    fun onItemClick(event: InventoryClickEvent) {
        if (event.clickedInventory?.holder is CraftGUI) {
            return
        }
        val item = event.currentItem ?: return
        val newItem = replace(item.type) ?: return
        event.currentItem = newItem.createItemStack().also { newItem -> newItem.amount = item.amount }
    }

    private fun replace(material: Material): Item? {
        return when (material) {
            Material.TORCH -> ItemRegistry.TORCH
            Material.COAL -> ItemRegistry.COAL
            Material.COBBLESTONE -> ItemRegistry.STONE
            Material.FURNACE -> ItemRegistry.FURNACE
            Material.COPPER_INGOT -> ItemRegistry.COPPER_INGOT
            else -> null
        }
    }
}