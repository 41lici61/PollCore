package com.example.pollcore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pollcore.activities.PollDetailActivity;
import com.example.pollcore.R;
import com.example.pollcore.models.Poll;
import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.PollViewHolder> {

    private List<Poll> pollList;
    private Context context;
    private int userId;
    private OnPollClickListener listener;

    public interface OnPollClickListener {
        void onPollClick(int pollId);
    }

    public PollAdapter(List<Poll> pollList, Context context, OnPollClickListener listener) {
        this.pollList = pollList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PollViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_poll, parent, false);
        return new PollViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PollViewHolder holder, int position) {
        Poll poll = pollList.get(position);
        holder.tvPollTitle.setText(poll.getTitle());
        holder.tvPollQuestion.setText(poll.getQuestion());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPollClick(poll.getIdPoll());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pollList.size();
    }

    public void updateList(List<Poll> newList) {
        pollList.clear();
        pollList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class PollViewHolder extends RecyclerView.ViewHolder {
        TextView tvPollTitle, tvPollQuestion;

        public PollViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPollTitle = itemView.findViewById(R.id.tvPollTitle);
            tvPollQuestion = itemView.findViewById(R.id.tvPollQuestion);
        }
    }
}