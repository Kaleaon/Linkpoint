package com.linkpoint.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.chat.ChatViewModel
import com.linkpoint.chat.ConnectionState
import com.linkpoint.databinding.FragmentLinkpointChatBinding
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var binding: FragmentLinkpointChatBinding? = null
    private val chatAdapter = ChatAdapter()

    private val viewModel: ChatViewModel by viewModels {
        val app = LinkpointApp.from(requireContext())
        ChatViewModel.Factory(app.services.chatRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentLinkpointChatBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = binding ?: return
        fragmentBinding.chatRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }

        fragmentBinding.sendButton.setOnClickListener {
            val text = fragmentBinding.messageInput.text?.toString().orEmpty()
            if (text.isBlank()) {
                Snackbar.make(view, R.string.linkpoint_chat_empty_message, Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.sendMessage(text)
                fragmentBinding.messageInput.setText("")
            }
        }

        fragmentBinding.messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                fragmentBinding.sendButton.performClick()
                true
            } else {
                false
            }
        }

        fragmentBinding.retryButton.setOnClickListener {
            viewModel.reconnect()
        }

        observeViewModel()
        viewModel.start()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.messages.collect { messages ->
                        chatAdapter.submitList(messages) {
                            binding?.chatRecycler?.scrollToPosition(maxOf(messages.size - 1, 0))
                        }
                    }
                }
                launch {
                    viewModel.connectionState.collect { state ->
                        updateConnectionUi(state)
                    }
                }
            }
        }
    }

    private fun updateConnectionUi(state: ConnectionState) {
        val fragmentBinding = binding ?: return
        fragmentBinding.connectionStatus.text = when (state) {
            ConnectionState.CONNECTED -> getString(R.string.linkpoint_chat_connected)
            ConnectionState.CONNECTING -> getString(R.string.linkpoint_chat_connecting)
            ConnectionState.DISCONNECTED -> getString(R.string.linkpoint_chat_disconnected)
        }
        fragmentBinding.progressIndicator.isVisible = state == ConnectionState.CONNECTING
        fragmentBinding.retryButton.isVisible = state == ConnectionState.DISCONNECTED
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
