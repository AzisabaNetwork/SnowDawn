package com.github.bea4dev.snowDawn.scenario.script

import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.snowDawn.coroutine.play
import com.github.bea4dev.snowDawn.item.weapon.WeaponTaskManager
import com.github.bea4dev.snowDawn.scenario.DEFAULT_TEXT_BOX
import com.github.bea4dev.snowDawn.scenario.Scenario
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.text.TextBox
import kotlinx.coroutines.time.delay
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.Duration

object EnterMegaStructure : Scenario() {
    override suspend fun run(player: Player) {
        val dist = Location(WorldRegistry.SECOND_MEGA_STRUCTURE, 2.5, 306.0, 2.5)
        dist.yaw = 90.0F

        WeaponTaskManager[player]?.enableBar?.set(false)

        MainThread.sync {
            player.gameMode = GameMode.SPECTATOR
        }.await()

        black(player)

        player.playSound(
            Sound.sound(
                org.bukkit.Sound.BLOCK_IRON_DOOR_OPEN,
                Sound.Source.MASTER,
                Float.MAX_VALUE,
                1.0F
            )
        )

        delay(Duration.ofSeconds(1))

        player.playSound(
            Sound.sound(
                org.bukkit.Sound.BLOCK_IRON_DOOR_CLOSE,
                Sound.Source.MASTER,
                Float.MAX_VALUE,
                1.0F
            )
        )

        blackFeedIn(player, 1000)

        MainThread.sync {
            player.gameMode = GameMode.SURVIVAL
            player.teleport(dist)
        }.await()

        delay(Duration.ofSeconds(1))

        player.showTitle(
            Title.title(
                Component.translatable(Text.SECOND_MEGA_STRUCTURE_TITLE.toString())
                    .color(NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD),
                Component.translatable(Text.SECOND_MEGA_STRUCTURE_SUB_TITLE.toString())
                    .color(NamedTextColor.GRAY),
                Title.Times.times(
                    Duration.ofSeconds(2),
                    Duration.ofSeconds(2),
                    Duration.ofSeconds(2)
                )
            )
        )

        delay(Duration.ofSeconds(6))

        TextBox(player, DEFAULT_TEXT_BOX, Text.BENE[player], 1, Text.SECOND_MEGA_STRUCTURE_MESSAGE_0[player, player.name])
            .play()
            .await()

        TextBox(player, DEFAULT_TEXT_BOX, Text.BENE[player], 1, Text.SECOND_MEGA_STRUCTURE_MESSAGE_1[player, player.name])
            .play()
            .await()

        WeaponTaskManager[player]?.enableBar?.set(true)
    }
}