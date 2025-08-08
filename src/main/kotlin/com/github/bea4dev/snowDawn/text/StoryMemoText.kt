package com.github.bea4dev.snowDawn.text

import com.github.bea4dev.snowDawn.save.ServerData
import com.github.bea4dev.snowDawn.world.WorldRegistry
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import org.bukkit.World
import java.text.MessageFormat
import java.util.Locale

object StoryMemoText {
    private val STORY_MEMO_TEXT = mapOf(
        WorldRegistry.SNOW_LAND to listOf("Test Title1\naaa\nbbb"),
        WorldRegistry.SECOND_MEGA_STRUCTURE to listOf("Test Title2\naaa\nbbb"),
    )

    init {
        val registry = TranslationRegistry.create(Key.key("snow_dawn", "story"))
        registry.defaultLocale(Locale.JAPAN)
        for ((world, texts) in STORY_MEMO_TEXT) {
            for ((index, text) in texts.withIndex()) {
                for ((lineIndex, line) in text.split("\n").withIndex()) {
                    registry.register("memo:${world.name}:${index}:${lineIndex}", Locale.JAPAN, MessageFormat(line))
                }
            }
        }
        val translator = GlobalTranslator.translator()
        translator.addSource(registry)
    }

    fun getNext(world: World): List<Component>? {
        val index = ServerData.storyTextIndex.computeIfAbsent(world.name) { 0 }

        val storyMemoText = STORY_MEMO_TEXT[world] ?: return null

        if (index >= storyMemoText.size) {
            return null
        }

        ServerData.storyTextIndex[world.name] = index + 1

        val lines = storyMemoText[index]
            .split("\n")
            .indices
            .map { line -> "memo:${world.name}:${index}:${line}" }
            .map { key -> Component.translatable(key) }

        return lines
    }
}