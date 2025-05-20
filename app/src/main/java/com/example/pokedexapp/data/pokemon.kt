package com.example.pokedexapp.data

import com.google.gson.annotations.SerializedName

data class pokemonSearchResponse(
    val results: List<Pokemon>
)

data class Pokemon(
    val name: String,
    val url: String
)