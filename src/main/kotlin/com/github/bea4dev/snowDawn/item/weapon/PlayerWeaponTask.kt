package com.github.bea4dev.snowDawn.item.weapon

import de.tr7zw.changeme.nbtapi.NBT
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

private val map = ConcurrentHashMap<Player, PlayerWeaponTask>()

object WeaponTaskManager {
    operator fun get(player: Player): PlayerWeaponTask? {
        return map[player]
    }
}

class PlayerWeaponTask(private val player: Player) : BukkitRunnable() {
    init {
        map[player] = this
    }

    var enableBar = true

    override fun run() {
        if (!player.isOnline) {
            super.cancel()
            map.remove(player)
            return
        }

        if (player.gameMode != GameMode.SURVIVAL) {
            return
        }

        val mainHandItem = player.inventory.itemInMainHand

        if (mainHandItem.isEmpty && enableBar) {
            player.resetTitle()
            return
        }

        val maxAttackTick = NBT.get(
            mainHandItem,
            Function { nbt -> nbt.getOrNull<Int>(WeaponTag.MAX_ATTACK_TICK.key, Int::class.java) })

        if (maxAttackTick == null) {
            if (enableBar) {
                player.resetTitle()
            }
            return
        }

        var attackTick =
            NBT.get(mainHandItem, Function { nbt -> nbt.getOrDefault(WeaponTag.ATTACK_TICK.key, maxAttackTick) })

        if (attackTick < maxAttackTick) {
            attackTick++
        }

        ProgressBar(player).setProgress(attackTick.toDouble() / maxAttackTick.toDouble())

        NBT.modify(mainHandItem) { nbt -> nbt.setInteger(WeaponTag.ATTACK_TICK.key, attackTick) }
    }
}