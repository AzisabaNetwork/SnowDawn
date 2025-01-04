package com.github.bea4dev.snowDawn.dimension

import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.dimension.DimensionTypeContainer
import com.github.bea4dev.vanilla_source.api.dimension.DimensionTypeContainer.DimensionTypeContainerBuilder

object DimensionRegistry {
    lateinit var SNOW_LAND_DIMENSION: Any
        private set

    fun init() {
        val dimensionTypeContainer = DimensionTypeContainerBuilder()
            .hasSkyLight(true)
            .ultraWarm(false)
            .effects(DimensionTypeContainer.EffectsType.THE_NETHER)
            .fixedTime(6000)
            .ambientLight(0.0f)
            .monsterSettings(DimensionTypeContainer.MonsterSettings(false, false, 15))
            .build()
        val nmsHandler = VanillaSourceAPI.getInstance().nmsHandler
        SNOW_LAND_DIMENSION = nmsHandler.createDimensionType("snow_land_dimension", dimensionTypeContainer)
    }
}