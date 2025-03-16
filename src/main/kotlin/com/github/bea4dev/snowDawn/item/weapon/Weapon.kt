package com.github.bea4dev.snowDawn.item.weapon

import com.github.bea4dev.snowDawn.entity.mob.Phage
import com.github.bea4dev.snowDawn.item.Item
import com.github.bea4dev.snowDawn.item.getItem
import com.github.bea4dev.snowDawn.player.PlayerManager
import com.github.bea4dev.vanilla_source.api.entity.EngineEntity
import com.github.bea4dev.vanilla_source.api.util.collision.CollideOption
import com.github.bea4dev.vanilla_source.api.world.cache.AsyncWorldCache
import de.tr7zw.changeme.nbtapi.NBT
import net.kyori.adventure.sound.Sound
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Function
import kotlin.random.Random

class Weapon(
    id: String,
    material: Material,
    customModelData: Int,
    private val maxAttackTick: Int,
    private val parryWaveRange: Double
) : Item(
    id, material, customModelData
) {
    override fun createItemStack(): ItemStack {
        return super.createItemStack()
            .also { item -> NBT.modify(item) { nbt -> nbt.setInteger(WeaponTag.MAX_ATTACK_TICK.key, maxAttackTick) } }
    }

    fun onSwing(player: Player, item: ItemStack) {
        val attackTick = NBT.get(item, Function { nbt -> nbt.getOrDefault(WeaponTag.ATTACK_TICK.key, maxAttackTick) })

        if (attackTick == 0) {
            return
        }

        if (attackTick == maxAttackTick) {
            player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_ATTACK_SWEEP, Sound.Source.PLAYER, 1.0F, 0.8F))
        }

        NBT.modify(item) { nbt -> nbt.setInteger(WeaponTag.ATTACK_TICK.key, 0) }

        val location = player.eyeLocation
        val world = AsyncWorldCache.getAsyncWorld(location.world.name)

        var entity: EngineEntity? = null
        for (phage in world.getNearbyEntities(location.x, location.y, location.z, 1.0)) {
            if (phage is Phage && phage.tryParry()) {
                entity = phage
            }
        }

        if (entity == null) {
            val result =
                world.rayTrace(
                    location.toVector(),
                    location.direction,
                    3.0,
                    CollideOption(FluidCollisionMode.NEVER, true)
                ) ?: return

            entity = result.hitEntity ?: return
        }

        if (entity !is Phage || !entity.tryParry()) {
            return
        }

        val hitPosition = entity.location.toVector()

        PlayerManager.ONLINE_PLAYERS.forEach {
            it.playSound(
                Sound.sound(
                    org.bukkit.Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,
                    Sound.Source.PLAYER,
                    1.0F,
                    1.2F
                ),
                location.x,
                location.y,
                location.z,
            )
        }

        for (nearEntity in world.getNearbyEntities(hitPosition.x, hitPosition.y, hitPosition.z, parryWaveRange)) {
            if (nearEntity is Phage) {
                nearEntity.parryBy(player)
            }
        }

        player.velocity = location.direction.multiply(-0.8)

        // effect
        for (i in 0..<10) {
            val size = 2.5
            val x = Random.nextDouble(size) - (size / 2.0)
            val y = Random.nextDouble(size) - (size / 2.0)
            val z = Random.nextDouble(size) - (size / 2.0)
            PlayerManager.ONLINE_PLAYERS.forEach {
                it.spawnParticle(Particle.CRIT, hitPosition.x, hitPosition.y, hitPosition.z, 0, x, y, z, 1.5)
            }
        }
        PlayerManager.ONLINE_PLAYERS.forEach {
            it.spawnParticle(
                Particle.FLASH,
                hitPosition.x,
                hitPosition.y,
                hitPosition.z,
                0,
                0.0,
                0.0,
                0.0,
                0.5
            )
        }
    }
}

fun ItemStack.getWeapon(): Weapon? {
    val item = getItem() ?: return null

    val hasMaxAttackTick = NBT.get(this, Function { nbt -> nbt.hasTag(WeaponTag.MAX_ATTACK_TICK.key) })
    if (!hasMaxAttackTick) {
        return null
    }

    return item as Weapon
}