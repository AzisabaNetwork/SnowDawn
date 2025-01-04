package com.github.bea4dev.snowDawn.generator.structure

import com.github.bea4dev.vanilla_source.api.asset.WorldAsset
import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator
import de.articdive.jnoise.pipeline.JNoise
import org.bukkit.block.data.BlockData
import org.bukkit.util.Vector
import java.util.Comparator
import kotlin.math.max

class SurfaceStructures(
    private val structures: List<WorldAsset>,
    private val rate: Double,
    private val excludeStart: Vector,
    private val excludeEnd: Vector,
    seed: Long
) : Structures {

    private val sizeX: Int = structures.stream()
        .map { asset -> asset.endPosition.blockX - asset.startPosition.blockX }
        .max(Comparator.naturalOrder())
        .get()
    private val sizeZ: Int = structures.stream()
        .map { asset -> asset.endPosition.blockZ - asset.endPosition.blockZ }
        .max(Comparator.naturalOrder())
        .get()

    private val noise: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed).build())
        .scale(0.1)
        .build()
    private val selectNoise: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed).build())
        .scale(0.2)
        .build()

    override fun getBlock(x: Int, structureY: Int, z: Int): BlockData? {
        val assetX = x.mod(sizeX)
        val assetZ = z.mod(sizeZ)

        if (assetX in excludeStart.blockX..excludeEnd.blockX) {
            return null
        }
        if (assetZ in excludeStart.blockZ..excludeEnd.blockZ) {
            return null
        }

        val minX = x - assetX
        val minZ = z - assetZ

        val noise = (this.noise.evaluateNoise(minX.toDouble(), minZ.toDouble()) + 1.0) / 2.0

        if (noise > rate) {
            return null
        }

        val selectNoise = (this.selectNoise.evaluateNoise(minX.toDouble(), minZ.toDouble()) + 1.0) / 2.0

        val index = max((selectNoise * structures.size).toInt(), structures.size - 1)
        val asset = structures[index]

        return asset.getBlock(assetX, structureY, assetZ)
    }
}