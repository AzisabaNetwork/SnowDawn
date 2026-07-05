package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.snowDawn.craft.CRAFT_GUI_BUTTON
import com.github.bea4dev.snowDawn.craft.CraftGUIManager
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.core.NonNullList
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import net.minecraft.world.item.ItemStack as NMSItemStack
import org.bukkit.GameMode
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.entity.Player

fun registerPacketListener(player: Player) {
    val pipeline = (player as CraftPlayer).handle.connection.connection.channel.pipeline()
    pipeline.addBefore("packet_handler", "SD Packet Handler: ${player.name}", PacketListener(player))
}

fun sendCraftingSlotButtons(player: Player) {
    if (player.gameMode != GameMode.SURVIVAL && player.gameMode != GameMode.ADVENTURE) {
        return
    }

    val serverPlayer = (player as CraftPlayer).handle
    val menu = serverPlayer.inventoryMenu
    val sourceItems = menu.getItems()
    val items = NonNullList.withSize(sourceItems.size, NMSItemStack.EMPTY)

    for (index in sourceItems.indices) {
        items[index] = sourceItems[index].copy()
    }

    val button = CraftItemStack.asNMSCopy(CRAFT_GUI_BUTTON)
    for (slot in 1..4) {
        if (slot < items.size) {
            items[slot] = button.copy()
        }
    }

    serverPlayer.connection.sendPacket(
        ClientboundContainerSetContentPacket(
            menu.containerId,
            menu.getStateId(),
            items,
            menu.getCarried().copy()
        )
    )
}

class PacketListener(private val player: Player) : ChannelDuplexHandler() {
    override fun channelRead(ctx: ChannelHandlerContext?, packet: Any?) {
        if (packet is ServerboundContainerClickPacket && packet.containerId == 0 && packet.slotNum in 1..4) {
            MainThread.launch {
                CraftGUIManager.update(player)
                CraftGUIManager.open(player)
            }
            player.updateInventory()
            return
        }

        super.channelRead(ctx, packet)
    }

    override fun write(ctx: ChannelHandlerContext?, packet: Any?, promise: ChannelPromise?) {
        when (packet) {
            is ClientboundContainerSetContentPacket -> {
                if (packet.containerId == 0 && packet.items.size >= 5 && (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE)) {
                    val items = packet.items

                    val button = CraftItemStack.asNMSCopy(CRAFT_GUI_BUTTON)
                    items[1] = button
                    items[2] = button
                    items[3] = button
                    items[4] = button
                }
            }

            is ClientboundContainerSetSlotPacket -> {
                if (packet.containerId == 0 && packet.slot in 1..4 && (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE)) {
                    return super.write(
                        ctx,
                        ClientboundContainerSetSlotPacket(
                            packet.containerId,
                            packet.stateId,
                            packet.slot,
                            CraftItemStack.asNMSCopy(CRAFT_GUI_BUTTON)
                        ),
                        promise
                    )
                }
            }
        }

        super.write(ctx, packet, promise)
    }
}
