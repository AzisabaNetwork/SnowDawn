package com.github.bea4dev.snowDawn.item.compass

import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.snowDawn.item.Item
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.snowDawn.world.WorldRegistry
import kotlinx.coroutines.time.delay
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Vibration
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.time.Duration

class Compass(
    id: String,
    material: Material,
    customModelData: Int,
    inactiveModelData: Int?,
    displayName: Text,
    lore: List<Text>
) : Item(
    id,
    material,
    customModelData,
    inactiveModelData,
    displayName,
    lore
) {
    fun onRightClick(player: Player) {
        val compassTask = PlayerCompassTaskManager[player] ?: return

        if (compassTask.delayTick > 0 || player.world != WorldRegistry.SNOW_LAND) {
            return
        }
        compassTask.delayTick = 20

        val sisetuPosition = Vector(1200, 255, 0)
        val playerPosition = player.location.toVector().add(Vector(0.0, 1.0, 0.0))

        val direction = sisetuPosition.clone().subtract(playerPosition).normalize().multiply(7)
        val destination = playerPosition.clone().add(direction)

        val vibration = Vibration(
            Vibration.Destination.BlockDestination(destination.toLocation(player.world)),
            10
        )
        player.world.spawnParticle(
            Particle.VIBRATION,
            playerPosition.toLocation(player.world),
            1,
            vibration
        )

        MainThread.launch {
            delay(Duration.ofMillis(55))

            player.playSound(
                player.location,
                org.bukkit.Sound.BLOCK_NOTE_BLOCK_HARP,
                Float.MAX_VALUE,
                2.0F
            )

            delay(Duration.ofMillis(55))

            player.playSound(
                player.location,
                org.bukkit.Sound.BLOCK_NOTE_BLOCK_HARP,
                Float.MAX_VALUE,
                2.0F
            )

            delay(Duration.ofMillis(55))

            player.playSound(
                player.location,
                org.bukkit.Sound.BLOCK_NOTE_BLOCK_HARP,
                Float.MAX_VALUE,
                2.0F
            )
        }
    }
}