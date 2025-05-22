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