package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.item.getItem
import com.github.bea4dev.snowDawn.item.weapon.BluePrint
import com.github.bea4dev.snowDawn.save.ServerData
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.snowDawn.toast.ToastKind
import com.github.bea4dev.snowDawn.toast.ToastNotification
import com.github.bea4dev.snowDawn.toast.sendToast
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class BluePrintListener : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val itemStack = event.currentItem ?: return
        val item = itemStack.getItem() as? BluePrint ?: return

        var hasUncraftable = false
        for (item in item.printed) {
            if (!ServerData.craftableItems.contains(item.id)) {
                ServerData.craftableItems.add(item.id)
                hasUncraftable = true
            }
        }

        if (hasUncraftable) {
            player.playSound(
                player.location,
                Sound.ENTITY_ARROW_HIT_PLAYER,
                Float.MAX_VALUE,
                1.5F
            )

            player.sendToast(
                ToastNotification(
                    Component.translatable(Text.RECIPE_UNLOCKED.toString())
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.BOLD),
                    item.printed[0].createItemStack(),
                    ToastKind.GOAL
                )
            )
        } else {
            player.playSound(
                player.location,
                Sound.ENTITY_ITEM_PICKUP,
                Float.MAX_VALUE,
                2.0F
            )
        }

        event.currentItem = null
    }
}