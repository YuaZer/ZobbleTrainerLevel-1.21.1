package io.github.yuazer.zobbletrainerlevel.utils

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Pokemon
import top.maplex.arim.Arim
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import kotlin.math.roundToInt

object ScriptUtils {
    fun evalToInt(expression: String, variables: Map<String, Any> = emptyMap()): Int {
        // 将变量替换到表达式中
        var parsedExpression = expression
        variables.forEach { (key, value) ->
            parsedExpression = parsedExpression.replace(key, value.toString(), ignoreCase = true)
        }
        // 使用 Arim 解析表达式
        return when (val result = Arim.fixedCalculator.evaluate(parsedExpression)) {
            else -> result.roundToInt()
        }
    }


    fun evalToInt(expression: String, pokemon: Pokemon): Int {
        return evalToInt(expression, pokemonPapiToMap(pokemon))
    }

    fun evalToBoolean(expression: String, variables: Map<String, Any> = emptyMap()): Boolean {
        var parsedExpression = expression
        variables.forEach { (key, value) ->
            parsedExpression = parsedExpression.replace(key, value.toString(), ignoreCase = true)
        }

        val arimVars = variables.mapValues { (_, value) ->
            when (value) {
                is String -> value.toDoubleOrNull() ?: value
                else -> value
            }
        }
        return try {
            val result = Arim.evaluator.evaluate(parsedExpression, arimVars)
            result as? Boolean ?: false
        } catch (e: Exception) {
            println("布尔表达式计算失败: \"$expression\"\n替换后: \"$parsedExpression\"\n变量: $arimVars")
            false
        }
    }


    fun evalToBoolean(expression: String, pokemon: Pokemon): Boolean {
        return evalToBoolean(expression, pokemonPapiToMap(pokemon))
    }

    fun evalListToBoolean(expression: List<String>, variables: Map<String, Any> = emptyMap()): Boolean {
        return expression.all { evalToBoolean(it, variables) }
    }

    fun evalListToBoolean(expression: List<String>, pokemon: Pokemon): Boolean {
        return expression.all { evalToBoolean(it, pokemonPapiToMap(pokemon)) }
    }

    fun pokemonPapiToMap(pokemon: Pokemon): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["%pokemon_name%"] = pokemon.species.name.replace(" ","#")
        map["%pokemon_level%"] = pokemon.level
        map["%pokemon_exp%"] = pokemon.experience
        map["%pokemon_exp_to_next_level%"] = pokemon.getExperienceToNextLevel()
        map["%pokemon_exp_percentage_to_next_level%"] =
            (pokemon.experience.toDouble() / pokemon.getExperienceToNextLevel().toDouble()).roundToInt() * 100

        // IVs
        map["%pokemon_ivs_attack%"] = pokemon.ivs[Stats.ATTACK] ?: 0
        map["%pokemon_ivs_defence%"] = pokemon.ivs[Stats.DEFENCE] ?: 0
        map["%pokemon_ivs_hp%"] = pokemon.ivs[Stats.HP] ?: 0
        map["%pokemon_ivs_speed%"] = pokemon.ivs[Stats.SPEED] ?: 0
        map["%pokemon_ivs_special_attack%"] = pokemon.ivs[Stats.SPECIAL_ATTACK] ?: 0
        map["%pokemon_ivs_special_defence%"] = pokemon.ivs[Stats.SPECIAL_DEFENCE] ?: 0
        // IV总和
        var ivsTotal = 0
        pokemon.ivs.forEach { (_, v) ->
            ivsTotal += v
        }
        map["%pokemon_ivs_total%"] = ivsTotal

        // EVs
        map["%pokemon_evs_attack%"] = pokemon.evs[Stats.ATTACK] ?: 0
        map["%pokemon_evs_defence%"] = pokemon.evs[Stats.DEFENCE] ?: 0
        map["%pokemon_evs_hp%"] = pokemon.evs[Stats.HP] ?: 0
        map["%pokemon_evs_speed%"] = pokemon.evs[Stats.SPEED] ?: 0
        map["%pokemon_evs_special_attack%"] = pokemon.evs[Stats.SPECIAL_ATTACK] ?: 0
        map["%pokemon_evs_special_defence%"] = pokemon.evs[Stats.SPECIAL_DEFENCE] ?: 0
        //努力值总和
        var evsTotal = 0
        pokemon.evs.forEach { (_, v) ->
            evsTotal += v
        }
        map["%pokemon_evs_total%"] = evsTotal

        // 属性类型
        map["%pokemon_type_1%"] = pokemon.types.elementAtOrNull(0)?.name ?: "NONE"
        map["%pokemon_type_2%"] = pokemon.types.elementAtOrNull(1)?.name ?: "NONE"

        return map
    }
}
