package com.github.bea4dev.snowDawn.generator

import com.github.bea4dev.snowDawn.generator.structure.FixedPositionStructure
import com.github.bea4dev.snowDawn.generator.structure.ItemChest
import com.github.bea4dev.snowDawn.generator.structure.SurfaceStructures
import com.github.bea4dev.snowDawn.generator.structure.UnderGroundStructures
import com.github.bea4dev.snowDawn.generator.structure.placeAsset
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.vanilla_source.api.asset.WorldAssetsRegistry
import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator
import de.articdive.jnoise.pipeline.JNoise
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.abs
import kotlin.math.floor

private class SnowLandGeneratorVariables(seed: Long) {
    val baseNoise: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed).build())
        .scale(0.005)
        .build()

    val shapeNoise1: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 1).build())
        .scale(0.01)
        .build()

    val shapeNoise2: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 2).build())
        .scale(0.002)
        .build()

    val detailNoise1: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 3).build())
        .scale(0.003)
        .build()

    val detailNoise2: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 4).build())
        .scale(0.02)
        .build()

    val caveNoise1: JNoise = JNoise.newBuilder()
        .perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed + 5).build())
        .scale(0.007)
        .build()
    val caveNoise2: JNoise = JNoise.newBuilder()
        .perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed + 6).build())
        .scale(0.009)
        .build()
    val caveNoise3: JNoise = JNoise.newBuilder()
        .perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed + 7).build())
        .scale(0.01)
        .build()
    val caveNoise4: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 8).build())
        .scale(0.006)
        .build()
    val caveNoise5: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 9).build())
        .scale(0.007)
        .build()
    val caveNoise7: JNoise = JNoise.newBuilder()
        .perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed + 10).build())
        .scale(0.009)
        .build()
    val caveNoise8: JNoise = JNoise.newBuilder()
        .perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed + 11).build())
        .scale(0.01)
        .build()
    val caveNoise9: JNoise = JNoise.newBuilder()
        .perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed + 12).build())
        .scale(0.012)
        .build()
    val caveNoise10: JNoise = JNoise.newBuilder()
        .perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed + 13).build())
        .scale(0.01)
        .build()
    val caveNoise11: JNoise = JNoise.newBuilder()
        .perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed + 14).build())
        .scale(0.012)
        .build()
    val caveNoise12: JNoise = JNoise.newBuilder()
        .perlin(PerlinNoiseGenerator.newBuilder().setSeed(seed + 15).build())
        .scale(0.013)
        .build()

    val detailNoise: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 13).build())
        .scale(0.05)
        .build()

    val iceNoise: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 14).build())
        .scale(0.01)
        .build()

    val hardBlockNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 15).build())
        .scale(0.007)
        .build()

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
    val holeRadius = 1000

    fun evaluateLowerHeight(x: Double, z: Double): Double {
        return baseNoise.evaluateNoise(x, z) * 16 + 128
    }

    fun evaluateHeight(x: Double, z: Double): Double {
        if (x * x + z * z > holeRadius * holeRadius) {
            return 256 + detailNoise2.evaluateNoise(x, z)
        } else {
            val lowerLandHeight = this.evaluateLowerHeight(x, z)
            var landHeight = lowerLandHeight

            val shapeValue1 = shapeNoise1.evaluateNoise(x, z) * 8 + 8
            if (shapeValue1 > 8) {
                landHeight += shapeValue1
            }

            val shapeValue2 = shapeNoise2.evaluateNoise(x, z) * 4
            if (shapeValue2 < -2 && shapeValue1 <= 8) {
                landHeight += shapeValue2
            }

            val detailValue1 = detailNoise1.evaluateNoise(x, z) * 1.5
            if (shapeValue1 > 8) {
                landHeight += detailValue1
            }

            val detailValue = detailNoise2.evaluateNoise(x, z) * 1.5
            landHeight += detailValue

            return landHeight
        }
    }

    fun getSpawnAssetRange(): Pair<Vector, Vector> {
        val asset = WorldAssetsRegistry.getAsset("ship_1")
        val assetSizeX = asset.endPosition.blockX - asset.startPosition.blockX
        val assetSizeZ = asset.endPosition.blockZ - asset.startPosition.blockZ
        val assetHalfSizeX = assetSizeX / 2 + 1
        val assetHalfSizeZ = assetSizeZ / 2 + 1
        var centerX = 0
        var centerZ = 0

        loop@ while (true) {
            for (x in (centerX - assetHalfSizeX)..(centerX + assetHalfSizeX)) {
                for (z in (centerZ - assetHalfSizeZ)..(centerZ + assetHalfSizeZ)) {
                    if (!canPlaceSpawnPoint(x, z)) {
                        centerX += assetSizeX
                        centerZ += assetSizeZ
                        continue@loop
                    }
                }
            }

            break@loop
        }

        return Pair(
            Vector(centerX - assetHalfSizeX, 0, centerZ - assetHalfSizeZ),
            Vector(centerX + assetHalfSizeX, 0, centerZ + assetHalfSizeZ)
        )
    }

    private fun canPlaceSpawnPoint(x: Int, z: Int): Boolean {
        val shapeValue1 = shapeNoise1.evaluateNoise(x.toDouble(), z.toDouble()) * 8 + 8
        if (shapeValue1 > 8) {
            return true
        }
        return false
    }
}

class SnowLand internal constructor(seed: Long) : ChunkGenerator() {

    private val variables = ThreadLocal.withInitial { SnowLandGeneratorVariables(seed) }
    private val spawnAsset = WorldAssetsRegistry.getAsset("ship_1")!!
    val spawnAssetRange = variables.get().getSpawnAssetRange()
    private val surfaceStructures = SurfaceStructures(
        { minX, surfaceY, minZ, asset -> true },
        listOf(
            WorldAssetsRegistry.getAsset("bridge_0")!! to 0.1,
            WorldAssetsRegistry.getAsset("bridge_1")!! to 0.2,
            WorldAssetsRegistry.getAsset("bridge_2")!! to 0.2,
            WorldAssetsRegistry.getAsset("bridge_3")!! to 0.1,
            WorldAssetsRegistry.getAsset("bridge_4")!! to 0.1,
            WorldAssetsRegistry.getAsset("bridge_mini_0")!! to 0.2,
            WorldAssetsRegistry.getAsset("bridge_mini_1")!! to 0.2,
            WorldAssetsRegistry.getAsset("ant_0")!! to 0.2,
        ),
        ThreadLocal.withInitial {
            val variables = variables.get()
            return@withInitial { chunkX: Int, chunkZ: Int ->
                variables.evaluateHeight(
                    chunkX.toDouble(),
                    chunkZ.toDouble()
                ).toInt()
            }
        },
        ItemChest(
            listOf(
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 5 },
                    ItemRegistry.TORCH.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.BLUE_PRINT_COPPER_EQUIPMENTS.createItemStack(),
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                ),
                listOf(
                    ItemStack(Material.POTATO, 5),
                    ItemStack(Material.POISONOUS_POTATO, 2),
                    ItemStack(Material.CARROT, 7),
                    ItemStack(Material.BAKED_POTATO, 3),
                    ItemRegistry.DIRT.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.DIRT.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.ICE.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 3 },
                ),
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemStack(Material.POTATO, 1),
                    ItemStack(Material.POISONOUS_POTATO, 1),
                    ItemStack(Material.CARROT, 2),
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 5 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.ICE.createItemStack().also { item -> item.amount = 3 },
                    ItemRegistry.DIRT.createItemStack().also { item -> item.amount = 3 },
                ),
                listOf(
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 3 },
                    ItemRegistry.BLUE_PRINT_COMPASS.createItemStack(),
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 3 },
                ),
                listOf(
                    ItemRegistry.BLUE_PRINT_STONE_HOE.createItemStack(),
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 7 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_CATALYST.createItemStack(),
                ),
                listOf(
                    ItemRegistry.BLUE_PRINT_STONE_SHOVEL.createItemStack(),
                    ItemRegistry.COPPER_INGOT.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_CRAFTING_TABLE.createItemStack()
                ),
                listOf(
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 4 },
                    ItemRegistry.DIRT.createItemStack().also { item -> item.amount = 3 },
                    ItemRegistry.BLUE_PRINT_CATALYST.createItemStack(),
                    ItemRegistry.ICE.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                ),
                listOf(
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SAPLING.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.DIRT.createItemStack().also { item -> item.amount = 4 },
                    ItemStack(Material.POTATO, 3),
                    ItemStack(Material.POTATO, 2),
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.ICE.createItemStack().also { item -> item.amount = 5 },
                    ItemRegistry.BLUE_PRINT_CHEST.createItemStack(),
                )
            )
        ),
        seed,
    )
    private val roomStructures = UnderGroundStructures(
        { minX, surfaceY, minZ, asset -> true },
        listOf(
            WorldAssetsRegistry.getAsset("room_0")!! to 0.1,
            WorldAssetsRegistry.getAsset("room_1")!! to 0.2,
        ),
        ItemChest(
            listOf(
                listOf(
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 4 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.BLUE_PRINT_COPPER_EQUIPMENTS.createItemStack(),
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 4 },
                ),
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_CATALYST.createItemStack(),
                ),
                listOf(
                    ItemStack(Material.POTATO, 3),
                    ItemStack(Material.POISONOUS_POTATO, 2),
                    ItemStack(Material.CARROT, 1),
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_COMPASS.createItemStack(),
                    ItemRegistry.BLUE_PRINT_CHEST.createItemStack(),
                ),
                listOf(
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 3 },
                    ItemRegistry.BLUE_PRINT_COPPER_EQUIPMENTS.createItemStack(),
                    ItemRegistry.BLUE_PRINT_CATALYST.createItemStack(),
                ),
                listOf(
                    ItemRegistry.BLUE_PRINT_STONE_AXE.createItemStack(),
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_COPPER_EQUIPMENTS.createItemStack()
                ),
                listOf(
                    ItemRegistry.BLUE_PRINT_STONE_SHOVEL.createItemStack(),
                    ItemRegistry.COPPER_INGOT.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_COMPASS.createItemStack(),
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 5 },
                    ItemRegistry.BLUE_PRINT_CRAFTING_TABLE.createItemStack()
                )
            )
        ),
        seed,
        0,
        merge = false,
    )
    private val miniRoomStructures = UnderGroundStructures(
        { minX, surfaceY, minZ, asset -> true },
        listOf(
            WorldAssetsRegistry.getAsset("room_0")!! to 0.1,
            WorldAssetsRegistry.getAsset("room_1")!! to 0.1,
        ),
        ItemChest(
            listOf(
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.TORCH.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.BLUE_PRINT_CATALYST.createItemStack()
                ),
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_COPPER_EQUIPMENTS.createItemStack(),
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 3 },
                ),
                listOf(
                    ItemStack(Material.POTATO, 3),
                    ItemStack(Material.POISONOUS_POTATO, 2),
                    ItemStack(Material.CARROT, 1),
                    ItemStack(Material.POTATO, 1),
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_COPPER_EQUIPMENTS.createItemStack()
                ),
                listOf(
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 3 },
                    ItemRegistry.BLUE_PRINT_COPPER_EQUIPMENTS.createItemStack(),
                    ItemRegistry.BLUE_PRINT_COMPASS.createItemStack()
                ),
                listOf(
                    ItemRegistry.BLUE_PRINT_STONE_AXE.createItemStack(),
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_CATALYST.createItemStack()
                ),
                listOf(
                    ItemRegistry.BLUE_PRINT_STONE_SHOVEL.createItemStack(),
                    ItemRegistry.COPPER_INGOT.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_COMPASS.createItemStack(),
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 5 },
                )
            )
        ),
        seed,
        72,
        merge = false,
    )
    private val sisetuStructure = FixedPositionStructure(
        { minX, surfaceY, minZ, asset -> true },
        WorldAssetsRegistry.getAsset("sisetu")!!,
        Vector(1200, 255, 0),
        ItemChest(
            listOf(
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.TORCH.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.BLUE_PRINT_COPPER_EQUIPMENTS.createItemStack(),
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.IRON_INGOT.createItemStack().also { item -> item.amount = 1 },
                ),
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.BLUE_PRINT_COPPER_EQUIPMENTS.createItemStack(),
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.IRON_INGOT.createItemStack().also { item -> item.amount = 2 },
                ),
                listOf(
                    ItemStack(Material.POTATO, 3),
                    ItemStack(Material.POISONOUS_POTATO, 2),
                    ItemStack(Material.CARROT, 1),
                    ItemStack(Material.POTATO, 1),
                    ItemStack(Material.BAKED_POTATO, 2),
                    ItemRegistry.BLUE_PRINT_COPPER_EQUIPMENTS.createItemStack(),
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.COPPER_INGOT.createItemStack().also { item -> item.amount = 3 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.IRON_INGOT.createItemStack().also { item -> item.amount = 1 },
                ),
                listOf(
                    ItemRegistry.BLUE_PRINT_STONE_AXE.createItemStack(),
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.BLUE_PRINT_STONE_SHOVEL.createItemStack(),
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_COPPER_EQUIPMENTS.createItemStack(),
                    ItemRegistry.IRON_INGOT.createItemStack().also { item -> item.amount = 3 },
                    ItemStack(Material.COPPER_ORE, 2),
                )
            )
        ),
        seed,
        merge = true,
    )

    private val populators = listOf(
        surfaceStructures,
        roomStructures,
        miniRoomStructures,
        sisetuStructure
    )

    override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        val variables = variables.get()

        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val worldX = chunkX * 16 + x
                val worldZ = chunkZ * 16 + z
                val landHeight = variables.evaluateHeight(worldX.toDouble(), worldZ.toDouble())
                val hardBlockHeight = variables.hardBlockNoise.evaluateNoise(
                    worldX.toDouble(),
                    worldZ.toDouble()
                ) * 3.0 + variables.detailNoise.evaluateNoise(worldX.toDouble(), worldZ.toDouble()) * 1.5
                val hardBlockRange = (hardBlockHeight.toInt() - 5)..<hardBlockHeight.toInt()

                for (y in chunkData.minHeight until chunkData.maxHeight) {
                    if (worldX in spawnAssetRange.first.blockX..spawnAssetRange.second.blockX) {
                        if (worldZ in spawnAssetRange.first.blockZ..spawnAssetRange.second.blockZ) {
                            val assetX = worldX - spawnAssetRange.first.blockX
                            val assetY = y - floor(landHeight).toInt()
                            val assetZ = worldZ - spawnAssetRange.first.blockZ

                            val block = spawnAsset.getBlock(assetX, assetY, assetZ)
                            if (block != null && !block.material.isAir) {
                                chunkData.setBlock(x, y, z, block)
                                continue
                            }
                        }
                    }

                    if (y in hardBlockRange) {
                        chunkData.setBlock(x, y, z, Material.DEEPSLATE)
                        continue
                    }

                    if (y == -64) {
                        chunkData.setBlock(x, y, z, Material.BEDROCK)
                        continue
                    }

                    val detailNoise =
                        variables.detailNoise.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())

                    val caveNoise1 =
                        variables.caveNoise1.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
                    val caveNoise2 =
                        variables.caveNoise2.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
                    val caveNoise3 =
                        variables.caveNoise3.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
                    val isNoodleCave1 =
                        (caveNoise1 * caveNoise1 + caveNoise2 * caveNoise2 + abs(caveNoise3) * 0.006 + detailNoise * 0.004) < 0.004

                    val caveNoise7 =
                        variables.caveNoise7.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
                    val caveNoise8 =
                        variables.caveNoise8.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
                    val caveNoise9 =
                        variables.caveNoise9.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
                    val isNoodleCave2 =
                        (caveNoise7 * caveNoise7 + caveNoise8 * caveNoise8 + abs(caveNoise9) * 0.006 + detailNoise * 0.004) < 0.004

                    val caveNoise10 =
                        variables.caveNoise10.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
                    val caveNoise11 =
                        variables.caveNoise11.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
                    val caveNoise12 =
                        variables.caveNoise12.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
                    val isNoodleCave3 =
                        (caveNoise10 * caveNoise10 + caveNoise11 * caveNoise11 + abs(caveNoise12) * 0.006 + detailNoise * 0.004) < 0.0043

                    val isNoodleCave = isNoodleCave1 || isNoodleCave2 || isNoodleCave3

                    val caveNoise4 =
                        variables.caveNoise4.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
                    val caveNoise5 =
                        variables.caveNoise5.evaluateNoise(worldX.toDouble(), y.toDouble(), worldZ.toDouble())
                    val cheeseCaveNoise = (caveNoise4 + caveNoise5 + detailNoise * 0.08)
                    val isCheeseCave = cheeseCaveNoise > 1.2 || cheeseCaveNoise < -1.15

                    val isCaveHole = isNoodleCave || isCheeseCave

                    if (isCaveHole) {
                        if (y == floor(landHeight).toInt()) {
                            chunkData.setBlock(x, y - 1, z, Material.POWDER_SNOW)
                        }
                    } else {
                        if (y < floor(landHeight).toInt()) {
                            val iceNoise =
                                variables.iceNoise.evaluateNoise(
                                    worldX.toDouble(),
                                    y.toDouble(),
                                    worldZ.toDouble()
                                ) + detailNoise * 0.1
                            when (iceNoise) {
                                in 0.9..<1.0 -> {
                                    chunkData.setBlock(x, y, z, Material.ICE)
                                }

                                else -> {
                                    this.generateStone(variables, x, y, z, chunkData)
                                }
                            }
                        } else if (y == floor(landHeight).toInt()) {
                            val layerIndex = ((landHeight - floor(landHeight)) * 8).toInt()
                            val layer = SnowLayer[layerIndex]
                            chunkData.setBlock(x, y, z, layer)
                        }
                    }
                }
            }
        }

        chunkData.placeAsset(WorldAssetsRegistry.getAsset("ent_door_0")!!, 0, -64, 0, true)
    }

    private fun generateStone(variables: SnowLandGeneratorVariables, x: Int, y: Int, z: Int, chunkData: ChunkData) {
        val coalNoise1 = variables.coalNoise1.evaluateNoise(x.toDouble(), y.toDouble(), z.toDouble())
        val coalNoise2 = variables.coalNoise2.evaluateNoise(x.toDouble(), y.toDouble(), z.toDouble())

        val copperNoise1 = variables.copperNoise1.evaluateNoise(x.toDouble(), y.toDouble(), z.toDouble())
        val copperNoise2 = variables.copperNoise2.evaluateNoise(x.toDouble(), y.toDouble(), z.toDouble())

        if (coalNoise1 * coalNoise2 > 0.45) {
            chunkData.setBlock(x, y, z, Material.COAL_ORE)
        } else if (copperNoise1 * copperNoise2 > 0.5) {
            chunkData.setBlock(x, y, z, Material.COPPER_ORE)
        } else {
            chunkData.setBlock(x, y, z, Material.STONE)
        }
    }

    override fun getDefaultPopulators(world: World): List<BlockPopulator> {
        return populators
    }

}