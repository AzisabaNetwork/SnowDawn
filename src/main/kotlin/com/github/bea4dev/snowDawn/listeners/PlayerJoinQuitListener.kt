package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.entity.mob.Phage
import com.github.bea4dev.snowDawn.item.weapon.PlayerWeaponTask
import com.github.bea4dev.snowDawn.item.weapon.Weapon
import com.github.bea4dev.snowDawn.world.WorldRegistry
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.util.Vector

private val LOGIN_POSITION = Vector(0.5, 0.0, 0.5)

internal class PlayerJoinQuitListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        player.gameMode = GameMode.CREATIVE

        player.teleport(Location(WorldRegistry.ASSET, 0.5, 1.0, 0.5))

        val weapon = Weapon("pipe0", Material.SHEARS, 1, 10, 5.0, 4.0F)
        player.inventory.setItemInMainHand(weapon.createItemStack())

        PlayerWeaponTask(player).runTaskTimer(SnowDawn.plugin, 0, 1)
        /*
        PlayerTask(player).start()

        player.teleport(LOGIN_POSITION.toLocation(WorldRegistry.VOID))
        //player.teleport(Location(world, 0.5, 240.0, 0.5))

        Prologue.start(player)*/
    }

    @EventHandler
    fun onPlayerClick(event: PlayerAnimationEvent) {
        val player = event.player

        if (player.isSneaking) {
            val phage = Phage(player.location, 20.0F, 5.0F)
            phage.block = Material.DEEPSLATE
            phage.spawn()
        }
    }

}