package com.github.bea4dev.snowDawn.generator.structure

import com.github.bea4dev.vanilla_source.api.asset.WorldAsset
import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator
import de.articdive.jnoise.pipeline.JNoise
import org.bukkit.Material
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo
import org.bukkit.util.Vector
import java.util.Random

class FixedPositionStructure(
    private val shouldPlace: (minX: Int, minY: Int, minZ: Int, worldAsset: WorldAsset) -> Boolean,
    private val structure: WorldAsset,
    private val position: Vector,
    private val itemChest: ItemChest,
    seed: Long,
    private val merge: Boolean = true,
) : BlockPopulator() {
    val noise: ThreadLocal<JNoise> = ThreadLocal.withInitial {
        JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed).build())
            .scale(10.0)
            .build()
    }

    override fun populate(
        worldInfo: WorldInfo,
        random: Random,
        chunkX: Int,
        chunkZ: Int,
        limitedRegion: LimitedRegion
    ) {
        val noise = noise.get()

        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val worldX = chunkX * 16 + x
                val worldZ = chunkZ * 16 + z
                val minX = position.blockX
                val minY = position.blockY
                val minZ = position.blockZ

                if (!shouldPlace(minX, minY, minZ, structure)) {
                    continue
                }

                for (y in minY until (minY + (structure.endPosition.blockY - structure.startPosition.blockY))) {
                    val block = structure.getBlock(worldX - minX, y - minY, worldZ - minZ)

                    if (block != null) {
                        if (merge && block.material.isAir) {
                            continue
                        }

                        limitedRegion.setBlockData(worldX, y, worldZ, block)

                        if (block.material == Material.CHEST) {
                            itemChest.populate(worldX, y, worldZ, limitedRegion, noise)
                        }
                    }
                }
            }
        }
    }
}