package com.github.bea4dev.snowDawn.craft

import com.github.bea4dev.snowDawn.item.Item
import com.github.bea4dev.snowDawn.item.getItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

private val globalItemCraftMap = mutableMapOf<Item, CraftRecipe>()

class CraftRecipe(
    val requiredItems: List<Item>,
    val craftItem: Item,
) {
    init {
        globalItemCraftMap[craftItem] = this
    }

    fun canCraft(player: Player): Boolean {
        root@ for (requiredItem in requiredItems) {
            for (itemStack in player.inventory.iterator()) {
                val item = itemStack.getItem() ?: continue

                if (requiredItem == item) {
                    continue@root
                }
            }

            val craftRecipe = globalItemCraftMap[requiredItem]
            if (craftRecipe != null && craftRecipe.canCraft(player)) {
                continue@root
            }

            return false
        }
        return true
    }

    fun createCraftIconFor(player: Player): ItemStack {
        return if (this.canCraft(player)) {
            craftItem.createItemStack()
        } else {
            craftItem.createInactiveItemStack()
        }
    }
}