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
    private val currentUserId: Long,
    private val onActionClick: (user: UserDto, isFriend: Boolean) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var users: List<UserDto> = emptyList()
    private var friendUserIds: Set<Long> = emptySet()

    fun setUsers(users: List<UserDto>) {
        this.users = users
        notifyDataSetChanged()
    }

    fun setFriendUserIds(friendUserIds: Set<Long>) {
        this.friendUserIds = friendUserIds
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

        if (user.id == currentUserId) {
            holder.buttonFriendAction.text = holder.itemView.context.getString(R.string.tu)
            holder.buttonFriendAction.isEnabled = false
            holder.buttonFriendAction.setOnClickListener(null)
            return
        }

        val isFriend = friendUserIds.contains(user.id)
        holder.buttonFriendAction.text = holder.itemView.context.getString(
            if (isFriend) R.string.eliminar_amistad else R.string.a_adir_amigos
        )
        holder.buttonFriendAction.isEnabled = true
        holder.buttonFriendAction.setOnClickListener { onActionClick(user, isFriend) }
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textUsername: TextView = itemView.findViewById(R.id.textUsername)
        val buttonFriendAction: Button = itemView.findViewById(R.id.buttonFriendAction)
    }
}
