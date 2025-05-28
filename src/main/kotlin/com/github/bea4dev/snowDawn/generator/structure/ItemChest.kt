package com.github.bea4dev.snowDawn.generator.structure

import de.articdive.jnoise.pipeline.JNoise
import org.bukkit.block.Chest
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.inventory.ItemStack
import kotlin.math.floor
import kotlin.math.min

class ItemChest(private val table: List<List<ItemStack>>): BlockPopulator() {
    fun getItems(random: Double): List<ItemStack> {
        val index = random * table.size
        return table[min(index.toInt(), table.size - 1)]
    }

    fun populate(x: Int, y: Int, z: Int, limitedRegion: LimitedRegion, noise: JNoise) {
        val state = limitedRegion.getBlockState(x, y, z) as Chest

        val noiseValue = noise.evaluateNoise(x.toDouble(), y.toDouble(), z.toDouble())
        val random = (noiseValue + 1.0) / 2.0

        for (item in getItems(random)) {
            state.blockInventory.addItem(item)
        }

        // shuffle
        val items: Array<ItemStack?> = state.blockInventory.contents

        for (i in items.size - 1 downTo 1) {
            val r: Double = noise.evaluateNoise(x.toDouble() + i, y.toDouble() + i, z.toDouble() + i)
            val j = floor(((r + 1.0) / 2.0) * (i + 1)).toInt()

            val tmp = items[i]
            items[i] = items[j]
            items[j] = tmp
        }

        state.blockInventory.contents = items
    }
}