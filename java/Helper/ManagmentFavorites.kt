package com.uilover.project2252.Helper

import android.content.Context
import android.widget.Toast
import com.uilover.project2252.Domain.ItemsModel

class ManagmentFavorites(val context: Context) {
    private val tinyDB = TinyDB(context)

    fun insertFavorite(item: ItemsModel) {
        var favoritesList = getListFavorites()
        val existAlready = favoritesList.any { it.title == item.title }
        
        if (!existAlready) {
            favoritesList.add(item)
            tinyDB.putListObject("FavoritesList", favoritesList)
            Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Already in Favorites", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeFavorite(item: ItemsModel) {
        var favoritesList = getListFavorites()
        favoritesList.removeIf { it.title == item.title }
        tinyDB.putListObject("FavoritesList", favoritesList)
        Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show()
    }

    fun getListFavorites(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("FavoritesList") ?: arrayListOf()
    }

    fun isFavorite(item: ItemsModel): Boolean {
        val favoritesList = getListFavorites()
        return favoritesList.any { it.title == item.title }
    }
} 