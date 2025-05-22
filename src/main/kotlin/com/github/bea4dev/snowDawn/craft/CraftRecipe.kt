package com.github.bea4dev.snowDawn.craft

import com.github.bea4dev.snowDawn.item.Item
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.item.getItem
import com.github.bea4dev.snowDawn.text.Text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object CraftRecipeRegistry {
    val RECIPES = listOf(
        CraftRecipe(listOf(RequiredItem(ItemRegistry.SCRAP)), ItemRegistry.SCRAP_PIPE)
    )
}

private val globalItemCraftMap = mutableMapOf<Item, CraftRecipe>()

class CraftRecipe(
    val requiredItems: List<RequiredItem>, val craftItem: Item, val craftItemAmount: Int = 1
) {
    init {
        globalItemCraftMap[craftItem] = this
    }

    fun canCraft(player: Player): Boolean {
        root@ for (requiredItem in requiredItems) {
            var amount = 0
            for (itemStack in player.inventory.iterator()) {
                val item = itemStack?.getItem() ?: continue

                if (requiredItem.item == item) {
                    amount += itemStack.amount

                    if (amount >= requiredItem.amount) {
                        continue@root
                    }
                }
            }
            return false
        }
        return true
    }

    fun createCraftIconFor(player: Player): ItemStack {
        val canCraft = this.canCraft(player)
        val item = if (canCraft) {
            craftItem.createItemStack().also { item -> item.amount = craftItemAmount }
        } else {
            craftItem.createInactiveItemStack()
        }

        val meta = item.itemMeta
        val lore = meta?.lore()
        if (lore != null) {
            val newLore = mutableListOf<Component>()

            if (canCraft) {
                newLore.add(Component.translatable(Text.CRAFT_REQUIRED.toString()).color(NamedTextColor.GRAY))
            } else {
                newLore.add(Component.translatable(Text.CANNOT_CRAFT.toString()).color(NamedTextColor.RED))
            }

            for (required in requiredItems) {
                var has = false
                var amount = 0
                for (itemStack in player.inventory.iterator()) {
                    val item = itemStack?.getItem() ?: continue

                    if (required.item == item) {
                        amount++

                        if (amount >= required.amount) {
                            has = true
                            break
                        }
                    }
                }

                val ok = if (has) {
                    Component.text(" ✔ ").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                } else {
                    Component.text(" ✘ ").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                }
                newLore.add(
                    ok.append(
                        Component.text(required.item.fontIcon ?: "")
                    ).append(
                        Component.translatable(required.item.displayName.toString()).color(NamedTextColor.GRAY)
                    ).append(
                        Component.text(" x${required.amount}").color(NamedTextColor.GRAY)
                    )
                )
            }

            newLore.add(Component.empty())
            newLore.addAll(lore)

            meta.lore(newLore)
        }
        item.itemMeta = meta

        return item
    }
}

class RequiredItem(val item: Item, val amount: Int = 1)