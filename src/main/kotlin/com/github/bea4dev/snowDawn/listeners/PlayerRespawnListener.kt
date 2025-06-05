package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.save.PlayerDataRegistry
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerRespawnListener : Listener {
    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        event.respawnLocation = PlayerDataRegistry[player].respawnLocation
    }
}