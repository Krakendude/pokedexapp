package com.example.pokedexapp.activities

import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pokedexapp.R
import com.example.pokedexapp.data.PokemonDetail
import com.example.pokedexapp.databinding.ActivityDetailBinding
import com.example.pokedexapp.utils.pokeservice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    companion object {
        const val POKEMON_NAME = "POKEMON_NAME"
    }

    lateinit var binding: ActivityDetailBinding

    lateinit var pokemonDetail: PokemonDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val name = intent.getStringExtra(POKEMON_NAME)!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getPokemonByName(name)

    }

    fun getPokemonByName(name: String) {
        // Llamada en un hilo secundario
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = pokeservice.getInstance()
                pokemonDetail = service.getPokemonByName(name)


                // Volvemos al hilo principal
                CoroutineScope(Dispatchers.Main).launch {
                    loadData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadData(){
        Toast.makeText(this, "{${pokemonDetail.name}}", Toast.LENGTH_SHORT).show()
    }
}