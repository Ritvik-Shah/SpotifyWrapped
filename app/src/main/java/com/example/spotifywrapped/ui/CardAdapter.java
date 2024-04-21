package com.example.spotifywrapped.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import com.example.spotifywrapped.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<String> texts;

    public CardAdapter(List<String> texts) {
        this.texts = texts;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.textView.setText(texts.get(position));
    }

    @Override
    public int getItemCount() {
        return texts.size();
    }

    public void addItem(String text) {
        texts.add(text);
        notifyItemInserted(texts.size() - 1);
    }

    public void removeItem(int position) {
        if (texts.size() > position) {
            texts.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, texts.size());
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public CardView cardView;

        public CardViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewCard);
            cardView = (CardView) itemView;
        }
    }
}
