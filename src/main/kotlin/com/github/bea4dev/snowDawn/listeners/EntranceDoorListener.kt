package com.github.bea4dev.snowDawn.listeners

import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.block.BlockPlaceEvent

class EntranceDoorListener : Listener {
    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        onBlockBreakPlace(event)
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        onBlockBreakPlace(event)
    }

    private fun <T> onBlockBreakPlace(event: T) where T: BlockEvent, T: Cancellable {
        val block = event.block

        // 周囲に岩盤のある黒いコンクリートなら破壊及び設置を阻止
        if (block.type == Material.BLACK_CONCRETE && block.getRelative(BlockFace.NORTH).type == Material.BEDROCK) {
            event.isCancelled = true
        }
    }
}