package io.github.yuazer.zobbletrainerlevel.events

import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent
import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.api.events.pokemon.PokemonGainedEvent
import com.cobblemon.mod.common.api.events.pokemon.PokemonSeenEvent
import com.cobblemon.mod.common.api.events.pokemon.healing.PokemonHealedEvent
import com.cobblemon.mod.common.api.events.storage.ReleasePokemonEvent
import com.cobblemon.mod.common.pokemon.Pokemon
import io.github.yuazer.zobbletrainerlevel.ZobbleTrainerLevel
import io.github.yuazer.zobbletrainerlevel.api.LevelApi
import io.github.yuazer.zobbletrainerlevel.utils.ScriptUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.platform.util.asLangText

object CobbleEventHandler {

    fun registerCobbleEvent() {
        CobblemonEvents.LEVEL_UP_EVENT.subscribe { event ->
            onLevelUp(event)
        }
        CobblemonEvents.BATTLE_STARTED_PRE.subscribe { event ->
            onBattleStartPre(event)
        }
        CobblemonEvents.POKEMON_CAPTURED.subscribe { event ->
            onCapture(event)
        }
        CobblemonEvents.BATTLE_FAINTED.subscribe { event ->
            if (event.battle.isPvW && event.battle.actors.any {
                    it.type == ActorType.PLAYER
                }) {
                onBeatWild_Lose(event)
            }
        }
        CobblemonEvents.POKEMON_GAINED.subscribe { event ->
            onPokemonGained(event)
        }
        CobblemonEvents.POKEMON_SEEN.subscribe { event ->
            onPokemonSeen(event)
        }
        CobblemonEvents.POKEMON_HEALED.subscribe { event ->
            onPokemonHealed(event)
        }
        CobblemonEvents.POKEMON_RELEASED_EVENT_POST.subscribe { event ->
            onPokemonReleased(event)
        }
    }

    fun onCapture(event: PokemonCapturedEvent) {
        val pokemon = event.pokemon
        val player = Bukkit.getPlayer(pokemon.getOwnerUUID() ?: return)
        if (player != null) {
            grantExpFromSection("Capture", pokemon, player)
        }
    }

    fun onBattleStartPre(event: BattleStartedEvent.Pre) {
        val players = event.battle.actors
            .filter { it.type == ActorType.PLAYER }
        playersLoop@ for (player in players) {
            val pokemonList = player.pokemonList
            val pokemon = player.pokemonList.firstOrNull() ?: continue@playersLoop
            val bukkitPlayer =
                Bukkit.getPlayer(pokemon.originalPokemon.getOwnerUUID() ?: continue@playersLoop) ?: continue@playersLoop
            val canBattle =
                pokemonList.all { it.originalPokemon.level <= LevelApi.getPlayerLevelContainer(bukkitPlayer.name).level }
            val noLevelNoBattle = ZobbleTrainerLevel.options.getBoolean("NoLevel_NoBattle")
            if (noLevelNoBattle && !canBattle) {
                bukkitPlayer.sendMessage(
                    bukkitPlayer.asLangText("pokemon-high-level")
                        .replace("%pokemon%", pokemon.getName().string)
                )
                event.cancel()
                return
            }
        }
    }

    fun onBeatWild_Lose(event: BattleFaintedEvent) {
        if (!event.battle.isPvW) return
        playerLoop@ for (serverPlayer in event.battle.players) {
            val ownerUUID = serverPlayer.uuid
            val player = Bukkit.getPlayer(ownerUUID) ?: continue@playerLoop

            val pokemon = event.killed.originalPokemon
            grantExpFromSection("BeatWild", pokemon, player)
        }
    }


    fun onBeatWild(event: BattleVictoryEvent) {
        event.battle.activePokemon.forEach {
            println(it.battlePokemon?.originalPokemon?.getDisplayName()?.string)
        }
//        event.losers.forEach {
//            it.activePokemon.forEach { ap ->
//                println(if (ap.battlePokemon == null) "battle null" else "loser:${ap.battlePokemon!!.originalPokemon.getDisplayName().string}")
//            }
//        }
//        event.winners.forEach {
//            it.activePokemon.forEach { ap ->
//                println(if (ap.battlePokemon == null) "battle null" else "winner:${ap.battlePokemon!!.originalPokemon.getDisplayName().string}")
//            }
//        }

//        loop@ for (winner in event.winners) {
//            val battlePokemon = winner.activePokemon.first().battlePokemon ?: continue@loop
//            if (event.losers.first().activePokemon.first().battlePokemon == null) {
//                println("loser battle pokemon null")
//                continue@loop
//            }
//            val targetPokemon = event.losers.first().activePokemon.first().battlePokemon ?: continue@loop
//            val pokemon = battlePokemon.originalPokemon
//            val targetOriginalPokemon = targetPokemon.originalPokemon
//            val ownerUUID = pokemon.getOwnerUUID() ?: continue@loop
//            val player = Bukkit.getPlayer(ownerUUID) ?: continue@loop
//
//            val specialSection = ZobbleTrainerLevel.options.getConfigurationSection("BeatWild.special")
//            if (specialSection != null) {
//                for (special in specialSection.getKeys(false)) {
//                    val conditions =
//                        ZobbleTrainerLevel.options.getStringList("BeatWild.special.$special.conditions")
//                    if (ScriptUtils.evalListToBoolean(conditions, targetOriginalPokemon)) {
//                        val experience = ScriptUtils.evalToInt(
//                            ZobbleTrainerLevel.options.getString("BeatWild.special.$special.exp")!!,
//                            targetOriginalPokemon
//                        )
//                        LevelApi.getPlayerLevelContainer(player.name).addExperience(experience)
//                        //退出当前 winner，处理下一个
//                        continue@loop
//                    }
//                }
//            }
//            // 如果没有触发任何特殊条件，则给予默认经验
//            val defaultExp = ZobbleTrainerLevel.options.getString("BeatWild.default")
//            if (defaultExp != null) {
//                val experience = ScriptUtils.evalToInt(defaultExp, targetOriginalPokemon)
//                LevelApi.getPlayerLevelContainer(player.name).addExperience(experience)
//            }
//        }
    }


    fun onLevelUp(event: LevelUpEvent) {
        val pokemon = event.pokemon
        if (pokemon.getOwnerUUID() == null) {
            return
        }
        val player = Bukkit.getPlayer(pokemon.getOwnerUUID()!!)
        if (player != null) {
            grantExpFromSection("LevelUp", pokemon, player)
        }
    }

    fun onPokemonGained(event: PokemonGainedEvent) {
        val player = Bukkit.getPlayer(event.playerId) ?: return
        grantExpFromSection("PokemonGained", event.pokemon, player)
    }

    fun onPokemonSeen(event: PokemonSeenEvent) {
        val player = Bukkit.getPlayer(event.playerId) ?: return
        grantExpFromSection("PokemonSeen", event.pokemon, player)
    }

    fun onPokemonHealed(event: PokemonHealedEvent) {
        val pokemon = event.pokemon
        val player = Bukkit.getPlayer(pokemon.getOwnerUUID() ?: return) ?: return
        grantExpFromSection("PokemonHealed", pokemon, player)
    }

    fun onPokemonReleased(event: ReleasePokemonEvent.Post) {
        val player = Bukkit.getPlayer(event.player.uuid) ?: return
        grantExpFromSection("PokemonReleased", event.pokemon, player)
    }

    private fun grantExpFromSection(sectionKey: String, pokemon: Pokemon, player: Player) {
        val specialSection = ZobbleTrainerLevel.options.getConfigurationSection("$sectionKey.special")
        if (specialSection != null) {
            for (special in specialSection.getKeys(false)) {
                val conditions =
                    ZobbleTrainerLevel.options.getStringList("$sectionKey.special.$special.conditions")
                if (ScriptUtils.evalListToBoolean(conditions, pokemon)) {
                    val expString = ZobbleTrainerLevel.options.getString("$sectionKey.special.$special.exp") ?: continue
                    val experience = ScriptUtils.evalToInt(expString, pokemon)
                    LevelApi.getPlayerLevelContainer(player.name).addExperience(experience)
                    return
                }
            }
        }

        val defaultExp = ZobbleTrainerLevel.options.getString("$sectionKey.default") ?: return
        val experience = ScriptUtils.evalToInt(defaultExp, pokemon)
        LevelApi.getPlayerLevelContainer(player.name).addExperience(experience)
    }
}
