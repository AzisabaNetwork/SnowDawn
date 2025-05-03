package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.biome.BiomeRegistry
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent

internal class ChunkListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onChunkLoad(event: ChunkLoadEvent) {
        if (event.world.name == "snow_land") {
            VanillaSourceAPI.getInstance().nmsHandler.setBiomeForChunk(event.chunk, BiomeRegistry.SNOW_LAND)
        }

        if (event.world.name == "prologue") {
            VanillaSourceAPI.getInstance().nmsHandler.setBiomeForChunk(event.chunk, BiomeRegistry.NO_BGM)
        }

        if (event.world.name == "second_mega_structure") {
            VanillaSourceAPI.getInstance().nmsHandler.setBiomeForChunk(event.chunk, BiomeRegistry.SECOND_MEGA_STRUCTURE)
        }
    }
}