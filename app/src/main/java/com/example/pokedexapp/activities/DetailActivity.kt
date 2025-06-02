package com.example.pokedexapp.activities

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.health.connect.datatypes.units.Length
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokedexapp.R
import com.example.pokedexapp.adapters.MoveAdapter
import com.example.pokedexapp.data.ChainLink
import com.example.pokedexapp.data.MoveDetail
import com.example.pokedexapp.data.MoveSlot
import com.example.pokedexapp.data.PokemonDetail
import com.example.pokedexapp.data.SpeciesResponse
import com.example.pokedexapp.databinding.ActivityDetailBinding
import com.example.pokedexapp.utils.pokeservice
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            binding.contentMoves.root.visibility = View.GONE

            when (menuItem.itemId) {
                R.id.menu_basic_info -> binding.contentBasicInfo.root.visibility = View.VISIBLE
                R.id.menu_stats -> binding.contentStats.root.visibility = View.VISIBLE
                R.id.menu_moves -> binding.contentMoves.root.visibility = View.VISIBLE
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
                    loadData(pokemonName = String.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    fun loadData(pokemonName: String) {
        supportActionBar?.title =
            pokemonDetail.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        loadSpeciesSubtitle(pokemonDetail.name)
        Picasso.get().load(pokemonDetail.sprite()).into(binding.avatarImageView)


        //basic info
        binding.contentBasicInfo.nameTextView.text = pokemonDetail.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        binding.contentBasicInfo.pokedexIDView.text = pokemonDetail.id.toString()
        binding.contentBasicInfo.heightTextView.text = pokemonDetail.height.toString()
        binding.contentBasicInfo.weightTextView.text = pokemonDetail.weight.toString()

        //listas en basic info
        val typeNames = pokemonDetail.types.map {
            it.type.name
        }

        val typeImageViews = listOf(
            binding.contentBasicInfo.firstTypeImageView,
            binding.contentBasicInfo.secondTypeImageView
        )

        showTypeIcons(typeNames, typeImageViews)

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
    // cargar evoluciones
        binding.contentBasicInfo.evolutionTextView.text = loadEvolutionChain(pokemonDetail.name).toString()

        // cargar movimientos
        loadMoves(pokemonDetail.moves)

        //cambiar el fondo segun el tipo
        val mainType = typeNames.firstOrNull()?: "normal"
        val bgResId = resources.getIdentifier("bg_$mainType", "drawable", packageName)

        val backgroundImageView = findViewById<ImageView>(R.id.backgroundImageView)
        if (bgResId != 0) {
            backgroundImageView.setImageResource(bgResId)
        }

    }
    fun loadSpeciesSubtitle(pokemonName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = pokeservice.getInstance().getPokemonSpecies(pokemonName)

                if (response.isSuccessful) {
                    val genus = response.body()
                        ?.genera
                        ?.firstOrNull { it.language.name == "en" }
                        ?.genus

                    withContext(Dispatchers.Main) {
                        supportActionBar?.subtitle = genus
                    }
                } else {
                    Log.e("SpeciesAPI", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SpeciesAPI", "Exception: ${e.message}")
            }
        }
    }
    fun showTypeIcons(typeNames: List<String>, typeImageViews: List<ImageView>) {
        for (i in typeImageViews.indices) {
            if (i < typeNames.size) {
                val typeName = typeNames[i].lowercase()
                val resourceId = resources.getIdentifier("img_$typeName", "drawable", packageName)

                if (resourceId != 0) {
                    typeImageViews[i].setImageResource(resourceId)
                    typeImageViews[i].visibility = View.VISIBLE
                } else {
                    typeImageViews[i].setImageResource(R.drawable.img_normal)
                    typeImageViews[i].visibility = View.VISIBLE
                }
            } else {
                typeImageViews[i].visibility = View.GONE
            }
        }
    }

    fun loadEvolutionChain(pokemonName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val speciesResponse = pokeservice.getInstance().getPokemonSpecies(pokemonName)
                val evolutionUrl = speciesResponse.body()?.evolutionChain?.url

                val evolutionId = evolutionUrl?.split("/")?.filter { it.isNotEmpty() }?.last()?.toIntOrNull()
                if (evolutionId != null) {
                    val evolutionResponse = pokeservice.getInstance().getEvolutionChain(evolutionId)
                    val chain = evolutionResponse.body()?.chain

                    val evolutionList = mutableListOf<String>()
                    parseEvolutionChain(chain, evolutionList)

                    withContext(Dispatchers.Main) {
                        // Muestra los nombres en content_basic_info (puedes hacerlo más bonito luego)
                        binding.contentBasicInfo.evolutionTextView.text = evolutionList.joinToString(" → ") { it.replaceFirstChar { c -> c.uppercase() } }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun parseEvolutionChain(chain: ChainLink?, evolutionList: MutableList<String>) {
        if (chain == null) return
        evolutionList.add(chain.species.name)
        if (chain.evolves_to.isNotEmpty()) {
            parseEvolutionChain(chain.evolves_to.first(), evolutionList)
        }
    }

    fun loadMoves(moveList: List<MoveSlot>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val detailedMoves = mutableListOf<MoveDetail>()
                val service = pokeservice.getInstance()

                for (move in moveList.take(249)) {
                    val moveDetail = service.getMove(move.move.name)
                    detailedMoves.add(moveDetail)
                }

                withContext(Dispatchers.Main) {
                    val moveAdapter = MoveAdapter(this@DetailActivity, detailedMoves) { position ->
                        val move = detailedMoves[position]
                        Toast.makeText(
                            this@DetailActivity,
                            "Movimiento: ${move.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    binding.contentMoves.movesRecyclerView.apply {
                        layoutManager = LinearLayoutManager(this@DetailActivity)
                        adapter = moveAdapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }


}