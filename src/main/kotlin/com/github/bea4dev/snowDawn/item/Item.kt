package com.github.bea4dev.snowDawn.item

import de.tr7zw.changeme.nbtapi.NBT
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.function.Function

object ItemRegistry {
    internal val itemIdMap = HashMap<String, Item>()

    operator fun get(id: String): Item? {
        return itemIdMap[id]
    }
}

const val ITEM_ID_TAG = "item_id"

open class Item(val id: String, val material: Material, val customModelData: Int) {
    init {
        ItemRegistry.itemIdMap.set(id, this)
    }

    open fun createItemStack(): ItemStack {
        return ItemStack(material).also { item ->
            val meta = item.itemMeta
            meta.setCustomModelData(customModelData)
            item.itemMeta = meta
        }.also { item ->
            NBT.modify(item) { nbt -> nbt.setString(ITEM_ID_TAG, id) }
        }
    }
}

fun ItemStack.getID(): String? {
    if (this.isEmpty) { return null }
    return NBT.get(this, Function { nbt -> nbt.getOrNull(ITEM_ID_TAG, String::class.java) })
}

fun ItemStack.getItem(): Item? {
    return this.getID()?.let { id -> ItemRegistry[id] }
}