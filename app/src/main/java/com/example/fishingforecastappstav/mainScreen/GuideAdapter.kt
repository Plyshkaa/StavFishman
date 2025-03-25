package com.example.fishingforecastappstav.mainScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fishingforecastappstav.R

class GuideAdapter(
    private val items: List<GuideCategory>,
    private val onItemClick: (GuideCategory) -> Unit
) : RecyclerView.Adapter<GuideAdapter.GuideViewHolder>() {

    inner class GuideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvCategoryTitle)
        val image: ImageView = itemView.findViewById(R.id.ivCategoryImage)

        init {
            itemView.setOnClickListener {
                onItemClick(items[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return GuideViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        val category = items[position]
        holder.title.text = category.title
        holder.image.setImageResource(category.imageResId)
    }

    override fun getItemCount() = items.size
}
