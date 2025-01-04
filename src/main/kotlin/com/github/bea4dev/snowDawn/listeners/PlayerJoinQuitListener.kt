package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.dimension.DimensionRegistry
import com.github.bea4dev.snowDawn.generator.GeneratorRegistry
import com.github.bea4dev.snowDawn.generator.SnowLand
import com.github.bea4dev.snowDawn.generator.VoidGenerator
import com.github.bea4dev.snowDawn.scenario.script.Prologue
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.WorldCreator
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.util.Vector
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

private val LOGIN_POSITION = Vector(0.5, 0.0, 0.5)

class PlayerJoinQuitListener: Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        player.teleport(LOGIN_POSITION.toLocation(WorldRegistry.VOID))
        //player.teleport(Location(world, 0.5, 240.0, 0.5))

        Prologue.start(player)
    }

}