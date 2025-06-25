package com.example.pet_track.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pet_track.R;
import com.example.pet_track.models.response.MessageResponse;

import lombok.NonNull;

public class ChatAdapter extends ListAdapter<MessageResponse, RecyclerView.ViewHolder> {

    private final String currentUserId;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 0;

    public ChatAdapter(String currentUserId) {
        super(DIFF_CALLBACK);
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getSenderId().equals(currentUserId) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_SENT) {
            View view = inflater.inflate(R.layout.item_chat_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_chat_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageResponse message = getItem(position);
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    // ViewHolder for sent messages
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textContent;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.textMessage);
        }

        void bind(MessageResponse message) {
            textContent.setText(message.getContent());
            int maxWidth = (int) (itemView.getResources().getDisplayMetrics().widthPixels * 0.7);
            textContent.setMaxWidth(maxWidth);
        }
    }

    // ViewHolder for received messages
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textContent;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.textMessage);
        }

        void bind(MessageResponse message) {
            textContent.setText(message.getContent());
            int maxWidth = (int) (itemView.getResources().getDisplayMetrics().widthPixels * 0.7);
            textContent.setMaxWidth(maxWidth);
        }
    }

    public static final DiffUtil.ItemCallback<MessageResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<MessageResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull MessageResponse oldItem, @NonNull MessageResponse newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MessageResponse oldItem, @NonNull MessageResponse newItem) {
            return oldItem.equals(newItem);
        }
    };
}

