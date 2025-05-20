package com.example.pokedexapp.utils

import com.example.pokedexapp.data.Pokemon
import com.example.pokedexapp.data.pokemonSearchResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface pokeservice {@GET("pokemon?limit=100000&offset=0")

suspend fun getPokemonList(): Response<pokemonSearchResponse>

    companion object {
        fun getInstance(): pokeservice {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(pokeservice::class.java)
        }
    }
}