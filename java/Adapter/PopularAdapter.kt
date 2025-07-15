package com.uilover.project2252.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uilover.project2252.Activity.DetailActivity
import com.uilover.project2252.Domain.ItemsModel

import com.uilover.project2252.Helper.ManagmentFavorites
import com.uilover.project2252.R
import com.uilover.project2252.databinding.ViewholderPopularBinding

class PopularAdapter(private val items: MutableList<ItemsModel>) :
    RecyclerView.Adapter<PopularAdapter.Viewholder>() {

    private lateinit var context: Context
    private lateinit var managmentFavorites: ManagmentFavorites

    inner class Viewholder(val binding: ViewholderPopularBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularAdapter.Viewholder {
        context = parent.context
        managmentFavorites = ManagmentFavorites(context)
        val binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: PopularAdapter.Viewholder, position: Int) {
        val item = items[position]
        holder.binding.titleTxt.text = item.title
        holder.binding.priceTxt.text = "₱" + item.price

        Glide.with(context)
            .load(item.picUrl[0])
            .into(holder.binding.pic)

        holder.binding.root.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("object", item)
            context.startActivity(intent)
        }

        // Update favorite button state
        updateFavoriteButton(holder, item)

        // Set favorite button click listener
        holder.binding.favoriteBtn.setOnClickListener {
            if (managmentFavorites.isFavorite(item)) {
                managmentFavorites.removeFavorite(item)
            } else {
                managmentFavorites.insertFavorite(item)
            }
            updateFavoriteButton(holder, item)
        }
    }

    private fun updateFavoriteButton(holder: Viewholder, item: ItemsModel) {
        if (managmentFavorites.isFavorite(item)) {
            holder.binding.favoriteBtn.setImageResource(R.drawable.favorite_filled)
        } else {
            holder.binding.favoriteBtn.setImageResource(R.drawable.favorite)
        }
    }

    override fun getItemCount(): Int = items.size
}