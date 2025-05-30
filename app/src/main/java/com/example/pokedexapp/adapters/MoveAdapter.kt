package com.example.pokedexapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.pokedexapp.R
import com.example.pokedexapp.data.MoveDetail
import com.example.pokedexapp.data.PokemonItem
import com.example.pokedexapp.databinding.ItemMoveBinding
import com.example.pokedexapp.databinding.ItemPokemonBinding

class MoveAdapter(
    private val context: Context,
    var moveList: List<MoveDetail>,
    val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<MoveAdapter.MoveViewHolder>() {

    class MoveViewHolder(val binding: ItemMoveBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveViewHolder {
        val binding = ItemMoveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoveViewHolder(binding)
    }



    override fun onBindViewHolder(holder: MoveViewHolder, position: Int) {
        val move = moveList[position]
        val b = holder.binding

        // nombre y stats
        b.moveNameTextView.text = move.name.replaceFirstChar { it.uppercase() }
        b.powerTextView.text = "Power: ${move.power ?: "--"}"
        b.accuracyTextView.text = "Accuracy: ${move.accuracy ?: "--"}"
        b.PPTextView.text = "PP: ${move.pp ?: "--"}"
        b.ailmentTextView.text = "Ailment: ${move.meta?.ailment?.name ?: "None"}"

        // tipo
        val typeIcon = context.resources.getIdentifier(
            "img_${move.type.name.lowercase()}",
            "drawable",
            context.packageName
        )
        b.typeImageView.setImageResource(typeIcon)

        val catergoryIcon = when (move.damageClass.name.lowercase()) {
            "physical" -> R.drawable.img_physical
            "special" -> R.drawable.img_special
            "status" -> R.drawable.img_status
            else -> R.drawable.img_status
        }
        b.categoryImageView.setImageResource(catergoryIcon)
    }

    override fun getItemCount(): Int = moveList.size

}