package com.github.bea4dev.snowDawn.biome

import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.biome.BiomeDataContainer
import org.bukkit.Color
import org.bukkit.Sound

object BiomeRegistry {
    lateinit var SNOW_LAND: Any
        private set

    fun init() {
        val nmsHandler = VanillaSourceAPI.getInstance().nmsHandler

        val snowLand = BiomeDataContainer()
        nmsHandler.setDefaultBiomeData(snowLand)
        snowLand.fogColorRGB = Color.WHITE.asRGB()
        snowLand.skyColorRGB = Color.WHITE.asRGB()
        snowLand.grassBlockColorRGB = Color.WHITE.asRGB()
        snowLand.foliageColorRGB = Color.WHITE.asRGB()
        snowLand.temperature = -100.0F
        snowLand.music = Sound.MUSIC_NETHER_CRIMSON_FOREST
        SNOW_LAND = nmsHandler.createBiome("snow_land", snowLand)
    }
}