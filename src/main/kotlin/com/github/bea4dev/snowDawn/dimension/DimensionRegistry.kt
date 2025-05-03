package com.github.bea4dev.snowDawn.dimension

import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.dimension.DimensionTypeContainer
import com.github.bea4dev.vanilla_source.api.dimension.DimensionTypeContainer.DimensionTypeContainerBuilder

object DimensionRegistry {
    lateinit var SNOW_LAND: Any
        private set
    lateinit var SECOND_MEGA_STRUCTURE: Any
        private set

    fun init() {
        val slDimensionTypeContainer = DimensionTypeContainerBuilder()
            .hasSkyLight(true)
            .ultraWarm(false)
            .effects(DimensionTypeContainer.EffectsType.THE_NETHER)
            .fixedTime(6000)
            .ambientLight(0.0f)
            .monsterSettings(DimensionTypeContainer.MonsterSettings(false, false, 15))
            .build()
        val nmsHandler = VanillaSourceAPI.getInstance().nmsHandler
        SNOW_LAND = nmsHandler.createDimensionType("snow_land_dimension", slDimensionTypeContainer)

        val smDimensionTypeContainer = DimensionTypeContainerBuilder()
            .hasSkyLight(true)
            .ultraWarm(false)
            .effects(DimensionTypeContainer.EffectsType.THE_NETHER)
            .fixedTime(6000)
            .ambientLight(0.1f)
            .monsterSettings(DimensionTypeContainer.MonsterSettings(false, false, 15))
            .build()
        SECOND_MEGA_STRUCTURE = nmsHandler.createDimensionType("second_mega_structure_dimension", smDimensionTypeContainer)
    }
}