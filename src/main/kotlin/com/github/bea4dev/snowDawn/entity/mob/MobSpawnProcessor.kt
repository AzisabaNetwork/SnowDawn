package com.github.bea4dev.snowDawn.entity.mob

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.entity.TickBase
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.random.Random

object MobSpawnProcessor : TickBase {
    private const val RANGE = 64
    private const val SPAWN_INTERVAL = 200
    private const val MAX_MOB_COUNT = 100
    private var tick = 0
    var mobCount = 0

    fun init() {
        SnowDawn.ENTITY_THREAD.addEntity(this)
    }

    override fun tick() {
        tick++

        if (tick % SPAWN_INTERVAL == 0) {
            for (player in Bukkit.getOnlinePlayers()) {
                val location = player.location

                if (location.world == WorldRegistry.ASSET || player.gameMode == GameMode.SPECTATOR) {
                    continue
                }

                for (x in location.blockX - RANGE until location.blockX + RANGE) {
                    for (y in location.blockY - RANGE until location.blockY + RANGE) {
                        for (z in location.blockZ - RANGE until location.blockZ + RANGE) {
                            val block = location.world.getBlockAt(x, y, z)
                            val ground = block.getRelative(BlockFace.DOWN)
                            val up = block.getRelative(BlockFace.UP)

                            if (mobCount < MAX_MOB_COUNT
                                && block.lightFromBlocks.toInt() == 0
                                && block.isPassable
                                && ground.isSolid
                                && up.isPassable
                            ) {
                                var random = 3000
                                if (block.lightFromSky.toInt() != 0) {
                                    random = 10000
                                }

                                if (Random.nextInt(random) == 0) {
                                    if (location.world == WorldRegistry.SNOW_LAND) {
                                        val phage = Phage(block.location.add(Vector(0.5, 0.0, 0.5)), 10.0F, 2.0F)
                                        phage.aiController.navigator.speed = 0.20F
                                        phage.dropItems = listOf(
                                            listOf(ItemRegistry.SCRAP.createItemStack()),
                                            listOf(ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 }),
                                            listOf(ItemStack(Material.POTATO)),
                                            listOf(ItemRegistry.FUEL.createItemStack()),
                                            listOf(ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 2 }),
                                            listOf(ItemRegistry.COPPER_INGOT.createItemStack()),
                                        )
                                        phage.spawn()
                                        mobCount++
                                    } else {
                                        val phage = Phage(block.location.add(Vector(0.5, 0.0, 0.5)), 20.0F, 5.0F)
                                        phage.dropItems = listOf(
                                            listOf(ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 }),
                                            listOf(ItemStack(Material.POTATO)),
                                            listOf(ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 2 }),
                                            listOf(ItemRegistry.COPPER_INGOT.createItemStack().also { item -> item.amount = 2 }),
                                            listOf(ItemRegistry.IRON_INGOT.createItemStack()),
                                        )

                                        if (Random.nextInt(3) == 0) {
                                            phage.block = Material.DEEPSLATE
                                        }

                                        phage.spawn()
                                        mobCount++
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun shouldRemove(): Boolean {
        return false
    }
}