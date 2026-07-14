package com.github.bea4dev.snowDawn.generator.structure

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.item.getItem
import de.articdive.jnoise.pipeline.JNoise
import org.bukkit.NamespacedKey
import org.bukkit.block.Chest
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.floor
import kotlin.math.min

val STRUCTURE_CHEST_REQUIREMENT_KEY by lazy {
    NamespacedKey(SnowDawn.plugin, "structure_chest_requirement")
}
val STRUCTURE_CHEST_UNLOCKED_KEY by lazy {
    NamespacedKey(SnowDawn.plugin, "structure_chest_unlocked")
}

enum class StructureChestRequirement(val id: String) {
    IRON_INGOT("iron_ingot"),
    DIAMOND("diamond"),
}

class ItemChest(
    private val table: List<List<ItemStack>>,
    private val requirement: StructureChestRequirement? = null,
) {
    fun getItems(random: Double): List<ItemStack> {
        val index = random * table.size
        return table[min(index.toInt(), table.size - 1)]
    }

    fun populate(x: Int, y: Int, z: Int, limitedRegion: LimitedRegion, noise: JNoise) {
        val noiseValue = noise.evaluateNoise(x.toDouble(), y.toDouble(), z.toDouble())
        val random = (noiseValue + 1.0) / 2.0
        val selectedItems = getItems(random)
        var state = limitedRegion.getBlockState(x, y, z) as Chest

        if (requirement != null && selectedItems.any { item -> item.getItem() == ItemRegistry.STORY_MEMO }) {
            state.persistentDataContainer.set(
                STRUCTURE_CHEST_REQUIREMENT_KEY,
                PersistentDataType.STRING,
                requirement.id,
            )
            limitedRegion.setBlockState(x, y, z, state)

            // setBlockState invalidates the previous inventory snapshot.
            state = limitedRegion.getBlockState(x, y, z) as Chest
        }

        for (item in selectedItems) {
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
