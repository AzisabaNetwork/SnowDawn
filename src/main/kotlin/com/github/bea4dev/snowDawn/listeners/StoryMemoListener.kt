package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.item.getItem
import com.github.bea4dev.snowDawn.save.ServerData
import com.github.bea4dev.snowDawn.text.StoryMemoText
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.snowDawn.toast.ToastKind
import com.github.bea4dev.snowDawn.toast.ToastNotification
import com.github.bea4dev.snowDawn.toast.sendToast
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

data class StoryMemoId(val worldName: String, val index: Int)

object StoryMemoUnlockEventRegistry {
    private val handlers = mutableMapOf<StoryMemoId, MutableList<suspend (Player) -> Unit>>()

    @Synchronized
    fun register(worldName: String, index: Int, handler: suspend (Player) -> Unit) {
        handlers.computeIfAbsent(StoryMemoId(worldName, index)) { mutableListOf() }.add(handler)
    }

    fun register(world: World, index: Int, handler: suspend (Player) -> Unit) {
        register(world.name, index, handler)
    }

    suspend fun fire(player: Player, memo: StoryMemoId) {
        getHandlers(memo).forEach { handler -> handler(player) }
    }

    @Synchronized
    private fun getHandlers(memo: StoryMemoId): List<suspend (Player) -> Unit> {
        return handlers[memo].orEmpty().toList()
    }
}

class StoryMemoListener : Listener {
    companion object {
        const val MEMO_WORLD_KEY = "story_memo_world"
        const val MEMO_INDEX_KEY = "story_memo_index"

        private val pendingUnlocks = ConcurrentHashMap<UUID, MutableSet<StoryMemoId>>()

        fun takePendingUnlocks(player: Player): Set<StoryMemoId> {
            return pendingUnlocks.remove(player.uniqueId).orEmpty()
        }
    }

    @EventHandler
    fun onMemoClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val holder = event.view.topInventory.holder

        if (holder is StoryMemoInventory) {
            event.isCancelled = true
            return
        }

        val memoItem = event.currentItem ?: return
        if (memoItem.type.isAir || !memoItem.hasItemMeta()) {
            return
        }
        val meta = memoItem.itemMeta ?: return
        val container = meta.persistentDataContainer
        val worldName = container.get(
            NamespacedKey(SnowDawn.plugin, MEMO_WORLD_KEY),
            PersistentDataType.STRING,
        ) ?: return
        val index = container.get(
            NamespacedKey(SnowDawn.plugin, MEMO_INDEX_KEY),
            PersistentDataType.INTEGER,
        ) ?: return

        if (ServerData.unlockStoryMemo(worldName, index)) {
            pendingUnlocks.computeIfAbsent(player.uniqueId) { ConcurrentHashMap.newKeySet() }
                .add(StoryMemoId(worldName, index))

            player.playSound(
                player.location,
                Sound.ITEM_BOOK_PAGE_TURN,
                Float.MAX_VALUE,
                1.5F,
            )
            player.sendToast(
                ToastNotification(
                    Component.translatable(Text.STORY_UNLOCKED.toString())
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.BOLD),
                    memoItem.clone(),
                    ToastKind.GOAL,
                )
            )
        }
    }

    @EventHandler
    fun onCompassUse(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND ||
            (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) ||
            event.item?.getItem() != ItemRegistry.COMPASS
        ) {
            return
        }

        event.isCancelled = true
        val player = event.player
        player.openInventory(StoryMemoInventory(player).getInventory())
    }
}

private class StoryMemoInventory(player: Player) : InventoryHolder {
    private val memoInventory: Inventory = Bukkit.createInventory(
        this,
        27,
        Component.translatable(Text.STORY_MEMO_LIST.toString()),
    )

    init {
        StoryMemoText.getUnlocked(player.world).forEachIndexed { slot, memo ->
            val item = ItemStack(Material.PAPER)
            val meta = item.itemMeta
            meta.displayName(
                (memo.lines.firstOrNull() ?: Component.empty())
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false)
            )
            meta.lore(
                memo.lines.drop(1).map { line ->
                    line.color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                }
            )
            item.itemMeta = meta
            memoInventory.setItem(slot, item)
        }
    }

    override fun getInventory(): Inventory = memoInventory
}
