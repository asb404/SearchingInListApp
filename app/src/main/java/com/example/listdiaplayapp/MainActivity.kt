package com.example.listdiaplayapp

import ItemAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var searchBar: EditText
    private var items: List<Item> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        searchBar = findViewById(R.id.searchBar)
        Log.d("Antara", "onCreate:")
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchData()
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterItems(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchData() {
        val call = ApiClient.apiService.getItems()

        call.enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                if (response.isSuccessful) {
                    items = response.body()?.filter { !it.name.isNullOrBlank() }
                        ?.sortedWith(compareBy({ it.listId }, { it.name })) ?: emptyList()
                    itemAdapter = ItemAdapter(items)
                    recyclerView.adapter = itemAdapter
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterItems(query: String) {
        val filteredItems = items.filter {
            it.listId.toString().contains(query, ignoreCase = true) ||
                    !it.name.isNullOrBlank() && it.name.contains(query, ignoreCase = true)
        }
        itemAdapter.updateItems(filteredItems)
    }
}