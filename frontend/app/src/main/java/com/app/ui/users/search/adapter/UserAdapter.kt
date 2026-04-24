package com.app.ui.users.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.R
import com.app.dto.model.UserDto

enum class FriendAction {
    ADD,
    ACCEPT,
    CANCEL,
    REMOVE,
    NONE
}

class UserAdapter(
    private val currentUserId: Long,
    private val onActionClick: (user: UserDto, action: FriendAction) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var users: List<UserDto> = emptyList()
    private var acceptedFriendUserIds: Set<Long> = emptySet()
    private var pendingIncomingUserIds: Set<Long> = emptySet()
    private var pendingOutgoingUserIds: Set<Long> = emptySet()

    fun setUsers(users: List<UserDto>) {
        this.users = users
        notifyDataSetChanged()
    }

    fun setAcceptedFriendUserIds(friendUserIds: Set<Long>) {
        this.acceptedFriendUserIds = friendUserIds
        notifyDataSetChanged()
    }

    fun setPendingIncomingUserIds(userIds: Set<Long>) {
        this.pendingIncomingUserIds = userIds
        notifyDataSetChanged()
    }

    fun setPendingOutgoingUserIds(userIds: Set<Long>) {
        this.pendingOutgoingUserIds = userIds
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

        val action = when {
            acceptedFriendUserIds.contains(user.id) -> FriendAction.REMOVE
            pendingIncomingUserIds.contains(user.id) -> FriendAction.ACCEPT
            pendingOutgoingUserIds.contains(user.id) -> FriendAction.CANCEL
            else -> FriendAction.ADD
        }

        holder.buttonFriendAction.text = holder.itemView.context.getString(when (action) {
            FriendAction.REMOVE -> R.string.eliminar_amistad
            FriendAction.ACCEPT -> R.string.aceptar_solicitud
            FriendAction.CANCEL -> R.string.cancelar_solicitud
            FriendAction.ADD -> R.string.a_adir_amigos
            FriendAction.NONE -> R.string.a_adir_amigos
        })
        holder.buttonFriendAction.isEnabled = true
        holder.buttonFriendAction.setOnClickListener { onActionClick(user, action) }
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textUsername: TextView = itemView.findViewById(R.id.textUsername)
        val buttonFriendAction: Button = itemView.findViewById(R.id.buttonFriendAction)
    }
}
