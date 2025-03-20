package com.example.fishingforecastappstav.mainScreen


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fishingforecastappstav.databinding.ItemFishBinding

class FishAdapter(
    private val fishList: List<Fish>,
    private val onFishClick: (Fish) -> Unit
) : RecyclerView.Adapter<FishAdapter.FishViewHolder>() {

    inner class FishViewHolder(val binding: ItemFishBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishViewHolder {
        val binding = ItemFishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FishViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FishViewHolder, position: Int) {
        val fish = fishList[position]
        with(holder.binding) {
            tvFishName.text = fish.name
            ivFishIcon.setImageResource(fish.iconResId)
            root.setOnClickListener { onFishClick(fish) }
        }
    }

    override fun getItemCount(): Int = fishList.size
}
