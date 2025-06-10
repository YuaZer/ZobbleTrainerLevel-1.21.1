package io.github.yuazer.zobbletrainerlevel.hook

import io.github.yuazer.zobbletrainerlevel.api.LevelApi
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object LevelHook :PlaceholderExpansion{
    override val identifier: String = "zobbletrainerlevel"
    //获取变量
    override fun onPlaceholderRequest(player: Player?, args: String): String {
        return player?.name?.let { name ->
            val data = LevelApi.getPlayerLevelContainer(name)
            when (args.lowercase()) {
                "level" -> data.level.toString()
                "exp" -> data.experience.toString()
                "exptonextlevel" -> data.getExperienceForNextLevel().toString()
                "need" -> (data.getExperiencePercentageToLevelUp()-data.experience).toString()
                else -> "argsError"
            }
        } ?: "playernull"
    }

}