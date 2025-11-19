package presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import domain.ShopItem

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpRecyclerView()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.shopList.observe(this) {
            shopListAdapter.submitList(it)
        }
        val buttonAddItem = findViewById<FloatingActionButton>(R.id.button_add_shop_item)
        buttonAddItem.setOnClickListener {
            Log.d("MainActivity", "Button Add clicked")
            try {
                val intent = ShopItemActivity.newIntentAddItem(this)
                Log.d("MainActivity", "Add Intent created: $intent")
                Log.d("MainActivity", "Intent component: ${intent.component}")
                Log.d("MainActivity", "Intent extras: ${intent.extras?.keySet()}")
                if (intent.resolveActivity(packageManager) != null) {
                    Log.d("MainActivity", "Activity can be resolved")
                    startActivity(intent)
                    Log.d("MainActivity", "startActivity called")
                } else {
                    Log.e("MainActivity", "Activity cannot be resolved!")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error starting activity", e)
                e.printStackTrace()
            }
        }
    }

    fun setUpRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_shop_list)
        with(recyclerView) {
            shopListAdapter = ShopListAdapter()
            adapter = shopListAdapter
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.VIEW_TYPE_ENABLED,
                ShopListAdapter.MAX_VIEW_HOLDER_COUNT
            )
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.VIEW_TYPE_DISABLED,
                ShopListAdapter.MAX_VIEW_HOLDER_COUNT
            )
        }
        setUpClickListeners()
        setUpSwipeListener(recyclerView)
    }

    private fun setUpClickListeners() {
        shopListAdapter.onShopItemLongClickListener = {
            viewModel.changeEnableState(it)
        }
        shopListAdapter.onShopItemClickListener = {
            Log.d("MainActivity", "Item clicked: ${it.name}, id: ${it.id}")
            if (it.id != ShopItem.UNDEFINED_ID) {
                try {
                    val intent = ShopItemActivity.newIntentEditItem(this, it)
                    Log.d("MainActivity", "Edit Intent created: $intent")
                    startActivity(intent)
                    Log.d("MainActivity", "startActivity called for edit")
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error starting edit activity", e)
                    e.printStackTrace()
                }
            } else {
                Log.e("MainActivity", "Cannot edit item with undefined ID")
            }
        }
    }

    private fun setUpSwipeListener(recyclerView: RecyclerView?) {
        val callBack = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val item = shopListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteShopItem(item)
            }

        }
        val itemTouchHelper = ItemTouchHelper(callBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}