package com.app.ui.friends.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.R
import com.app.dto.model.FriendDto

class FriendsAdapter(
    private val currentUserId: Long
) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    private var items: List<FriendDto> = emptyList()

    fun setItems(items: List<FriendDto>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val otherUsername =
            if (item.requesterId == currentUserId) item.receiverUsername else item.requesterUsername
        holder.username.text = otherUsername
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.textUsername)
    }
}

