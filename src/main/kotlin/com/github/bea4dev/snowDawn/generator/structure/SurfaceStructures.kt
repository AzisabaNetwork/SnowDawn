package com.github.bea4dev.snowDawn.generator.structure

import com.github.bea4dev.vanilla_source.api.asset.WorldAsset
import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator
import de.articdive.jnoise.pipeline.JNoise
import org.bukkit.generator.ChunkGenerator
import java.util.Comparator
import kotlin.jvm.optionals.getOrDefault

class SurfaceStructures(
    private val shouldPlace: (minX: Int, minY: Int, minZ: Int, worldAsset: WorldAsset, chunkData: ChunkGenerator.ChunkData) -> Boolean,
    private val structures: List<Pair<WorldAsset, Double>>,
    seed: Long,
    private val merge: Boolean = true,
) {
    private val maxLengthX =
        structures.stream().map { entry -> entry.first }
            .map { asset -> asset.endPosition.blockX - asset.startPosition.blockX + 1 }
            .max(Comparator.naturalOrder())
            .getOrDefault(0)
    private val maxLengthZ =
        structures.stream().map { entry -> entry.first }
            .map { asset -> asset.endPosition.blockZ - asset.startPosition.blockZ + 1 }
            .max(Comparator.naturalOrder())
            .getOrDefault(0)

    val noise: ThreadLocal<JNoise> = ThreadLocal.withInitial {
        JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed).build())
            .scale(0.01)
            .build()
    }

    fun generate(
        chunkX: Int,
        surfaceYFunction: (Int, Int) -> Int,
        chunkZ: Int,
        chunkData: ChunkGenerator.ChunkData,
    ) {
        val noise = noise.get()

        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val worldX = chunkX * 16 + x
                val worldZ = chunkZ * 16 + z
                val minX = worldX - worldX.mod(maxLengthX)
                val minZ = worldZ - worldZ.mod(maxLengthZ)

                val assetAndRate = structures[(minX xor minZ).mod(structures.size)]
                val asset = assetAndRate.first
                val rate = assetAndRate.second

                val noiseValue = noise.evaluateNoise(minX.toDouble(), minZ.toDouble())
                if ((noiseValue + 1.0) / 2.0 > rate) {
                    continue
                }

                val maxX = minX + (asset.endPosition.blockX - asset.startPosition.blockX)
                val maxZ = minZ + (asset.endPosition.blockZ - asset.startPosition.blockZ)

                val midX = (minX + maxX) / 2
                val midZ = (minZ + maxZ) / 2

                val surfaceY = surfaceYFunction(midX, midZ)

                if (!shouldPlace(minX, surfaceY, minZ, asset, chunkData)) {
                    continue
                }

                for (y in surfaceY until chunkData.maxHeight) {
                    val block = asset.getBlock(worldX - minX, y - surfaceY, worldZ - minZ)

                    if (block != null) {
                        if (merge && block.material.isAir) {
                            continue
                        }

                        chunkData.setBlock(x, y - 2, z, block)
                    }
                }
            }
        }
    }
}