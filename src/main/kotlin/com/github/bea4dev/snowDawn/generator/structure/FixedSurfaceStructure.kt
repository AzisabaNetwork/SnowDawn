package com.github.bea4dev.snowDawn.generator.structure

import com.github.bea4dev.vanilla_source.api.asset.WorldAsset
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo
import org.bukkit.util.Vector
import java.util.Random

class FixedSurfaceStructure(
    private val structure: WorldAsset,
    private val position: Vector,
    private val merge: Boolean = true,
) : BlockPopulator() {
    override fun populate(
        worldInfo: WorldInfo,
        random: Random,
        chunkX: Int,
        chunkZ: Int,
        limitedRegion: LimitedRegion
    ) {
        val minX = position.blockX
        val minZ = position.blockZ
        val sizeX = structure.endPosition.blockX - structure.startPosition.blockX
        val sizeY = structure.endPosition.blockY - structure.startPosition.blockY
        val sizeZ = structure.endPosition.blockZ - structure.startPosition.blockZ
        val maxX = minX + sizeX
        val maxZ = minZ + sizeZ
        val minY = limitedRegion.findStructureTerrainSurfaceY(worldInfo, minX, maxX, minZ, maxZ)?.plus(1) ?: return

        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val worldX = chunkX * 16 + x
                val worldZ = chunkZ * 16 + z

                if (worldX !in minX..maxX || worldZ !in minZ..maxZ) {
                    continue
                }

                for (y in minY..(minY + sizeY)) {
                    val block = structure.getBlock(worldX - minX, y - minY, worldZ - minZ) ?: continue

                    if (merge && block.material.isAir) {
                        continue
                    }

                    limitedRegion.setBlockData(worldX, y, worldZ, block)
                }
            }
        }
    }
}
