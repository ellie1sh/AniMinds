package com.uilover.project2252.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.uilover.project2252.Adapter.PopularAdapter
import com.uilover.project2252.Helper.ManagmentFavorites
import com.uilover.project2252.databinding.ActivityFavoritesBinding

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var managmentFavorites: ManagmentFavorites

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentFavorites = ManagmentFavorites(this)
        initList()
        initBackButton()
    }

    private fun initList() {
        binding.progressBarFavorites.visibility = View.VISIBLE
        val favoritesList = managmentFavorites.getListFavorites()
        
        if (favoritesList.isEmpty()) {
            binding.emptyFavoritesText.visibility = View.VISIBLE
            binding.recyclerViewFavorites.visibility = View.GONE
        } else {
            binding.emptyFavoritesText.visibility = View.GONE
            binding.recyclerViewFavorites.visibility = View.VISIBLE
            binding.recyclerViewFavorites.layoutManager = GridLayoutManager(this, 2)
            binding.recyclerViewFavorites.adapter = PopularAdapter(favoritesList)
        }
        binding.progressBarFavorites.visibility = View.GONE
    }

    private fun initBackButton() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }
} 