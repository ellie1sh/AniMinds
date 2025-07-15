package com.uilover.project2252.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.uilover.project2252.Adapter.CategoryAdapter
import com.uilover.project2252.Adapter.PopularAdapter
import com.uilover.project2252.ViewModel.MainViewModel
import com.uilover.project2252.databinding.ActivityMainBinding
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel()
    private val auth     = FirebaseAuth.getInstance()
    private val db       = FirebaseFirestore.getInstance()
    private var firstLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBanner()
        initCategory()
        initPopular()
        initBottomMenu()
        initSearchBar()
        watchMyOrders()
    }

    private fun initBottomMenu() {
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, CartActivity::class.java))
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, BuyerProfileActivity::class.java))
        }
        
        // Update favorites button text and add click listener
        binding.favoritesBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, FavoritesActivity::class.java))
        }

        // Add orders button click listener
        binding.ordersBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, OrdersActivity::class.java))
        }
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.loadBanner().observeForever {
            Glide.with(this@MainActivity)
                .load(it[0].url)
                .into(binding.banner)
            binding.progressBarBanner.visibility = View.GONE
        }
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.loadCategory().observeForever {
            binding.recyclerViewCat.layoutManager =
                LinearLayoutManager(
                    this@MainActivity, LinearLayoutManager.HORIZONTAL,
                    false
                )
            binding.recyclerViewCat.adapter = CategoryAdapter(it)
            binding.progressBarCategory.visibility = View.GONE
        }
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE
        viewModel.loadPopular().observeForever {
            binding.recyclerViewPopular.layoutManager = GridLayoutManager(this, 2)
            binding.recyclerViewPopular.adapter = PopularAdapter(it)
            binding.progressBarPopular.visibility = View.GONE
        }
    }

    private fun initSearchBar() {
        binding.editTextText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    initPopular()
                    initCategory()
                    return
                }
                viewModel.loadAllItems().observeForever { allItems ->
                    val filtered = allItems.filter { it.title.contains(query, ignoreCase = true) }
                    Log.d("SearchDebug", "Filtered items: $filtered")
                    binding.recyclerViewPopular.adapter = PopularAdapter(filtered.toMutableList())
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove observers to prevent memory leaks
        viewModel.loadPopular().removeObserver { }
        viewModel.loadCategory().removeObserver { }
    }


    private fun watchMyOrders() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("Orders")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("OrderWatcher", "listen:error", error)
                    return@addSnapshotListener
                }

                if (firstLoad) {
                    firstLoad = false
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { dc ->
                    if (dc.type == DocumentChange.Type.MODIFIED) {
                        val oldStatus = dc.oldIndex
                        val newStatus = dc.document.getString("status")

                        if (newStatus == "accepted") {
                            val orderId = dc.document.getString("orderId") ?: dc.document.id
                            showOrderAcceptedToast(orderId)
                        }
                    }
                }
            }
    }

    private fun showOrderAcceptedToast(orderId: String) {
        val toast = Toast.makeText(
            this,
            "Order #$orderId accepted!",
            Toast.LENGTH_SHORT
        )


        val xOffset = 50
        val yOffset = 100
        toast.setGravity(Gravity.BOTTOM or Gravity.END, xOffset, yOffset)

        toast.show()
    }
}