package com.github.bea4dev.snowDawn.generator.structure

import com.github.bea4dev.vanilla_source.api.asset.WorldAsset
import org.bukkit.generator.ChunkGenerator

fun ChunkGenerator.ChunkData.placeAsset(asset: WorldAsset, startX: Int, startY: Int, startZ: Int, merge: Boolean = false) {
    val assetSize = asset.endPosition.clone().subtract(asset.startPosition)

    for (x in startX..startX + assetSize.blockX) {
        for (y in startY..startY + assetSize.blockY) {
            for (z in startZ..startZ + assetSize.blockZ) {
                val block = asset.getBlock(x - startX, y - startY, z - startZ)

                if (block != null) {
                    if (merge && block.material.isAir) {
                        continue
                    }

                    this.setBlock(x, y, z, block)
                }
            }
        }
    }
}