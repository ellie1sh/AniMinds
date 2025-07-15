package com.uilover.project2252.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.uilover.project2252.Domain.BannerModel
import com.uilover.project2252.Domain.CategoryModel
import com.uilover.project2252.Domain.ItemsModel
import com.uilover.project2252.objects.Product

class MainRepository {
    private val firebaseDatabse = FirebaseDatabase.getInstance()

    fun loadBanner(): LiveData<MutableList<BannerModel>> {
        val listData = MutableLiveData<MutableList<BannerModel>>()
        val ref = firebaseDatabse.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BannerModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(BannerModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }


    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        val listData = MutableLiveData<MutableList<CategoryModel>>()
        val ref = firebaseDatabse.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(CategoryModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }

    fun loadPopular(): LiveData<MutableList<ItemsModel>> {
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabse.getReference("Popular")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }

    fun loadItemCategory(categoryId: String): LiveData<MutableList<ItemsModel>> {
        val itemsLiveData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabse.getReference("Items")
        val query: Query = ref.orderByChild("categoryId").equalTo(categoryId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                itemsLiveData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return itemsLiveData
    }

    fun loadAllItems(): LiveData<MutableList<ItemsModel>> {
        val liveData = MutableLiveData<MutableList<ItemsModel>>()
        val combinedList = mutableListOf<ItemsModel>()

        // Fetch from 'Items'
        val refItems = FirebaseDatabase.getInstance().getReference("Items")
        refItems.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val item = child.getValue(ItemsModel::class.java)
                    if (item != null) {
                        combinedList.add(item)
                    }
                }
                // After loading Items, load from 'products'
                val refProducts = FirebaseDatabase.getInstance().getReference("products")
                refProducts.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (child in snapshot.children) {
                            val product = child.getValue(Product::class.java)
                            if (product != null) {
                                combinedList.add(
                                    ItemsModel(
                                        product.title,
                                        product.description,
                                        product.picUrl,
                                        product.price,
                                        product.rating,
                                        product.numberInCart,
                                        product.extra
                                    )
                                )
                            }
                        }
                        liveData.value = combinedList
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        return liveData
    }

    fun loadItems(category: String): LiveData<MutableList<ItemsModel>> {
        val liveData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = FirebaseDatabase.getInstance().getReference("products")
        ref.orderByChild("category").equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<ItemsModel>()
                    for (child in snapshot.children) {
                        val product = child.getValue(Product::class.java)
                        if (product != null) {
                            items.add(
                                ItemsModel(
                                    product.title,
                                    product.description,
                                    product.picUrl,
                                    product.price,
                                    product.rating,
                                    product.numberInCart,
                                    product.extra
                                )
                            )
                        }
                    }
                    liveData.value = items
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        return liveData
    }
}