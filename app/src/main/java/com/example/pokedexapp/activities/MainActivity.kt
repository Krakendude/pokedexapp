package com.example.pokedexapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokedexapp.R
import com.example.pokedexapp.adapters.PokemonAdapter
import com.example.pokedexapp.data.PokemonItem
import com.example.pokedexapp.databinding.ActivityMainBinding
import com.example.pokedexapp.utils.pokeservice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var adapter: PokemonAdapter

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

        adapter = PokemonAdapter(pokemonListFiltered) { position ->
            val pokemon = pokemonListFiltered[position]
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("name", pokemon.name)
            intent.putExtra("url", pokemon.url)
            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        loadPokemonList()

        //searchPokemon("")
    }

    private fun loadPokemonList() {
        lifecycleScope.launch {
            val response = pokeservice.getInstance().getPokemonList()
            pokemonList = response.results
            adapter.updateItems(pokemonList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)

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
}

