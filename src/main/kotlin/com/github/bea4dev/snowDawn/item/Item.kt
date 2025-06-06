package com.github.bea4dev.snowDawn.item

import com.github.bea4dev.snowDawn.item.weapon.Weapon
import com.github.bea4dev.snowDawn.text.Text
import de.tr7zw.changeme.nbtapi.NBT
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.function.Function

object ItemRegistry {
    internal val itemIdMap = HashMap<String, Item>()

    val SCRAP = Item("scrap", Material.STRING, 1, 2, Text.ITEM_SCRAP, listOf(Text.ITEM_SCRAP_LORE_0))
    val SCRAP_PIPE = Weapon(
        "scrap_pipe", Material.SHEARS, 1, 2, Text.ITEM_SCRAP_PIPE, listOf(
            Text.ITEM_SCRAP_PIPE_LORE_0,
            Text.ITEM_SCRAP_PIPE_LORE_1,
            Text.ITEM_SCRAP_PIPE_LORE_2,
            Text.ITEM_SCRAP_PIPE_LORE_3,
            Text.ITEM_SCRAP_PIPE_LORE_4
        ), 10, 5.0, 4.0F
    )
    val ICE = Item("ice", Material.ICE, 0, 0, Text.ITEM_ICE, listOf())
    val COAL = Item("coal", Material.COAL, 0, 0, Text.ITEM_COAL, listOf())
    val TORCH = Item("torch", Material.TORCH, 0, 1, Text.ITEM_TORCH, listOf())
    val STONE = Item("stone", Material.COBBLESTONE, 0, 1, Text.ITEM_STONE, listOf())
    val SCRAP_PICKAXE = Item("scrap_pickaxe", Material.WOODEN_PICKAXE, 1, 2, Text.ITEM_SCRAP_PICKAXE, listOf())
    val STONE_PICKAXE = Item("stone_pickaxe", Material.STONE_PICKAXE, 0, 1, Text.ITEM_STONE_PICKAXE, listOf())
    val FURNACE = Item("furnace", Material.FURNACE, 0, 1, Text.ITEM_FURNACE, listOf())
    val COPPER_INGOT = Item("copper_ingot", Material.COPPER_INGOT, 0, 1, Text.ITEM_COPPER_INGOT, listOf())
    val COPPER_HELMET = Item("copper_helmet", Material.CHAINMAIL_HELMET, 0, 1, Text.ITEM_COPPER_HELMET, listOf())
    val COPPER_CHESTPLATE =
        Item("copper_chestplate", Material.CHAINMAIL_CHESTPLATE, 0, 1, Text.ITEM_COPPER_CHEST_PLATE, listOf())
    val COPPER_LEGGINGS =
        Item("copper_leggings", Material.CHAINMAIL_LEGGINGS, 0, 1, Text.ITEM_COPPER_LEGGINGS, listOf())
    val COPPER_BOOTS = Item("copper_boots", Material.CHAINMAIL_BOOTS, 0, 1, Text.ITEM_COPPER_BOOTS, listOf())

    operator fun get(id: String): Item? {
        return itemIdMap[id]
    }
}

const val ITEM_ID_TAG = "item_id"

open class Item(
    val id: String,
    val material: Material,
    val customModelData: Int,
    val inactiveModelData: Int?,
    val displayName: Text,
    val lore: List<Text>,
    val fontIcon: String? = null,
) {
    init {
        ItemRegistry.itemIdMap[id] = this
    }

    open fun createItemStack(): ItemStack {
        return ItemStack(material).also { item ->
            val meta = item.itemMeta
            meta.setCustomModelData(customModelData)

            meta.displayName(Component.translatable(displayName.toString()))
            meta.lore(lore.map { line ->
                Component.translatable(line.toString()).color(NamedTextColor.GRAY).decoration(
                    TextDecoration.ITALIC, false
                )
            })
            item.itemMeta = meta
        }.also { item ->
            NBT.modify(item) { nbt -> nbt.setString(ITEM_ID_TAG, id) }
        }
    }

    open fun createInactiveItemStack(): ItemStack {
        return createItemStack().also { item ->
            val meta = item.itemMeta
            meta.setCustomModelData(inactiveModelData)
            item.itemMeta = meta
        }
    }
}

fun ItemStack.getID(): String? {
    if (this.isEmpty) {
        return null
    }
    return NBT.get(this, Function { nbt -> nbt.getOrNull(ITEM_ID_TAG, String::class.java) })
}

fun ItemStack.getItem(): Item? {
    return this.getID()?.let { id -> ItemRegistry[id] }
}