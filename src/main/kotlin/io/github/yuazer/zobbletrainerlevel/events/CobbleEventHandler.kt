package io.github.yuazer.zobbletrainerlevel.events

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleEvent
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent
import io.github.yuazer.zobbletrainerlevel.ZobbleTrainerLevel
import io.github.yuazer.zobbletrainerlevel.api.LevelApi
import io.github.yuazer.zobbletrainerlevel.utils.ScriptUtils
import org.bukkit.Bukkit
import taboolib.platform.BukkitPlugin

object CobbleEventHandler {

    fun registerCobbleEvent() {
        CobblemonEvents.LEVEL_UP_EVENT.subscribe { event ->
            onLevelUp(event)
        }
        CobblemonEvents.BATTLE_VICTORY.subscribe { event ->
            //野外对战
            if (event.battle.isPvW && event.winners.any {
                    it.type == ActorType.PLAYER
                }) {
                onBeatWild(event)
            }
        }
    }

    fun onBeatWild(event: BattleVictoryEvent) {
        loop@for (winner in event.winners) {
            val battlePokemon = winner.activePokemon[0].battlePokemon ?: continue@loop
            val pokemon = battlePokemon.originalPokemon
            val ownerUUID = pokemon.getOwnerUUID() ?: continue@loop
            val player = Bukkit.getPlayer(ownerUUID) ?: continue@loop

            val specialSection = ZobbleTrainerLevel.options.getConfigurationSection("BeatWild.special")
            if (specialSection != null) {
                for (special in specialSection.getKeys(false)) {
                    val conditions =
                        ZobbleTrainerLevel.options.getStringList("BeatWild.special.$special.conditions")
                    if (ScriptUtils.evalListToBoolean(conditions, pokemon)) {
                        val experience = ScriptUtils.evalToInt(
                            ZobbleTrainerLevel.options.getString("BeatWild.special.$special.exp")!!,
                            pokemon
                        )
                        LevelApi.getPlayerLevelContainer(player.name).addExperience(experience)
                        //退出当前 winner，处理下一个
                        continue@loop
                    }
                }
            }
            // 如果没有触发任何特殊条件，则给予默认经验
            val defaultExp = ZobbleTrainerLevel.options.getString("BeatWild.default")
            if (defaultExp != null) {
                val experience = ScriptUtils.evalToInt(defaultExp, pokemon)
                LevelApi.getPlayerLevelContainer(player.name).addExperience(experience)
            }
        }
    }




    fun onLevelUp(event: LevelUpEvent) {
        val pokemon = event.pokemon
        if (pokemon.getOwnerUUID() == null) {
            return
        }
        val player = Bukkit.getPlayer(pokemon.getOwnerUUID()!!)
        ZobbleTrainerLevel.options.getConfigurationSection("LevelUp.special")?.getKeys(false)?.forEach { special ->
            val conditions = ZobbleTrainerLevel.options.getStringList("LevelUp.special.$special.conditions")
            if (ScriptUtils.evalListToBoolean(conditions, pokemon)) {
                val experience = ScriptUtils.evalToInt(
                    ZobbleTrainerLevel.options.getString("LevelUp.special.$special.exp")!!,
                    pokemon
                )
                LevelApi.getPlayerLevelContainer(player!!.name).addExperience(experience)
                return
            }
        }
        val defaultExp = ZobbleTrainerLevel.options.getString("LevelUp.default")
        if (defaultExp != null) {
            val experience = ScriptUtils.evalToInt(defaultExp, pokemon)
            LevelApi.getPlayerLevelContainer(player!!.name).addExperience(experience)
        }
    }
}