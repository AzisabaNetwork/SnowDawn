package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.coroutine.PlayerCoroutineFlag
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.text.Text
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.player.PlayerInteractEvent

val CAMPFIRE_CLICK = PlayerCoroutineFlag()

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

    @EventHandler
    fun onClickCampfire(event: PlayerInteractEvent) {
        val player = event.player

        if (event.action == Action.RIGHT_CLICK_BLOCK && event.clickedBlock?.type == Material.CAMPFIRE) {
            player.respawnLocation = player.location
            player.sendMessage(Component.translatable(Text.MESSAGE_SET_RESPAWN.toString()))

            CAMPFIRE_CLICK.onComplete(player)
        }
    }
}