package com.github.bea4dev.snowDawn.generator.structure

import com.github.bea4dev.vanilla_source.api.asset.WorldAsset
import org.bukkit.generator.ChunkGenerator
import java.util.Comparator
import kotlin.jvm.optionals.getOrDefault

class SurfaceStructures(
    private val shouldPlace: (minX: Int, minY: Int, minZ: Int, worldAsset: WorldAsset, chunkData: ChunkGenerator.ChunkData) -> Boolean,
    private val structures: List<WorldAsset>,
    private val merge: Boolean = true,
) {
    private val maxLengthX =
        structures.stream().map { asset -> asset.endPosition.blockX - asset.startPosition.blockX + 1 }
            .max(Comparator.naturalOrder())
            .getOrDefault(0)
    private val maxLengthZ =
        structures.stream().map { asset -> asset.endPosition.blockZ - asset.startPosition.blockZ + 1 }
            .max(Comparator.naturalOrder())
            .getOrDefault(0)

    fun generate(
        chunkX: Int,
        surfaceYFunction: (Int, Int) -> Int,
        chunkZ: Int,
        chunkData: ChunkGenerator.ChunkData
    ) {
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val worldX = chunkX * 16 + x
                val worldZ = chunkZ * 16 + z
                val minX = worldX - worldX.mod(maxLengthX)
                val minZ = worldZ - worldZ.mod(maxLengthZ)

                val surfaceY = surfaceYFunction(minX, minZ)

                val asset = structures[(minX xor minZ).mod(structures.size)]

                if (!shouldPlace(minX, surfaceY, minZ, asset, chunkData)) {
                    continue
                }

                for (y in surfaceY until chunkData.maxHeight) {
                    val block = asset.getBlock(worldX - minX, y - surfaceY, worldZ - minZ)

                    if (block != null) {
                        if (merge && block.material.isAir) {
                            continue
                        }

                        chunkData.setBlock(x, y, z, block)
                    }
                }
            }
        }
    }
}