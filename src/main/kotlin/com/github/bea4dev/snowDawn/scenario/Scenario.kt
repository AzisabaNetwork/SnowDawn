package com.github.bea4dev.snowDawn.scenario

import com.github.bea4dev.snowDawn.coroutine.launch
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.entity.tick.TickThread
import com.github.bea4dev.vanilla_source.api.text.TextBox
import com.google.gson.JsonParser
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.io.InputStreamReader
import java.net.URL
import java.time.Duration
import java.util.UUID


val SCENARIO_TICK_THREAD: TickThread = VanillaSourceAPI.getInstance().tickThreadPool.nextTickThread
const val DEFAULT_TEXT_BOX = "\uE201"
const val BLACK_SCREEN_TEXT = "\uE101"

abstract class Scenario {
    protected abstract suspend fun run(player: Player)

    fun start(player: Player) {
        SCENARIO_TICK_THREAD.launch {
            run(player)
        }
    }

    fun black(player: Player) {
        player.showTitle(
            Title.title(
                Component.text(BLACK_SCREEN_TEXT),
                Component.empty(),
                Title.Times.times(Duration.ZERO, Duration.ofHours(1), Duration.ZERO)
            )
        )
    }

    fun blackFeedOut(player: Player, feedOutMilli: Long) {
        player.showTitle(
            Title.title(
                Component.text(BLACK_SCREEN_TEXT),
                Component.empty(),
                Title.Times.times(Duration.ofMillis(feedOutMilli), Duration.ofHours(1), Duration.ZERO)
            )
        )
    }

    fun blackFeedIn(player: Player, feedInMilli: Long) {
        player.showTitle(
            Title.title(
                Component.text(BLACK_SCREEN_TEXT),
                Component.empty(),
                Title.Times.times(Duration.ZERO, Duration.ofMillis(1), Duration.ofMillis(feedInMilli))
            )
        )
    }

    fun clearBlack(player: Player) {
        player.resetTitle()
    }
}

fun TextBox.lowSound(): TextBox {
    this.setLowerSound(
        net.kyori.adventure.sound.Sound.sound(
            Sound.BLOCK_NOTE_BLOCK_BIT,
            net.kyori.adventure.sound.Sound.Source.NEUTRAL,
            Float.MAX_VALUE,
            0.9F
        )
    )
    this.setHigherSound(
        net.kyori.adventure.sound.Sound.sound(
            Sound.BLOCK_NOTE_BLOCK_BIT,
            net.kyori.adventure.sound.Sound.Source.NEUTRAL,
            Float.MAX_VALUE,
            0.95F
        )
    )
    return this
}

fun getPlayerSkin(uuid: UUID): Pair<String, String> {
    try {
        val url =
            URL(("https://sessionserver.mojang.com/session/minecraft/profile/$uuid") + "?unsigned=false")
        val reader = InputStreamReader(url.openStream())
        val property = JsonParser().parse(reader).asJsonObject.get("properties").asJsonArray.get(0).asJsonObject
        val texture = property["value"].asString
        val signature = property["signature"].asString
        return Pair(texture, signature)
    } catch (e: Exception) {
        e.printStackTrace()
        return Pair("", "")
    }
}