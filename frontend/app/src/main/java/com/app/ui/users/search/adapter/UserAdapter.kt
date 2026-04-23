package com.app.ui.users.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.R
import com.app.dto.model.UserDto

class UserAdapter(
    private val onAddFriendClick: (UserDto) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var users: List<UserDto> = emptyList()

    fun setUsers(users: List<UserDto>) {
        this.users = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.textUsername.text = user.username
        holder.buttonFriendAction.setOnClickListener { onAddFriendClick(user) }
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textUsername: TextView = itemView.findViewById(R.id.textUsername)
        val buttonFriendAction: Button = itemView.findViewById(R.id.buttonFriendAction)
    }
}

