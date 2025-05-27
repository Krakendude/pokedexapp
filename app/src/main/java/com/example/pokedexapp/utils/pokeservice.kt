package com.example.pokedexapp.utils

import com.example.pokedexapp.data.PokemonDetail
import com.example.pokedexapp.data.PokemonItem
import com.example.pokedexapp.data.SpeciesResponse
import com.example.pokedexapp.data.pokemonResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface pokeservice {
    @GET("pokemon?limit=100000&offset=0")
    suspend fun getPokemonList(): pokemonResponse


    @GET("pokemon/{name}")
    suspend fun searchPokemonByName(@Path("name") name: String): pokemonResponse


    @GET("pokemon/{id}")
    suspend fun getPokemonById(@Path("id") id: Int): PokemonDetail

    @GET("pokemon/{name}")
    suspend fun getPokemonByName(@Path("name") name: String): PokemonDetail

    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecies(@Path("name") name: String): Response<SpeciesResponse>

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