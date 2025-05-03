package com.github.bea4dev.snowDawn.generator

import org.bukkit.Material
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.util.Random

class SecondMegaStructure(private val seed: Long) : ChunkGenerator() {
    private val interval = 5
    private val thickness = 4

    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                for (y in 310 until 320) {
                    chunkData.setBlock(x, y, z, Material.STONE)
                }

                if ((chunkX.mod(interval) in -thickness until thickness) && (chunkZ.mod(interval) in -thickness until thickness)) {
                    for (y in -64 until 320) {
                        chunkData.setBlock(x, y, z, Material.STONE)
                    }
                }
            }
        }
    }
}