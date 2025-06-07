package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.coroutine.CoroutineFlagRegistry
import com.github.bea4dev.snowDawn.coroutine.PlayerCoroutineFlag
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.save.PlayerDataRegistry
import com.github.bea4dev.snowDawn.text.Text
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

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
    fun onCopperBreak(event: BlockBreakEvent) {
        if (event.block.type != Material.COPPER_ORE || event.player.gameMode != GameMode.SURVIVAL) return
        event.isDropItems = false

        val world = event.block.world
        world.dropItem(event.block.location.add(0.5, 0.5, 0.5), ItemStack(Material.RAW_COPPER))
    }

    @EventHandler
    fun onClickCampfire(event: PlayerInteractEvent) {
        val player = event.player

        if (event.action == Action.RIGHT_CLICK_BLOCK && event.clickedBlock?.type == Material.CAMPFIRE && event.hand == EquipmentSlot.HAND) {
            player.sendMessage(Component.translatable(Text.MESSAGE_SET_RESPAWN.toString()))
            PlayerDataRegistry[player].respawnLocation = player.location.clone()

            CoroutineFlagRegistry.CAMPFIRE_CLICK.onComplete(player)
        }
    }

    @EventHandler
    fun onPlaceCampfire(event: BlockPlaceEvent) {
        val player = event.player
        if (event.block.type == Material.CAMPFIRE) {
            CoroutineFlagRegistry.CAMPFIRE_PLACE.onComplete(player)
        }
    }
}