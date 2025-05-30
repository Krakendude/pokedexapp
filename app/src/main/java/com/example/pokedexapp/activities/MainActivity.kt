package com.example.pokedexapp.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokedexapp.R
import com.example.pokedexapp.adapters.PokemonAdapter
import com.example.pokedexapp.data.PokemonItem
import com.example.pokedexapp.data.pokemonResponse
import com.example.pokedexapp.databinding.ActivityMainBinding
import com.example.pokedexapp.utils.pokeservice
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var adapter: PokemonAdapter

    lateinit var sharedPreferences: SharedPreferences

    lateinit var context: Context

    var pokemonList: List<PokemonItem> = emptyList()
    var pokemonListFiltered: List<PokemonItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        adapter = PokemonAdapter(this, pokemonListFiltered) { position ->
            val pokemon = pokemonListFiltered[position]
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.POKEMON_NAME, pokemon.name)
            intent.putExtra(FavoritesActivity.POKEMON_NAME, pokemon.name)
            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        loadPokemonList()

        //searchPokemon("")
    }

    private fun loadPokemonList() {
        lifecycleScope.launch {
            val response = pokeservice.getInstance().getPokemonList()
            pokemonList = response.results
            pokemonListFiltered = pokemonList
            adapter.updateItems(pokemonList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        supportActionBar?.setStackedBackgroundDrawable(R.color.pokeball_red.toDrawable())

        val menuItem = menu.findItem(R.id.menu_search)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchPokemon(newText)
                return true
            }

        })

        return true
    }

    fun searchPokemon(query: String) {
        pokemonListFiltered = pokemonList.filter { it.name.contains(query, true) }
        adapter.updateItems(pokemonListFiltered)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_favorites -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




}

