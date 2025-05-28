package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.item.ItemRegistry
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFadeEvent

class BlockListener : Listener {
    @EventHandler
    fun onIceMelt(event: BlockFadeEvent) {
        if (event.block.type != Material.ICE) return
        event.isCancelled = true
    }

    @EventHandler
    fun onIceBreak(event: BlockBreakEvent) {
        if (event.block.type != Material.ICE || event.player.gameMode != GameMode.SURVIVAL) return
        event.isCancelled = true
        event.block.type = Material.AIR

        val world = event.block.world
        world.dropItem(event.block.location.add(0.5, 0.5, 0.5), ItemRegistry.ICE.createItemStack())
    }
}