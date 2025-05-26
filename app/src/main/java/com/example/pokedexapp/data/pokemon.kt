package com.example.pokedexapp.data

import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter


data class pokemonResponse(
    val results: List<PokemonItem>,
    val name: String
)

data class PokemonItem(
    val name: String,
    val url: String
)

data class PokemonDetail(
    val id: Int,
    val name: String,
    val stats: List<StatEntry>,
    val types: List<TypeEntry>,
    val abilities: List<AbilityEntry>,
    val height: Int,
    val weight: Int
) {
    fun sprite(): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
    }

    fun cry(): String {
        return "https://raw.githubusercontent.com/PokeAPI/cries/main/cries/pokemon/latest/$id.ogg"
    }
}


data class Image(
    val url: String
)

data class StatEntry(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: Stat
)

data class Stat(
    val name: String
)

data class TypeEntry(
    val type: Type
)

data class Type(
    val name: String
)

data class AbilityEntry(
    val ability: Ability
)

data class Ability(
    val name: String
)