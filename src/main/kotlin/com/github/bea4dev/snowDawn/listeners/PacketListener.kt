package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.snowDawn.craft.CRAFT_GUI_BUTTON
import com.github.bea4dev.snowDawn.craft.CraftGUIManager
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import org.bukkit.GameMode
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.entity.Player

fun registerPacketListener(player: Player) {
    val pipeline = (player as CraftPlayer).handle.connection.connection.channel.pipeline()
    pipeline.addBefore("packet_handler", "SD Packet Handler: ${player.name}", PacketListener(player))
}

class PacketListener(private val player: Player) : ChannelDuplexHandler() {
    override fun channelRead(ctx: ChannelHandlerContext?, packet: Any?) {
        if (packet is ServerboundContainerClickPacket && packet.containerId == 0 && packet.slotNum in 1..4) {
            MainThread.launch { CraftGUIManager.open(player) }
            player.updateInventory()
            return
        }

        super.channelRead(ctx, packet)
    }

    override fun write(ctx: ChannelHandlerContext?, packet: Any?, promise: ChannelPromise?) {
        when (packet) {
            is ClientboundContainerSetContentPacket -> {
                if (packet.containerId == 0 && packet.items.size >= 5 && player.gameMode == GameMode.SURVIVAL) {
                    val items = packet.items

                    val button = CraftItemStack.asNMSCopy(CRAFT_GUI_BUTTON)
                    items[1] = button
                    items[2] = button
                    items[3] = button
                    items[4] = button
                }
            }

            is ClientboundContainerSetSlotPacket -> {
                if (packet.containerId == 0 && packet.slot in 1..4 && player.gameMode == GameMode.SURVIVAL) {
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