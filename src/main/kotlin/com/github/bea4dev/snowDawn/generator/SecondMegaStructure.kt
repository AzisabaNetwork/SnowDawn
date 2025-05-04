package com.github.bea4dev.snowDawn.generator

import com.github.bea4dev.snowDawn.generator.structure.placeAsset
import com.github.bea4dev.vanilla_source.api.asset.WorldAssetsRegistry
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator
import de.articdive.jnoise.pipeline.JNoise
import org.bukkit.Material
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import org.bukkit.util.Vector
import java.util.Random
import kotlin.math.min

private class Variables(seed: Long) {
    val populateNoise: JNoise = JNoise.newBuilder()
        .perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed).build())
        .scale(0.1)
        .build()
    val populateDetailNoise: JNoise = JNoise.newBuilder()
        .perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed).build())
        .scale(0.05)
        .build()
}

class SecondMegaStructure(private val seed: Long) : ChunkGenerator() {
    private val variables: ThreadLocal<Variables> = ThreadLocal.withInitial { Variables(seed) }
    private val interval = 5
    private val thickness = 4
    private val chain = WorldAssetsRegistry.getAsset("chain")
    private val chainEnd = WorldAssetsRegistry.getAsset("chain_end")
    private val fan0 = WorldAssetsRegistry.getAsset("fan_0")
    private val fan1 = WorldAssetsRegistry.getAsset("fan_1")
    private val fan2 = WorldAssetsRegistry.getAsset("fan_2")

    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        val variables = this.variables.get()

        val generateChain =
            variables.populateDetailNoise.evaluateNoise(chunkX.toDouble() * 16.0, chunkZ.toDouble() * 16.0) > 0.3
        val chainSize = chain.endPosition.clone().subtract(chain.startPosition).add(Vector(1, 1, 1))
        val chainEndSize = chainEnd.endPosition.clone().subtract(chainEnd.startPosition).add(Vector(1, 1, 1))
        val chunkStartPopulateNoise = variables.populateNoise.evaluateNoise(chunkX * 16.0, chunkZ * 16.0)
        val chainStartX = min(
            variables.populateDetailNoise.evaluateNoise(chunkX.toDouble(), chunkZ.toDouble()) * 8.0 + 8.0,
            16.0 - chainSize.x
        ).toInt()
        val chainStartZ = min(
            variables.populateDetailNoise.evaluateNoise(chunkX * 2.0, chunkZ * 2.0) * 8.0 + 8.0,
            16.0 - chainSize.z
        ).toInt()
        val chainRepeat = (chunkStartPopulateNoise * 16.0 + 16.0).toInt()

        val isGeneratePillar = { chunkX: Int, chunkZ: Int ->
            (chunkX.mod(interval) in -thickness until thickness) && (chunkZ.mod(interval) in -thickness until thickness) && variables.populateNoise.evaluateNoise(
                (chunkX / interval).toDouble(),
                (chunkZ / interval).toDouble()
            ) < 0.3
        }

        if (isGeneratePillar(chunkX - 1, chunkZ) && isGeneratePillar(chunkX + 1, chunkZ)) {
            for (y in -32 until 310 step 16) {
                if (variables.populateDetailNoise.evaluateNoise(chunkX * 16.0, y.toDouble(), chunkZ * 16.0) > 0.5) {
                    val random = variables.populateDetailNoise.evaluateNoise(chunkZ * 16.0, y.toDouble(), chunkX * 16.0)
                    val fanAsset = if (random > 0.3) {
                        fan0
                    } else if (random > -0.3) {
                        fan1
                    } else {
                        fan2
                    }

                    chunkData.placeAsset(fanAsset, 0, y, 0)
                }
            }
        }

        val generatePillar = isGeneratePillar(chunkX, chunkZ)

        for (x in 0 until 16) {
            for (z in 0 until 16) {
                for (y in 310 until 320) {
                    chunkData.setBlock(x, y, z, Material.STONE)
                }

                if (generatePillar) {
                    for (y in -64 until 320) {
                        chunkData.setBlock(x, y, z, Material.STONE)
                    }
                }
            }
        }

        if (!generatePillar) {
            if (generateChain) {
                for (i in 0 until chainRepeat) {
                    if (i < chainRepeat - 1) {
                        val startY = 310 - 1 - (chainSize.blockY * i) - chainSize.blockY
                        chunkData.placeAsset(chain, chainStartX, startY + 1, chainStartZ)
                    } else {
                        val startY = 310 - 1 - (chainSize.blockY * i) - chainEndSize.blockY
                        chunkData.placeAsset(chainEnd, chainStartX, startY + 1, chainStartZ)
                    }
                }
            }
        }
    }
}