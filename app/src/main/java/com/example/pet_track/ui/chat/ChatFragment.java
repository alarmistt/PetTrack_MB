package com.example.pet_track.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pet_track.R;
import com.example.pet_track.utils.SharedPreferencesManager;
import com.example.pet_track.utils.SignalRManager;
import com.example.pet_track.viewmodel.ChatViewModel;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private EditText editMessage;
    private Button buttonSend;

    private static final String ARG_CLINIC_ID = "clinicId";
    private String clinicId;
    private String currentUserId;

    public static ChatFragment newInstance(String clinicId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CLINIC_ID, clinicId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clinicId = getArguments().getString(ARG_CLINIC_ID);
        }

        currentUserId = SharedPreferencesManager.getInstance(requireContext()).getUserId();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewChat);
        editMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatAdapter = new ChatAdapter(currentUserId);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatViewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            chatAdapter.submitList(messages, () ->
                    recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1)
            );
        });

        chatViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        buttonSend.setOnClickListener(v -> {
            String messageText = editMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                chatViewModel.sendMessage(requireContext(), clinicId, messageText);
                editMessage.setText("");
            }
        });

        chatViewModel.loadMessages(requireContext(), clinicId);
    }

    @Override
    public void onStart() {
        super.onStart();
        String token = SharedPreferencesManager.getInstance(requireContext()).getToken();

        SignalRManager.getInstance().setListener(message -> {
            requireActivity().runOnUiThread(() -> {
                chatViewModel.addIncomingMessage(message);
            });
        });

        SignalRManager.getInstance().startConnection(token);
    }

    @Override
    public void onStop() {
        super.onStop();
        SignalRManager.getInstance().stopConnection();
    }
}

