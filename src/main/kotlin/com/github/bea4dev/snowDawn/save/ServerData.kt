package com.github.bea4dev.snowDawn.save

import com.github.bea4dev.snowDawn.item.ItemRegistry
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object ServerData {
    private val file = File("server_saves.yml")

    val storyTextIndex = mutableMapOf<String, Int>()
    val craftableItems = mutableListOf(
        ItemRegistry.SCRAP_PIPE.id,
        ItemRegistry.SCRAP_PICKAXE.id,
        ItemRegistry.STONE_PICKAXE.id,
        ItemRegistry.TORCH.id,
        ItemRegistry.FURNACE.id,
        ItemRegistry.CAMPFIRE.id,
        ItemRegistry.FLINT_AND_STEEL.id,
    )

    fun load() {
        // 初回はデフォルトで保存してから読む
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            save()
        }

        val yml = YamlConfiguration.loadConfiguration(file)

        // storyTextIndex 読み込み（存在すれば上書き、無ければ現状維持）
        yml.getConfigurationSection("storyTextIndex")?.let { sec ->
            storyTextIndex.clear()
            for (key in sec.getKeys(false)) {
                val n = when (val v = sec.get(key)) {
                    is Number -> v.toInt()
                    is String -> v.toIntOrNull() ?: 0
                    else -> 0
                }
                storyTextIndex[key] = n
            }
        }

        // craftableItems 読み込み（存在すれば上書き、無ければ現状維持＝デフォルトのまま）
        if (yml.contains("craftableItems")) {
            craftableItems.clear()
            craftableItems.addAll(yml.getStringList("craftableItems"))
        }
    }

    fun save() {
        val yml = YamlConfiguration()

        // Map はセクションとして保存
        if (storyTextIndex.isNotEmpty()) {
            yml.createSection(
                "storyTextIndex",
                storyTextIndex.mapValues { it.value as Any } // Map<String, Any> にする
            )
        } else {
            // 空でもキーを残したい場合はコメントアウト解除
            // yml.createSection("storyTextIndex")
        }

        // リストはそのまま保存
        yml.set("craftableItems", craftableItems)

        runCatching {
            file.parentFile?.mkdirs()
            yml.save(file)
        }.onFailure { it.printStackTrace() }
    }
}