package com.github.bea4dev.snowDawn.item.weapon

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import java.time.Duration

private const val PROGRESS_BAR = 0xE000

class ProgressBar(private val player: Player) {
    private var lastProgress = 0

    fun setProgress(progress: Double) {
        var progressInt = (progress * 10.0).toInt()
        if (progressInt > 10) {
            progressInt = 10
        }
        if (progressInt < 0) {
            progressInt = 0
        }
        if (progressInt == lastProgress) {
            return
        }

        val char = Char(PROGRESS_BAR or progressInt)

        val display = Component.text("$char").color(TextColor.fromHexString("#4e5c24"))
        player.showTitle(
            Title.title(
                Component.empty(),
                display,
                Title.Times.times(Duration.ZERO, Duration.ofMillis(400), Duration.ofMillis(50))
            )
        )

        lastProgress = progressInt
    }

}