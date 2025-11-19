package presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.shoppinglist.R
import domain.ShopItem

class ShopListAdapter : ListAdapter<ShopItem, ShopListViewHolder>(
    ShopItemDiffCallBack()
) {
    var onShopItemLongClickListener: ((ShopItem) -> Unit)? = null

    var onShopItemClickListener: ((ShopItem) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShopListViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_ENABLED -> R.layout.item_shop_enabled
            VIEW_TYPE_DISABLED -> R.layout.item_shop_disabled
            else -> throw RuntimeException("Error viewType $viewType")
        }
        val view = LayoutInflater.from(parent.context).inflate(
            layout,
            parent,
            false
        )
        return ShopListViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ShopListViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.tvName?.text = item.name
        holder.tvCount?.text = item.count.toString()
        
        // Очищаем предыдущие слушатели перед установкой новых
        holder.view.setOnLongClickListener(null)
        holder.view.setOnClickListener(null)
        
        holder.view.setOnLongClickListener {
            onShopItemLongClickListener?.invoke(item)
            true
        }
        holder.view.setOnClickListener {
            android.util.Log.d("ShopListAdapter", "View clicked for item: ${item.name}, id: ${item.id}")
            onShopItemClickListener?.invoke(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.isEnabled) VIEW_TYPE_ENABLED
        else VIEW_TYPE_DISABLED
    }

    companion object {
        const val VIEW_TYPE_ENABLED = 0
        const val VIEW_TYPE_DISABLED = 1
        const val MAX_VIEW_HOLDER_COUNT = 30
    }
}