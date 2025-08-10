package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.entity.mob.Phage
import com.github.bea4dev.snowDawn.item.weapon.getWeapon
import com.github.bea4dev.vanilla_source.api.util.collision.CollideOption
import com.github.bea4dev.vanilla_source.api.world.cache.AsyncWorldCache
import org.bukkit.FluidCollisionMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent

class WeaponListener : Listener {
    @EventHandler
    fun onPlayerSwingWeapon(event: PlayerAnimationEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand

        val weapon = item.getWeapon()
        if (weapon == null) {
            val location = player.eyeLocation
            val world = AsyncWorldCache.getAsyncWorld(location.world.name)

            val result = world.rayTrace(
                location.toVector(),
                location.direction,
                3.0,
                CollideOption(FluidCollisionMode.NEVER, true)
            )
            if (result != null) {
                val hitEntity = result.hitEntity
                if (hitEntity != null && hitEntity is Phage) {
                    hitEntity.damage(player, 1.0F, false)
                }
            }
        } else {
            weapon.onSwing(player, item)
        }
    }
}