package com.github.bea4dev.snowDawn.player

import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.entity.TickBase
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.data.type.Campfire
import org.bukkit.entity.Player
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val TEMPERATURE_CHECK_RADIUS = 16
private const val MIN_TEMPERATURE = -140
private const val MAX_TEMPERATURE = 100

private val thread = VanillaSourceAPI.getInstance().tickThreadPool.nextTickThread

class PlayerTask(private val player: Player) : TickBase {
    private var tick = 0
    private var temperature = MAX_TEMPERATURE

    override fun tick() {
        tick++

        if (player.gameMode != GameMode.SURVIVAL) {
            player.freezeTicks = 0
            return
        }

        val location = player.location

        val world = thread.threadLocalCache.getGlobalWorld(location.world.name)

        var temperature = -1
        val hasBlockLight = world.getBlockLightLevel(location.blockX, location.blockY, location.blockZ) > 0
                || world.getBlockLightLevel(location.blockX, location.blockY + 1, location.blockZ) > 0

        if (hasBlockLight) {
            loop@ for (x in (location.blockX - TEMPERATURE_CHECK_RADIUS)..(location.blockX + TEMPERATURE_CHECK_RADIUS)) {
                for (y in (location.blockY - TEMPERATURE_CHECK_RADIUS)..(location.blockY + TEMPERATURE_CHECK_RADIUS)) {
                    for (z in (location.blockZ - TEMPERATURE_CHECK_RADIUS)..(location.blockZ + TEMPERATURE_CHECK_RADIUS)) {
                        val block = world.getBlockData(x, y, z)
                        if (block != null) {
                            if (block.material == Material.CAMPFIRE) {
                                if ((block as Campfire).isLit) {
                                    temperature = 1
                                    break@loop
                                }
                            }

                            if (temperature < 0 && (block.material == Material.TORCH || block.material == Material.WALL_TORCH)) {
                                temperature = 1
                            }
                        }
                    }
                }
            }
        }

        this.temperature += temperature

        if (this.temperature > MAX_TEMPERATURE) {
            this.temperature = MAX_TEMPERATURE
        }
        if (this.temperature < MIN_TEMPERATURE) {
            this.temperature = MIN_TEMPERATURE
        }

        MainThread.launch {
            player.lockFreezeTicks(true)
            if (this@PlayerTask.temperature <= 0) {
                player.freezeTicks = abs(this@PlayerTask.temperature)
            }
        }
    }

    override fun shouldRemove(): Boolean {
        return !player.isOnline
    }

    fun start() {
        thread.addEntity(this)
    }
}