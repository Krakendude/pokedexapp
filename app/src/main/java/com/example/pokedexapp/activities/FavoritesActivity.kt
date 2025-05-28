package com.example.pokedexapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokedexapp.R
import com.example.pokedexapp.adapters.PokemonAdapter
import com.example.pokedexapp.data.favoritesManager
import com.example.pokedexapp.databinding.ActivityFavoritesBinding
import com.example.pokedexapp.utils.pokeservice
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    companion object {
        const val POKEMON_NAME = "POKEMON_NAME"
    }

    lateinit var binding: ActivityFavoritesBinding
    lateinit var adapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.title = "Favorites"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = PokemonAdapter(this, emptyList()) { position ->
            val pokemon = adapter.items[position]
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.POKEMON_NAME, pokemon.name)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    private fun loadFavoritePokemons(){

        val favoriteNames = favoritesManager.getFavorites(this)

        lifecycleScope.launch {
            val fullList = pokeservice.getInstance().getPokemonList().results
            val filtered = fullList.filter { it.name in favoriteNames }
            adapter.updateItems(filtered)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        super.onResume()
        val sharedPrefs = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val favorites = sharedPrefs.getStringSet("favorite_pokemons", setOf()) ?: setOf()

        // Filtra la lista global de Pokémon (debes tenerla cargada de alguna manera)
        lifecycleScope.launch {
            val allPokemons = pokeservice.getInstance().getPokemonList().results
            val favoriteList = allPokemons.filter { favorites.contains(it.name) }

            adapter.updateItems(favoriteList)
        }
    }
}
