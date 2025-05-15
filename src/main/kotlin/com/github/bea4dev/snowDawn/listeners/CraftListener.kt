package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.snowDawn.craft.CraftGUI
import com.github.bea4dev.snowDawn.craft.CraftGUIManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class CraftListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        CraftGUIManager.onPlayerJoin(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        CraftGUIManager.onPlayerQuit(event.player)
    }

    @EventHandler
    fun onPlayerClickInventory(event: InventoryClickEvent) {
        CraftGUIManager.update(event.whoClicked as Player)
    }

    @EventHandler
    fun onClickInventory(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        CraftGUIManager.update(player)

        val holder = event.clickedInventory?.holder ?: return

        if (holder is CraftGUI) {
            event.isCancelled = true
            holder.onClick(event)
        }
    }

    @EventHandler
    fun onPickup(event: EntityPickupItemEvent) {
        val player = event.entity as? Player ?: return
        Bukkit.getScheduler().runTaskLater(SnowDawn.plugin, Runnable {
            CraftGUIManager.update(player)
        }, 2)
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        MainThread.launch { CraftGUIManager.update(event.player) }
    }
}