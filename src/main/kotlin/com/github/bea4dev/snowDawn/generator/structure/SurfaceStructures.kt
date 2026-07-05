package com.github.bea4dev.snowDawn.generator.structure

import com.github.bea4dev.vanilla_source.api.asset.WorldAsset
import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator
import de.articdive.jnoise.pipeline.JNoise
import org.bukkit.Material
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo
import java.util.Comparator
import java.util.Random
import kotlin.jvm.optionals.getOrDefault

class SurfaceStructures(
    private val shouldPlace: (minX: Int, minY: Int, minZ: Int, worldAsset: WorldAsset) -> Boolean,
    private val structures: List<Pair<WorldAsset, Double>>,
    private val surfaceYFunction: ThreadLocal<(Int, Int) -> Int>? = null,
    private val itemChest: ItemChest,
    seed: Long,
    private val merge: Boolean = true,
) : BlockPopulator() {
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
        val surfaceYFunction = surfaceYFunction?.get()
        val chunkMinX = chunkX * 16
        val chunkMaxX = chunkMinX + 15
        val chunkMinZ = chunkZ * 16
        val chunkMaxZ = chunkMinZ + 15

        for (minX in cellStartsInChunk(chunkMinX, chunkMaxX, maxLengthX)) {
            for (minZ in cellStartsInChunk(chunkMinZ, chunkMaxZ, maxLengthZ)) {
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

                val surfaceY = surfaceYFunction?.invoke(midX, midZ)
                    ?: (limitedRegion.findStructureTerrainSurfaceY(worldInfo, minX, maxX, minZ, maxZ)?.plus(2)
                        ?: continue)

                if (!shouldPlace(minX, surfaceY, minZ, asset)) {
                    continue
                }

                val originY = surfaceY - 2
                val assetHeight = asset.endPosition.blockY - asset.startPosition.blockY

                for (assetX in 0..(maxX - minX)) {
                    val worldX = minX + assetX
                    if (!limitedRegion.isInRegion(worldX, worldInfo.minHeight, minZ)) {
                        continue
                    }

                    for (assetZ in 0..(maxZ - minZ)) {
                        val worldZ = minZ + assetZ
                        if (!limitedRegion.isInRegion(worldX, worldInfo.minHeight, worldZ)) {
                            continue
                        }

                        for (assetY in 0..assetHeight) {
                            val worldY = originY + assetY
                            if (worldY !in worldInfo.minHeight until worldInfo.maxHeight) {
                                continue
                            }

                            val block = asset.getBlock(assetX, assetY, assetZ) ?: continue
                            if (merge && block.material.isAir) {
                                continue
                            }

                            limitedRegion.setBlockData(worldX, worldY, worldZ, block)

                            if (block.material == Material.CHEST) {
                                itemChest.populate(worldX, worldY, worldZ, limitedRegion, noise)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun cellStartsInChunk(chunkMin: Int, chunkMax: Int, cellSize: Int): IntProgression {
        var first = Math.floorDiv(chunkMin, cellSize) * cellSize
        if (first < chunkMin) {
            first += cellSize
        }
        return first..chunkMax step cellSize
    }
}
