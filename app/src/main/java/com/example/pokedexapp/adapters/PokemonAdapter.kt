package com.example.pokedexapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.pokedexapp.data.PokemonItem
import com.example.pokedexapp.databinding.ItemPokemonBinding
import com.squareup.picasso.Picasso

class PokemonAdapter(
    var items: List<PokemonItem>,
    val onItemClick: (position: Int) -> Unit
): Adapter<PokemonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.item_superhero, parent, false)
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = items[position]
        holder.render(pokemon)
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    fun updateItems(items: List<PokemonItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}

class PokemonViewHolder(val binding: ItemPokemonBinding) : ViewHolder(binding.root) {

    fun render(pokemon: PokemonItem) {
        binding.nameTextView.text = pokemon.name.replaceFirstChar { it.uppercase() }

        // Extraer el ID desde la URL
        val id = pokemon.url.split("/").filter { it.isNotEmpty() }.last()

        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

        // Cargar imagen con Picasso
        Picasso.get().load(imageUrl).into(binding.spriteImageView)
    }
}