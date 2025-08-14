package com.example.fishingforecastappstav.mainScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fishingforecastappstav.R

class FishSelectionAdapter(
    private val fishList: List<Fish>,
    private val onFishSelected: (Fish) -> Unit
) : RecyclerView.Adapter<FishSelectionAdapter.FishViewHolder>() {

    class FishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFishIcon: ImageView = itemView.findViewById(R.id.ivFishIcon)
        val tvFishName: TextView = itemView.findViewById(R.id.tvFishName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fish_selection_item, parent, false)
        return FishViewHolder(view)
    }

    override fun onBindViewHolder(holder: FishViewHolder, position: Int) {
        val fish = fishList[position]
        
        holder.tvFishName.text = fish.name
        
        // Устанавливаем иконку рыбы напрямую
        holder.ivFishIcon.setImageResource(fish.iconResId)
        
        holder.itemView.setOnClickListener {
            onFishSelected(fish)
        }
    }

    override fun getItemCount(): Int = fishList.size
}
