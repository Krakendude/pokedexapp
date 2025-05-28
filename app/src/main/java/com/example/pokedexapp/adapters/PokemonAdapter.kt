package com.example.pokedexapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.pokedexapp.R
import com.example.pokedexapp.data.PokemonItem
import com.example.pokedexapp.databinding.ItemPokemonBinding
import com.squareup.picasso.Picasso

class PokemonAdapter(
    private val context: Context,
    var items: List<PokemonItem>,
    val onItemClick: (position: Int) -> Unit


): Adapter<PokemonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.render(items[position], context, onItemClick)
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(items: List<PokemonItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}

class PokemonViewHolder(val binding: ItemPokemonBinding) : ViewHolder(binding.root) {

    fun render(pokemon: PokemonItem, context: Context, onItemClick: (position: Int) -> Unit) {
        val id = pokemon.url.split("/").filter {  it.isNotEmpty() }.last()
        fun updateFavoriteIcon() {
            val sharedPrefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
            val favorites = sharedPrefs.getStringSet("favorite_pokemons", setOf())?.toMutableSet() ?: mutableSetOf()

            val isfavorite = favorites.contains(pokemon.name)
            binding.favoriteButtonMain.setImageResource(
                if (isfavorite) R.drawable.ic_favorite_selected else R.drawable.ic_not_favorite
            )
        }

        updateFavoriteIcon()

            binding.favoriteButtonMain.setOnClickListener {
                val sharedPrefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
                val favorites = sharedPrefs.getStringSet("favorite_pokemons", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                val isCurrentlyFavorite = favorites.contains(pokemon.name)

                if (isCurrentlyFavorite) {
                    AlertDialog.Builder(context)
                        .setTitle("Remove from favorites")
                        .setMessage("Are you sure you want to remove ${pokemon.name.capitalize()} from your favorites?")
                        .setPositiveButton("Yes") { _, _ ->
                            favorites.remove(pokemon.name)
                            sharedPrefs.edit().putStringSet("favorite_pokemons", favorites).apply()
                            updateFavoriteIcon()
                            binding.favoriteButtonMain.setImageResource(R.drawable.ic_not_favorite)
                            Toast.makeText(context, "${pokemon.name.capitalize()} removed from favorites", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                } else {
                    favorites.add(pokemon.name)
                    sharedPrefs.edit().putStringSet("favorite_pokemons", favorites).apply()
                    binding.favoriteButtonMain.setImageResource(R.drawable.ic_favorite_selected)
                    updateFavoriteIcon()
                    Toast.makeText(context, "${pokemon.name.capitalize()} added to favorites", Toast.LENGTH_SHORT).show()
                }
            }

        //como se muestra el recyclerview
        binding.nameTextView.text = pokemon.name.replaceFirstChar { it.uppercase() }

        // Extraer el ID desde la URL

        binding.idTextView.text = "Pokemon id: ${id}"

        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

        // Cargar imagen con Picasso
        Picasso.get().load(imageUrl).into(binding.spriteImageView)
    }
}