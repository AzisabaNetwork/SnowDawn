package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.item.getItem
import com.github.bea4dev.snowDawn.text.StoryMemoText
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack

class ChestOpenListener : Listener {
    @EventHandler
    fun onChestOpen(event: InventoryOpenEvent) {
        val player = event.player
        val inventory = event.inventory

        for (index in inventory.contents.indices) {
            val item = inventory.getItem(index)

            if (item == null) {
                continue
            }

            val itemInstance = item.getItem() ?: continue

            if (itemInstance != ItemRegistry.STORY_MEMO) {
                continue
            }

            // replace empty memo
            val lines = StoryMemoText.getNext(player.world)

            if (lines == null) {
                inventory.setItem(index, null)
                continue
            }

            val title = lines.getOrNull(0) ?: Component.empty()
            val lore = lines.drop(1)

            val newItem = ItemStack(Material.PAPER, 1)
            val newMeta = newItem.itemMeta
            newMeta.displayName(title)
            newMeta.lore(lore)
            newItem.itemMeta = newMeta

            inventory.setItem(index, newItem)
        }
    }
}