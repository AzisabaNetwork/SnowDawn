package com.github.bea4dev.snowDawn.player

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.snowDawn.save.PlayerDataRegistry
import com.github.bea4dev.snowDawn.scenario.script.EnterMegaStructure
import com.github.bea4dev.snowDawn.scenario.script.Sisetu
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.entity.TickBase
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Campfire
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.abs

private const val TEMPERATURE_CHECK_RADIUS = 16
private const val MIN_TEMPERATURE = -140
private const val MAX_TEMPERATURE = 100

private val thread = VanillaSourceAPI.getInstance().tickThreadPool.nextTickThread

class PlayerTask(private val player: Player) : TickBase {
    private val playerData = PlayerDataRegistry[player]
    private var tick = 0
    private var temperature = MAX_TEMPERATURE
    private var startedPlayingSisetuMovie = false

    override fun tick() {
        tick++

        freezeTick()

        sisetuMovieTick()

        entranceTick()

        updatePlayerInventory()

        lastLocationUpdate()
    }

    private fun freezeTick() {
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
                                    temperature = +1
                                    break@loop
                                }
                            }

                            if (temperature < 0 && (block.material == Material.TORCH || block.material == Material.WALL_TORCH || block.material == Material.COPPER_BULB)) {
                                temperature = +1
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

        if (temperature < 0 && tick % 50 < 25) {
            player.spawnParticle(
                Particle.SNOWFLAKE,
                player.eyeLocation.add(player.eyeLocation.direction.multiply(0.5)).add(0.0, -0.2, 0.0),
                1,
                0.01,
                0.01,
                0.01,
                0.01,
            )
        }
    }

    private fun sisetuMovieTick() {
        // 施設に近づいたときにムービーを再生する
        val location = player.location

        val sisetuPosition = Vector(1200, 255, 0)

        // 半径20ブロック以内
        if (location.toVector().distance(sisetuPosition) < 20 && player.world == WorldRegistry.SNOW_LAND) {
            if (!playerData.finishedSisetuMovie && !startedPlayingSisetuMovie) {
                startedPlayingSisetuMovie = true

                Sisetu.start(player)
            }
        }
    }

    private fun entranceTick() {
        // 岩盤上にあるエントランスのワープ処理
        val block = player.location.block.getRelative(BlockFace.DOWN)

        if (block.type == Material.BLACK_CONCRETE && block.getRelative(BlockFace.NORTH).type == Material.BEDROCK) {
            Bukkit.getScheduler().runTask(SnowDawn.plugin, Runnable {
                if (block.world == WorldRegistry.SNOW_LAND) {
                    val dist = Location(WorldRegistry.SECOND_MEGA_STRUCTURE, 2.5, 306.0, 2.5)
                    dist.yaw = 90.0F
                    player.teleport(dist)
                    playerData.prevSnowLandEntrance = block.location

                    if (!playerData.secondMegaStructureEnterFlag) {
                        playerData.secondMegaStructureEnterFlag = true
                        // 初めて足を踏み入れたときには演出を再生する
                        EnterMegaStructure.start(player)
                    }
                } else if (block.world == WorldRegistry.SECOND_MEGA_STRUCTURE) {
                    val dist = playerData.prevSnowLandEntrance?.clone()
                    if (dist != null) {
                        dist.add(-1.5, 1.0, 0.5)
                        dist.yaw = 90.0F
                        player.teleport(dist)
                    }
                }
            })
        }
    }

    private fun updatePlayerInventory() {
        if (tick % 20 == 0) {
            player.updateInventory()
        }
    }

    private fun lastLocationUpdate() {
        if (player.gameMode == GameMode.SURVIVAL && tick % 20 == 0) {
            playerData.lastLocation = player.location
        }
    }

    override fun shouldRemove(): Boolean {
        return !player.isOnline
    }

    fun start() {
        thread.addEntity(this)
    }
}