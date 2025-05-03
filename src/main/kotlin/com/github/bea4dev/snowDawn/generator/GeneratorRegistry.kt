package com.github.bea4dev.snowDawn.generator

object GeneratorRegistry {
    val VOID = VoidGenerator()
    val SNOW_LAND: SnowLand get() = _SNOW_LAND.value
    val SECOND_MEGA_STRUCTURE: SecondMegaStructure get() = _SECOND_MEGA_STRUCTURE.value

    private lateinit var _SNOW_LAND: Lazy<SnowLand>
    private lateinit var _SECOND_MEGA_STRUCTURE: Lazy<SecondMegaStructure>

    fun init(seed: Long) {
        _SNOW_LAND = lazy { SnowLand(seed) }
        _SECOND_MEGA_STRUCTURE = lazy { SecondMegaStructure(seed) }
    }
}