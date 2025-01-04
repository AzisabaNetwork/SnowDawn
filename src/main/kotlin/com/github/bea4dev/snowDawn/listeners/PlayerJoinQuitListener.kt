package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.player.PlayerTask
import com.github.bea4dev.snowDawn.scenario.script.Prologue
import com.github.bea4dev.snowDawn.world.WorldRegistry
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.util.Vector

private val LOGIN_POSITION = Vector(0.5, 0.0, 0.5)

class PlayerJoinQuitListener: Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        player.gameMode = GameMode.ADVENTURE

        PlayerTask(player).start()

        player.teleport(LOGIN_POSITION.toLocation(WorldRegistry.VOID))
        //player.teleport(Location(world, 0.5, 240.0, 0.5))

        Prologue.start(player)
    }

}