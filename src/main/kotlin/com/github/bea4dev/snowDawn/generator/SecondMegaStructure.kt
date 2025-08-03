package com.github.bea4dev.snowDawn.generator

import com.github.bea4dev.snowDawn.generator.structure.placeAsset
import com.github.bea4dev.vanilla_source.api.asset.WorldAssetsRegistry
import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator
import de.articdive.jnoise.pipeline.JNoise
import net.minecraft.core.SectionPos.x
import org.bukkit.Material
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import org.bukkit.util.Vector
import java.util.Random
import kotlin.math.min
import kotlin.math.pow

private class Variables(seed: Long) {
    val populateNoise: JNoise =
        JNoise.newBuilder().perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed).build()).scale(0.1).build()
    val populateDetailNoise: JNoise =
        JNoise.newBuilder().perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed + 100).build()).scale(0.05).build()
    val coalNoise1 = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 16).build())
        .scale(0.1)
        .build()
    val coalNoise2 = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 17).build())
        .scale(0.12)
        .build()
    val copperNoise1 = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 18).build())
        .scale(0.1)
        .build()
    val copperNoise2 = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 19).build())
        .scale(0.12)
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
    private val hideInXP = WorldAssetsRegistry.getAsset("hide_in_xp")
    private val hideOutXP = WorldAssetsRegistry.getAsset("hide_out_xp")
    private val hideInXN = WorldAssetsRegistry.getAsset("hide_in_xn")
    private val hideOutXN = WorldAssetsRegistry.getAsset("hide_out_xn")
    private val hideInZP = WorldAssetsRegistry.getAsset("hide_in_zp")
    private val hideOutZP = WorldAssetsRegistry.getAsset("hide_out_zp")
    private val hideInZN = WorldAssetsRegistry.getAsset("hide_in_zn")
    private val hideOutZN = WorldAssetsRegistry.getAsset("hide_out_zn")

    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        val variables = this.variables.get()

        val isGeneratePillar = { chunkX: Int, chunkZ: Int ->
            (chunkX.mod(interval) in -thickness until thickness) && (chunkZ.mod(interval) in -thickness until thickness) && variables.populateNoise.evaluateNoise(
                (chunkX / interval).toDouble(), (chunkZ / interval).toDouble()
            ) < 0.3
        }

        val generatePillar = isGeneratePillar(chunkX, chunkZ)

        generatePillarAndCeil(chunkX, chunkZ, variables, generatePillar, chunkData)
        generateChain(variables, generatePillar, chunkX, chunkZ, chunkData)
        generateFan(variables, isGeneratePillar, chunkX, chunkZ, chunkData)
        generateHiddenRoom(variables, generatePillar, isGeneratePillar, chunkX, chunkZ, chunkData)
        generateStairs(variables, isGeneratePillar, chunkX, chunkZ, chunkData)

        if (chunkX == 0 && chunkZ == 0) {
            // 出入り口を生成
            chunkData.placeAsset(WorldAssetsRegistry.getAsset("ent_door_1")!!, 0, 319 - 14, 0)
        }
    }

    private fun generatePillarAndCeil(
        chunkX: Int,
        chunkZ: Int,
        variables: Variables,
        generatePillar: Boolean,
        chunkData: ChunkData
    ) {
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                for (y in 310 until 320) {
                    setStone(chunkX, chunkZ, x, y, z, variables, chunkData)
                }

                if (generatePillar) {
                    for (y in -64 until 320) {
                        setStone(chunkX, chunkZ, x, y, z, variables, chunkData)
                    }
                }

                chunkData.setBlock(x, 319, z, Material.BEDROCK)
            }
        }
    }

    private fun setStone(chunkX: Int, chunkZ: Int, x: Int, y: Int, z: Int, variables: Variables, chunkData: ChunkData) {
        val worldX = chunkX * 16 + x
        val worldZ = chunkZ * 16 + z

        val coalNoise1 = variables.coalNoise1.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
        val coalNoise2 = variables.coalNoise2.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())

        val copperNoise1 = variables.copperNoise1.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
        val copperNoise2 = variables.copperNoise2.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())

        if (coalNoise1 * coalNoise2 > 0.45) {
            chunkData.setBlock(x, y, z, Material.COAL_ORE)
        } else if (copperNoise1 * copperNoise2 > 0.5) {
            chunkData.setBlock(x, y, z, Material.COPPER_ORE)
        } else {
            chunkData.setBlock(x, y, z, Material.STONE)
        }
    }

    private fun generateChain(
        variables: Variables, generatePillar: Boolean, chunkX: Int, chunkZ: Int, chunkData: ChunkData
    ) {
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
            variables.populateDetailNoise.evaluateNoise(chunkX * 2.0, chunkZ * 2.0) * 8.0 + 8.0, 16.0 - chainSize.z
        ).toInt()
        val chainRepeat = (chunkStartPopulateNoise * 16.0 + 16.0).toInt()

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

    private fun generateFan(
        variables: Variables, isGeneratePillar: (Int, Int) -> Boolean, chunkX: Int, chunkZ: Int, chunkData: ChunkData
    ) {
        if (isGeneratePillar(chunkX - 1, chunkZ) && isGeneratePillar(chunkX + 1, chunkZ)) {
            for (y in -32 until (310 - 16) step 16) {
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
    }

    private fun generateHiddenRoom(
        variables: Variables,
        generatePillar: Boolean,
        isGeneratePillar: (Int, Int) -> Boolean,
        chunkX: Int,
        chunkZ: Int,
        chunkData: ChunkData
    ) {
        val isGenerateHiddenRoom = { chunkX: Int, chunkZ: Int ->
            variables.populateNoise.evaluateNoise(chunkX * 16.0, chunkZ * 16.0) > 0.3
        }

        val isCenterOfPillar = { chunkX: Int, chunkZ: Int ->
            (chunkX.mod(interval) == 1) && (chunkZ.mod(interval) == 1) && isGenerateHiddenRoom(
                chunkX, chunkZ
            ) && isGeneratePillar(chunkX + 1, chunkZ) && isGeneratePillar(
                chunkX - 1, chunkZ
            ) && isGeneratePillar(chunkX, chunkZ + 1) && isGeneratePillar(chunkX, chunkZ - 1)
        }

        val waterHeight = 200
        val enterHeight = 24

        if (!generatePillar) {
            if (isCenterOfPillar(chunkX + 2, chunkZ)) {
                chunkData.placeAsset(hideOutXN, 15, waterHeight + enterHeight, 5)
            }
            if (isCenterOfPillar(chunkX - 3, chunkZ)) {
                chunkData.placeAsset(hideOutXP, -16, waterHeight + enterHeight, 5)
            }
            if (isCenterOfPillar(chunkX, chunkZ + 2)) {
                chunkData.placeAsset(hideOutZN, 5, waterHeight + enterHeight, 15)
            }
            if (isCenterOfPillar(chunkX, chunkZ - 3)) {
                chunkData.placeAsset(hideOutZP, 5, waterHeight + enterHeight, -16)
            }

            if (isCenterOfPillar(chunkX + 2, chunkZ) && isCenterOfPillar(chunkX - 3, chunkZ)) {
                chunkData.placeAsset(hideInXN, 0, waterHeight + enterHeight, 5, true)
            }
            if (isCenterOfPillar(chunkX, chunkZ + 2) && isCenterOfPillar(chunkX, chunkZ - 3)) {
                chunkData.placeAsset(hideInZN, 5, waterHeight + enterHeight, 0, true)
            }
            return
        }

        if (isCenterOfPillar(chunkX, chunkZ)) {
            for (x in 0 until 16) {
                for (z in 0 until 16) {
                    val radius = 6.0
                    val xFromCenter = x - 7.5
                    val zFromCenter = z - 7.5

                    if (xFromCenter * xFromCenter + zFromCenter * zFromCenter < radius * radius) {
                        for (y in 0 until waterHeight) {
                            chunkData.setBlock(x, y, z, Material.WATER)
                        }
                    } else {
                        for (y in 0 until waterHeight) {
                            chunkData.setBlock(x, y, z, Material.STONE)
                        }
                    }

                    for (y in waterHeight until 310) {
                        chunkData.setBlock(x, y, z, Material.AIR)
                    }
                }
            }
        } else {
            if (isCenterOfPillar(chunkX + 1, chunkZ)) {
                chunkData.placeAsset(hideOutXN, -1, waterHeight + enterHeight, 5)
            }

            if (isCenterOfPillar(chunkX - 2, chunkZ)) {
                chunkData.placeAsset(hideOutXP, 0, waterHeight + enterHeight, 5)
            }
            if (isCenterOfPillar(chunkX - 1, chunkZ)) {
                chunkData.placeAsset(hideInXP, 0, waterHeight + enterHeight, 5)
            }

            if (isCenterOfPillar(chunkX, chunkZ + 1)) {
                chunkData.placeAsset(hideOutZN, 5, waterHeight + enterHeight, -1)
            }

            if (isCenterOfPillar(chunkX, chunkZ - 2)) {
                chunkData.placeAsset(hideOutZP, 5, waterHeight + enterHeight, 0)
            }
            if (isCenterOfPillar(chunkX, chunkZ - 1)) {
                chunkData.placeAsset(hideInZP, 5, waterHeight + enterHeight, 0)
            }
        }
    }

    private fun generateStairs(
        variables: Variables, isGeneratePillar: (Int, Int) -> Boolean, chunkX: Int, chunkZ: Int, chunkData: ChunkData
    ) {
        val isGenerateStairs = { chunkX: Int, chunkZ: Int ->
            val x = chunkX.mod(interval)
            val z = chunkZ.mod(interval)

            variables.populateNoise.evaluateNoise(x * 16.0, z * 16.0) > -0.2
        }

        if (!isGenerateStairs(chunkX, chunkZ)) {
            return
        }

        val zFunction = { z: Int ->
            val worldZ = chunkZ * 16 + z
            worldZ.mod(256) - 64
        }
        val xFunction = { x: Int ->
            val worldX = chunkX * 16 + x
            (worldX.mod(256) * -1) + 192
        }

        if (isGeneratePillar(chunkX + 1, chunkZ)) {
            for (z in 0 until 16) {
                val y = zFunction(z + chunkX * 16)

                chunkData.setBlock(15, y, z, Material.STONE)
            }
        }
        if (isGeneratePillar(chunkX - 1, chunkZ)) {
            for (z in 0 until 16) {
                val y = zFunction(z - chunkX * 16)

                chunkData.setBlock(0, y, z, Material.STONE)
            }
        }
        if (isGeneratePillar(chunkX, chunkZ + 1)) {
            for (x in 0 until 16) {
                val y = xFunction(x + chunkZ * 16)

                chunkData.setBlock(x, y, 15, Material.STONE)
            }
        }
        if (isGeneratePillar(chunkX, chunkZ - 1)) {
            for (x in 0 until 16) {
                val y = xFunction(x - chunkZ * 16)

                chunkData.setBlock(x, y, 0, Material.STONE)
            }
        }
    }
}