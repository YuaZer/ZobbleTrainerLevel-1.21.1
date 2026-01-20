package io.github.yuazer.zobbletrainerlevel.hook

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.util.getPlayer
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object OtherHook : PlaceholderExpansion {
    override val identifier: String = "zobbletrainerlevelother"
    override fun onPlaceholderRequest(player: Player?, args: String): String {
        return player?.name?.let {
            val key = args.split("<>")[0]
            val value = args.split("<>")[1]
            when(key.lowercase()){
                "party" -> Cobblemon.storage.getParty(player.uniqueId.getPlayer()!!).any { it.species.name.equals(value,true) }.toString()
                "pc" -> Cobblemon.storage.getPC(player.uniqueId.getPlayer()!!).any { it.species.name.equals(value,true) }.toString()
                "all" -> (Cobblemon.storage.getParty(player.uniqueId.getPlayer()!!).any { it.species.name.equals(value,true) }||Cobblemon.storage.getPC(player.uniqueId.getPlayer()!!).any { it.species.name.equals(value,true) }).toString()
                else -> "null"
            }
        } ?: "playernull"
    }
}