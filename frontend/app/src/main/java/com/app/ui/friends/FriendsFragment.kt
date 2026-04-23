package com.app.ui.friends

import android.os.Bundle
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.R
import com.app.databinding.FragmentFriendsBinding
import com.app.ui.friends.adapter.FriendsAdapter
import com.app.ui.friends.adapter.PendingRequestsAdapter
import com.app.utils.SessionManager
import com.google.android.material.snackbar.Snackbar
import com.app.ui.main.MainActivity

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FriendsViewModel by viewModels()

    private lateinit var pendingAdapter: PendingRequestsAdapter
    private lateinit var friendsAdapter: FriendsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.menuButton.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }

        if (!SessionManager.isLoggedIn()) {
            Snackbar.make(binding.root, "Haz login para ver tus amigos", Snackbar.LENGTH_SHORT).show()
            return
        }

        setupRecyclerViews(SessionManager.userId)
        observeViewModel(SessionManager.userId)
        viewModel.loadFriends(SessionManager.userId)
    }

    private fun setupRecyclerViews(userId: Long) {
        pendingAdapter = PendingRequestsAdapter(
            onAcceptClick = { friend -> viewModel.acceptFriend(friend.id) },
            onRejectClick = { friend -> viewModel.rejectFriend(friend.id) }
        )

        friendsAdapter = FriendsAdapter(currentUserId = userId)

        binding.recyclerPendingRequests.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPendingRequests.adapter = pendingAdapter

        binding.recyclerFriends.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFriends.adapter = friendsAdapter
    }

    private fun observeViewModel(userId: Long) {
        viewModel.pendingRequests.observe(viewLifecycleOwner) { requests ->
            pendingAdapter.setItems(requests)
            binding.textPendingTitle.text =
                if (requests.isNullOrEmpty()) getString(R.string.no_tienes_solicitudes_pendientes)
                else getString(R.string.solicitudes_pendientes)
        }

        viewModel.acceptedFriends.observe(viewLifecycleOwner) { friends ->
            friendsAdapter.setItems(friends)
            binding.textFriendsTitle.text =
                if (friends.isNullOrEmpty()) getString(R.string.todavia_no_tienes_amigos)
                else getString(R.string.mis_amigos)
        }

        viewModel.actionDone.observe(viewLifecycleOwner) {
            viewModel.loadFriends(userId)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
