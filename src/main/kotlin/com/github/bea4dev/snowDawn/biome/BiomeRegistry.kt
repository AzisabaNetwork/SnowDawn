package com.github.bea4dev.snowDawn.biome

import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.biome.BiomeDataContainer
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Sound

object BiomeRegistry {
    lateinit var SNOW_LAND: Any
        private set
    lateinit var NO_BGM: Any
        private set
    lateinit var SECOND_MEGA_STRUCTURE: Any
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
        snowLand.music = Sound.MUSIC_NETHER_CRIMSON_FOREST.key().asString()
        SNOW_LAND = nmsHandler.createBiome("snow_land", snowLand)

        val noBGM = BiomeDataContainer()
        nmsHandler.setDefaultBiomeData(noBGM)
        noBGM.music = "minecraft:none"
        NO_BGM = nmsHandler.createBiome("no_bgm", noBGM)

        val secondMegaStructure = BiomeDataContainer()
        nmsHandler.setDefaultBiomeData(secondMegaStructure)
        secondMegaStructure.fogColorRGB = Color.GRAY.asRGB()
        secondMegaStructure.skyColorRGB = Color.GRAY.asRGB()
        secondMegaStructure.temperature = 5.0F
        secondMegaStructure.music = Sound.MUSIC_END.key().asString()
        secondMegaStructure.particle = Particle.WHITE_ASH
        secondMegaStructure.particleAmount = 0.02F
        SECOND_MEGA_STRUCTURE = nmsHandler.createBiome("second_mega_structure", secondMegaStructure)
    }
}