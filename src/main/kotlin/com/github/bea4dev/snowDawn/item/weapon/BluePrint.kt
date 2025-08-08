package com.github.bea4dev.snowDawn.item.weapon

import com.github.bea4dev.snowDawn.item.Item
import com.github.bea4dev.snowDawn.text.Text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class BluePrint(
    id: String,
    material: Material,
    customModelData: Int,
    inactiveModelData: Int?,
    displayName: Text,
    lore: List<Text>,
    val printed: List<Item>,
) : Item(
    id,
    material,
    customModelData,
    inactiveModelData,
    displayName,
    lore
) {
    override fun createItemStack(): ItemStack {
        return super.createItemStack().also { item ->
            val meta = item.itemMeta!!
            val name = meta.displayName() ?: Component.empty()
            val lore = meta.lore() ?: mutableListOf()

            lore.add(
                Component.translatable(Text.ITEM_BLUE_PRINT_CLICK.toString())
                    .color(NamedTextColor.WHITE)
                    .decorate(TextDecoration.UNDERLINED)
            )
            meta.displayName(name.color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD))
            meta.lore(lore)

            item.itemMeta = meta
        }
    }
}