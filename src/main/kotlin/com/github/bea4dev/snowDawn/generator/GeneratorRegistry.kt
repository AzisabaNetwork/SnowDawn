package com.github.bea4dev.snowDawn.generator

object GeneratorRegistry {
    val VOID = VoidGenerator()
    val SNOW_LAND: SnowLand get() = _SNOW_LAND.value

    private lateinit var _SNOW_LAND: Lazy<SnowLand>

    fun init(seed: Long) {
        _SNOW_LAND = lazy { SnowLand(seed) }
    }
}