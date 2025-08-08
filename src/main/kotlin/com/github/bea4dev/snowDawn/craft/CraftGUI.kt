package com.github.bea4dev.snowDawn.craft

import com.github.bea4dev.snowDawn.item.getItem
import com.github.bea4dev.snowDawn.text.Text
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

internal object CraftGUIManager {
    private val playerCraftUIMap = mutableMapOf<Player, CraftGUI>()

    fun onPlayerJoin(player: Player) {
        val gui = CraftGUI(player)
        gui.update()

        playerCraftUIMap[player] = gui
    }

    fun onPlayerQuit(player: Player) {
        playerCraftUIMap.remove(player)
    }

    fun update(player: Player) {
        playerCraftUIMap[player]?.update()
    }

    fun open(player: Player) {
        playerCraftUIMap[player]?.inventory?.let { player.openInventory(it) }
    }
}

internal val CRAFT_GUI_BUTTON = ItemStack(Material.CRAFTING_TABLE).also { item ->
    val meta = item.itemMeta
    meta.displayName(
        Component.translatable(Text.CRAFT_UI.toString()).color(NamedTextColor.GOLD).decorate(
            TextDecoration.UNDERLINED
        )
    )
    meta.lore(listOf(Component.translatable(Text.CRAFT_UI_CLICK_TO_OPEN.toString()).color(NamedTextColor.GRAY)))
    item.itemMeta = meta
}

class CraftGUI(private val player: Player) : InventoryHolder {
    private val inventory = Bukkit.createInventory(this, 54, Component.translatable(Text.CRAFT_UI.toString()))

    override fun getInventory(): Inventory {
        return inventory
    }

    fun update() {
        if (!player.isOnline) {
            return
        }

        for (recipe in CraftRecipeRegistry.RECIPES.withIndex()) {
            val index = recipe.index
            val recipe = recipe.value

            inventory.setItem(index, recipe.createCraftIconFor(player))
        }

        inventory.setItem(53, ItemStack(Material.BARRIER).also { item ->
            val meta = item.itemMeta
            meta.displayName(Component.translatable(Text.CLOSE.toString()).color(NamedTextColor.RED))
            item.itemMeta = meta
        })

        for (i in 0 until 54) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, ItemStack(Material.BLACK_STAINED_GLASS_PANE).also { item ->
                    val meta = item.itemMeta
                    meta.displayName(Component.text("").color(NamedTextColor.WHITE))
                    item.itemMeta = meta
                })
            }
        }
    }

    fun onClick(event: InventoryClickEvent) {
        if (event.slot == 53) {
            player.closeInventory()
            return
        }

        val recipe = CraftRecipeRegistry.RECIPES.getOrNull(event.slot) ?: return

        if (!recipe.canCraft(player)) {
            player.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.MASTER, 1.0F, 2.0F))
            return
        }

        val inventory = player.inventory
        root@ for (required in recipe.requiredItems) {
            var amount = required.amount
            for (slot in 0 until inventory.size) {
                val itemStack = inventory.getItem(slot) ?: continue
                val item = itemStack.getItem() ?: continue

                if (required.item != item) {
                    continue
                }

                if (itemStack.amount >= amount) {
                    itemStack.amount -= amount
                    if (itemStack.amount <= 0) {
                        inventory.setItem(slot, null)
                    } else {
                        inventory.setItem(slot, itemStack)
                    }
                    continue@root
                } else {
                    amount -= itemStack.amount
                    inventory.setItem(slot, null)
                }
            }
        }

        val craftItem = recipe.craftItem.createItemStack().also { item -> item.amount = recipe.craftItemAmount }
        val dropItem = inventory.addItem(craftItem).values

        dropItem.forEach { item -> player.world.dropItemNaturally(player.eyeLocation, item) }

        player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ITEM_PICKUP, Sound.Source.PLAYER, 1.0F, 1.2F))

        update()
    }

}