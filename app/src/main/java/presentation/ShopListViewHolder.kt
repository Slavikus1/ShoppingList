package presentation

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R

class ShopListViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val tvName: TextView? = view.findViewById<TextView>(R.id.tv_name)
    val tvCount: TextView? = view.findViewById<TextView>(R.id.tv_count)
}