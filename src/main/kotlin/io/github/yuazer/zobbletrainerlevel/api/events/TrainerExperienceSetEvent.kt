package io.github.yuazer.zobbletrainerlevel.api.events

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

data class TrainerExperienceSetEvent(
    val player: Player,
    val experience: Int
): BukkitProxyEvent()
