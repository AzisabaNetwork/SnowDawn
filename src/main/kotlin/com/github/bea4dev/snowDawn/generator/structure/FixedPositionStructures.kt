package com.github.bea4dev.snowDawn.generator.structure

import com.github.bea4dev.vanilla_source.api.asset.WorldAsset
import org.bukkit.block.data.BlockData
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo
import org.bukkit.util.Vector
import java.util.Random

class FixedPositionStructures(
    private val structures: List<Pair<WorldAsset, Vector>>,
    private val blocks: List<Pair<BlockData, Vector>> = emptyList(),
    private val merge: Boolean = false,
) : BlockPopulator() {
    override fun populate(
        worldInfo: WorldInfo,
        random: Random,
        chunkX: Int,
        chunkZ: Int,
        limitedRegion: LimitedRegion
    ) {
        for ((structure, position) in structures) {
            val minX = position.blockX
            val minY = position.blockY
            val minZ = position.blockZ
            val sizeX = structure.endPosition.blockX - structure.startPosition.blockX
            val sizeY = structure.endPosition.blockY - structure.startPosition.blockY
            val sizeZ = structure.endPosition.blockZ - structure.startPosition.blockZ
            val maxX = minX + sizeX
            val maxZ = minZ + sizeZ

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

        for ((blockData, position) in blocks) {
            val blockChunkX = Math.floorDiv(position.blockX, 16)
            val blockChunkZ = Math.floorDiv(position.blockZ, 16)

            if (blockChunkX == chunkX && blockChunkZ == chunkZ) {
                limitedRegion.setBlockData(position.blockX, position.blockY, position.blockZ, blockData)
            }
        }
    }
}
