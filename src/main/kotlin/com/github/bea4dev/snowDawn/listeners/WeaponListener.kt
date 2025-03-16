package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.item.weapon.getWeapon
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent

class WeaponListener : Listener {
    @EventHandler
    fun onPlayerSwingWeapon(event: PlayerAnimationEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand

        val weapon = item.getWeapon() ?: return
        weapon.onSwing(player, item)
    }
}