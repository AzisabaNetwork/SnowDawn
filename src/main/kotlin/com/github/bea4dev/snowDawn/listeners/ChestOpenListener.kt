package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.generator.structure.STRUCTURE_CHEST_REQUIREMENT_KEY
import com.github.bea4dev.snowDawn.generator.structure.STRUCTURE_CHEST_UNLOCKED_KEY
import com.github.bea4dev.snowDawn.generator.structure.StructureChestRequirement
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.item.getItem
import com.github.bea4dev.snowDawn.text.StoryMemoText
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.snowDawn.world.WorldRegistry
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.block.Chest
import org.bukkit.block.DoubleChest
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ChestOpenListener : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onLockedStructureChestInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK || event.hand != EquipmentSlot.HAND) {
            return
        }

        val player = event.player
        if (player.world != WorldRegistry.SNOW_LAND) {
            return
        }

        val chest = event.clickedBlock?.state as? Chest ?: return
        if (!tryUnlockStructureChest(player, getChests(chest.inventory))) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onLockedStructureChestOpen(event: InventoryOpenEvent) {
        val player = event.player as? Player ?: return
        if (player.world != WorldRegistry.SNOW_LAND) {
            return
        }

        if (!tryUnlockStructureChest(player, getChests(event.inventory))) {
            event.isCancelled = true
        }
    }

    private fun tryUnlockStructureChest(player: Player, chests: List<Chest>): Boolean {
        val requirement = getStructureChestRequirement(chests) ?: return true
        if (chests.any { chest -> chest.isStructureChestUnlocked() }) {
            unlockStructureChests(chests)
            return true
        }
        val requiredMaterial = when (requirement) {
            StructureChestRequirement.IRON_INGOT -> Material.IRON_INGOT
            StructureChestRequirement.DIAMOND -> Material.DIAMOND
        }
        val mainHandItem = player.inventory.itemInMainHand

        if (mainHandItem.type != requiredMaterial) {
            player.playSound(
                player.location,
                Sound.BLOCK_WOODEN_DOOR_CLOSE,
                1.0F,
                1.35F,
            )
            player.sendMessage(
                Component.translatable(
                    Text.MESSAGE_STRUCTURE_CHEST_ITEM_REQUIRED.toString(),
                    Component.translatable(requiredMaterial.translationKey()),
                )
            )
            return false
        }

        if (mainHandItem.amount <= 1) {
            player.inventory.setItemInMainHand(ItemStack(Material.AIR))
        } else {
            mainHandItem.amount -= 1
        }

        unlockStructureChests(chests)
        return true
    }

    private fun unlockStructureChests(chests: List<Chest>) {
        chests.forEach { chest ->
            if (chest.isStructureChestUnlocked()) {
                return@forEach
            }
            chest.persistentDataContainer.set(
                STRUCTURE_CHEST_UNLOCKED_KEY,
                PersistentDataType.BYTE,
                1,
            )
            chest.update()
        }
    }

    @EventHandler
    fun onLockedStructureChestBreak(event: BlockBreakEvent) {
        if (isLockedStructureChest(event.block)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onLockedStructureChestExplode(event: BlockExplodeEvent) {
        event.blockList().removeIf(::isLockedStructureChest)
    }

    @EventHandler
    fun onLockedStructureChestExplode(event: EntityExplodeEvent) {
        event.blockList().removeIf(::isLockedStructureChest)
    }

    @EventHandler(ignoreCancelled = true)
    fun onChestOpen(event: InventoryOpenEvent) {
        val player = event.player
        val inventory = event.inventory

        for (index in inventory.contents.indices) {
            val item = inventory.getItem(index)

            if (item == null) {
                continue
            }

            val itemInstance = item.getItem() ?: continue

            if (itemInstance != ItemRegistry.STORY_MEMO) {
                continue
            }

            // replace empty memo
            val memo = StoryMemoText.getNext(player.world)

            if (memo == null) {
                inventory.setItem(index, null)
                continue
            }

            val title = (memo.lines.getOrNull(0) ?: Component.empty())
                .color(NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false)
            val lore = memo.lines.drop(1)
                .map { component -> component.color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false) }

            val newItem = ItemStack(Material.PAPER, 1)
            val newMeta = newItem.itemMeta
            newMeta.displayName(title)
            newMeta.lore(lore)
            newMeta.persistentDataContainer.set(
                NamespacedKey(SnowDawn.plugin, StoryMemoListener.MEMO_WORLD_KEY),
                PersistentDataType.STRING,
                memo.worldName,
            )
            newMeta.persistentDataContainer.set(
                NamespacedKey(SnowDawn.plugin, StoryMemoListener.MEMO_INDEX_KEY),
                PersistentDataType.INTEGER,
                memo.index,
            )
            newItem.itemMeta = newMeta

            inventory.setItem(index, newItem)
        }
    }

    private fun getChests(inventory: Inventory): List<Chest> {
        return when (val holder = inventory.holder) {
            is Chest -> listOf(holder)
            is DoubleChest -> listOfNotNull(holder.leftSide as? Chest, holder.rightSide as? Chest)
            else -> emptyList()
        }
    }

    private fun getStructureChestRequirement(chests: List<Chest>): StructureChestRequirement? {
        val containsStoryMemo = chests.any { chest ->
            chest.inventory.contents.any { item -> item?.getItem() == ItemRegistry.STORY_MEMO }
        }
        if (!containsStoryMemo) {
            return null
        }

        return chests.firstNotNullOfOrNull { chest ->
            val id = chest.persistentDataContainer.get(
                STRUCTURE_CHEST_REQUIREMENT_KEY,
                PersistentDataType.STRING,
            ) ?: return@firstNotNullOfOrNull null
            StructureChestRequirement.entries.firstOrNull { requirement -> requirement.id == id }
        }
    }

    private fun Chest.isStructureChestUnlocked(): Boolean {
        return persistentDataContainer.has(
            STRUCTURE_CHEST_UNLOCKED_KEY,
            PersistentDataType.BYTE,
        )
    }

    private fun isLockedStructureChest(block: Block): Boolean {
        if (block.world != WorldRegistry.SNOW_LAND) {
            return false
        }

        val chest = block.state as? Chest ?: return false
        val chests = getChests(chest.inventory)
        return getStructureChestRequirement(chests) != null &&
            chests.none { structureChest -> structureChest.isStructureChestUnlocked() }
    }
}
