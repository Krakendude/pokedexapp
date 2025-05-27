package com.example.pokedexapp.activities

import android.health.connect.datatypes.units.Length
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pokedexapp.R
import com.example.pokedexapp.data.PokemonDetail
import com.example.pokedexapp.data.SpeciesResponse
import com.example.pokedexapp.databinding.ActivityDetailBinding
import com.example.pokedexapp.utils.pokeservice
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.reflect.typeOf

class DetailActivity : AppCompatActivity() {

    companion object {
        const val POKEMON_NAME = "POKEMON_NAME"
    }

    lateinit var binding: ActivityDetailBinding

    lateinit var pokemonDetail: PokemonDetail

    lateinit var speciesResponse: SpeciesResponse


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

        binding.navigationView.setOnItemSelectedListener { menuItem ->
            binding.contentBasicInfo.root.visibility = View.GONE
            binding.contentStats.root.visibility = View.GONE

            when (menuItem.itemId) {
                R.id.menu_basic_info -> binding.contentBasicInfo.root.visibility = View.VISIBLE
                R.id.menu_stats -> binding.contentStats.root.visibility = View.VISIBLE
            }
            true
        }

        binding.navigationView.selectedItemId = R.id.menu_basic_info
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
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

    fun loadData() {
        val genus = speciesResponse.genera.firstOrNull { it.language.name == "en" }?.genus
        supportActionBar?.title =
            pokemonDetail.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        supportActionBar?.subtitle = genus
        Picasso.get().load(pokemonDetail.sprite()).into(binding.avatarImageView)

        //basic info
        binding.contentBasicInfo.nameTextView.text = pokemonDetail.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        binding.contentBasicInfo.pokedexIDView.text = pokemonDetail.id.toString()
        binding.contentBasicInfo.heightTextView.text = pokemonDetail.height.toString()
        binding.contentBasicInfo.weightTextView.text = pokemonDetail.weight.toString()
        binding.contentBasicInfo.typesTextView.text = pokemonDetail.types.toString()

        //listas en basic info
        val typeNames = pokemonDetail.types.map {
            it.type.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }
        }
        binding.contentBasicInfo.typesTextView.text = typeNames.joinToString(", ")

        val abilitiesNames = pokemonDetail.abilities.map {
            it.ability.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }
        }
        binding.contentBasicInfo.abilityTextView.text = abilitiesNames.joinToString(", ")


        // stats
        pokemonDetail.stats.forEach { statEntry ->
            val statName = statEntry.stat.name.lowercase()
            val value = statEntry.baseStat

            when (statName) {
                "hp" -> binding.contentStats.hpProgress.progress = value
                "attack" -> binding.contentStats.attackProgress.progress = value
                "defense" -> binding.contentStats.defenseProgress.progress = value
                "speed" -> binding.contentStats.speedProgress.progress = value
                "special-attack" -> binding.contentStats.specialAttackProgress.progress = value
                "special-defense" -> binding.contentStats.specialDefenseProgress.progress = value
            }

            when (statName) {
                "hp" -> binding.contentStats.hpTextView.text = value.toString()
                "attack" -> binding.contentStats.attackTextView.text = value.toString()
                "defense" -> binding.contentStats.defenseTextView.text = value.toString()
                "special-attack" -> binding.contentStats.specialAttackTextView.text = value.toString()
                "special-defense" -> binding.contentStats.specialDefenseTextView.text = value.toString()
                "speed" -> binding.contentStats.speedTextView.text = value.toString()
            }
        }
        binding.contentBasicInfo.crybutton.setOnClickListener {
            val cryUrl = "https://play.pokemonshowdown.com/audio/cries/${pokemonDetail.name.lowercase()}.ogg"
            val mediaPlayer = MediaPlayer()

            try {
                mediaPlayer.setDataSource(cryUrl)
                mediaPlayer.setOnPreparedListener {
                    it.start()
                }
                mediaPlayer.prepareAsync()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "No se pudo reproducir el grito", Toast.LENGTH_SHORT).show()
            }
        }
    }
}