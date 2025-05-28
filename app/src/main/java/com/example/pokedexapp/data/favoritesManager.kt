package com.example.pokedexapp.data

import android.content.Context

object favoritesManager {
    private const val PREF_NAME = "favorites_prefs"
    private const val KEY_FAVORITES = "favorite_pokemons"

    fun getFavorites(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun toggleFavorite(context: Context, pokemonName: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val current = prefs.getStringSet(KEY_FAVORITES, mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        if (current.contains(pokemonName)) {
            current.remove(pokemonName)
        } else {
            current.add(pokemonName)
        }

        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
    }

    fun isFavorite(context: Context, pokemonName: String): Boolean {
        return getFavorites(context).contains(pokemonName)
    }
}