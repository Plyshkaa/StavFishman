package com.example.fishingforecastappstav.mainScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fishingforecastappstav.databinding.ItemFishBinding

class FishAdapter(
    private val onFishClick: (Fish) -> Unit
) : ListAdapter<Fish, FishAdapter.FishViewHolder>(FishDiffCallback()) {

    inner class FishViewHolder(val binding: ItemFishBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(fish: Fish) {
            with(binding) {
                tvFishName.text = fish.name
                ivFishIcon.setImageResource(fish.iconResId)
                root.setOnClickListener { onFishClick(fish) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishViewHolder {
        val binding = ItemFishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FishViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FishViewHolder, position: Int) {
        val fish = getItem(position)
        holder.bind(fish)
    }
}

class FishDiffCallback : DiffUtil.ItemCallback<Fish>() {
    override fun areItemsTheSame(oldItem: Fish, newItem: Fish): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Fish, newItem: Fish): Boolean {
        return oldItem == newItem
    }
}
