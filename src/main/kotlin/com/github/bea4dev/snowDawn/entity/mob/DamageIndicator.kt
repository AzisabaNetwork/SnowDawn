package com.github.bea4dev.snowDawn.entity.mob

import com.github.bea4dev.vanilla_source.api.entity.TickBase
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.server.level.ServerEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.EntityType
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.TextDisplay
import org.bukkit.util.Vector
import kotlin.random.Random

class DamageIndicator(text: Component, private var position: Vector, private val world: World) : TickBase {
    private val entity = Display.TextDisplay(EntityType.TEXT_DISPLAY, (world as CraftWorld).handle)
    private val serverEntity = ServerEntity(entity.level() as ServerLevel, entity, 0, false, { arg -> }, mutableSetOf())
    private val velocity = Vector(Random.nextDouble() - 0.5, 0.5, Random.nextDouble() - 0.5).normalize().multiply(0.25)
    private var tick = 0
    private val lastTick = 20

    init {
        entity.setPosRaw(position.x, position.y, position.z)

        val bukkitEntity = entity.bukkitEntity as TextDisplay
        bukkitEntity.text(text)
        bukkitEntity.billboard = org.bukkit.entity.Display.Billboard.CENTER
        bukkitEntity.teleportDuration = 1
    }

    override fun tick() {
        tick++

        if (tick == 1) {
            val spawnPacket = ClientboundAddEntityPacket(entity, serverEntity)
            sendPacket(spawnPacket)

            val metadata = entity.entityData.packDirty()
            if (metadata != null) {
                sendPacket(ClientboundSetEntityDataPacket(entity.id, metadata))
            }
        }

        if (tick == lastTick) {
            val despawnPacket = ClientboundRemoveEntitiesPacket(entity.id)
            sendPacket(despawnPacket)
        }

        if (tick >= lastTick) {
            return
        }

        velocity.add(Vector(0.0, -0.06, 0.0))

        val newPosition = position.clone().add(velocity)
        val delta = newPosition.clone().subtract(position)

        val movePacket = ClientboundMoveEntityPacket.PosRot(
            entity.id,
            (delta.getX() * 4096).toInt().toShort(),
            (delta.getY() * 4096).toInt().toShort(),
            (delta.getZ() * 4096).toInt().toShort(),
            ((entity.xRot * 256.0f) / 360.0f).toInt().toByte(),
            ((entity.yRot * 256.0f) / 360.0f).toInt().toByte(),
            false
        )
        sendPacket(movePacket)

        position = newPosition
    }

    private fun sendPacket(packet: Packet<*>) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.world == world) {
                (player as CraftPlayer).handle.connection.sendPacket(packet)
            }
        }
    }

    override fun shouldRemove(): Boolean {
        return tick > lastTick
    }
}