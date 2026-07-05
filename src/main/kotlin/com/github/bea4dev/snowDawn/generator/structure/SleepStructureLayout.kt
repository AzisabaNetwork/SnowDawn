package com.github.bea4dev.snowDawn.generator.structure

import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.asset.WorldAsset
import com.github.bea4dev.vanilla_source.api.asset.WorldAssetsRegistry
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.Vector

object SleepStructureLayout {
    val sleep0Asset: WorldAsset by lazy { WorldAssetsRegistry.getAsset("sleep_0")!! }
    val sleep1Asset: WorldAsset by lazy { WorldAssetsRegistry.getAsset("sleep_1")!! }

    val sleep0Position = Vector(0, 63, 0)
    val chestPosition: Vector by lazy {
        Vector(sleep0Size.blockX / 2 - 1, 63, sleep0Position.blockZ + sleep0Size.blockZ + 1)
    }
    val sleep1Position: Vector by lazy {
        Vector(0, 63, chestPosition.blockZ + 1)
    }

    val structures: List<Pair<WorldAsset, Vector>> by lazy {
        listOf(
            sleep0Asset to sleep0Position,
            sleep1Asset to sleep1Position,
        )
    }

    fun isProtectedBlock(block: Block): Boolean {
        return isSnowLand(block.world) &&
                (isInsideStructure(block, sleep0Asset, sleep0Position) ||
                        isInsideStructure(block, sleep1Asset, sleep1Position) ||
                        isChestBlock(block))
    }

    fun isChestBlock(block: Block): Boolean {
        return isSnowLand(block.world) &&
                block.x == chestPosition.blockX &&
                block.y == chestPosition.blockY &&
                block.z == chestPosition.blockZ
    }

    private val sleep0Size: Vector by lazy { sleep0Asset.endPosition.clone().subtract(sleep0Asset.startPosition) }

    private fun isInsideStructure(block: Block, asset: WorldAsset, position: Vector): Boolean {
        val size = asset.endPosition.clone().subtract(asset.startPosition)

        return block.x in position.blockX..(position.blockX + size.blockX) &&
                block.y in position.blockY..(position.blockY + size.blockY) &&
                block.z in position.blockZ..(position.blockZ + size.blockZ)
    }

    private fun isSnowLand(world: World): Boolean {
        return try {
            world == WorldRegistry.SNOW_LAND
        } catch (_: UninitializedPropertyAccessException) {
            false
        }
    }
}
