package com.github.bea4dev.snowDawn.save

import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object PlayerDataRegistry {
    private val map = ConcurrentHashMap<UUID, PlayerData>()

    operator fun get(player: Player): PlayerData {
        return map.computeIfAbsent(player.uniqueId) { PlayerData(player) }
    }
}

class PlayerData(player: Player) {
    var respawnLocation = player.location.clone()
}