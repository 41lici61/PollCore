package com.example.pollcore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pollcore.R;
import com.example.pollcore.models.Poll;
import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.PollViewHolder> {

    private List<Poll> pollList;
    private Context context;
    private OnPollClickListener listener;
    private OnPollDeleteListener deleteListener;
    private boolean showDeleteButton = false;

    public interface OnPollClickListener {
        void onPollClick(int pollId);
    }

    public interface OnPollDeleteListener {
        void onPollDelete(int pollId, int position);
    }

    public PollAdapter(List<Poll> pollList, Context context, OnPollClickListener listener) {
        this.pollList = pollList;
        this.context = context;
        this.listener = listener;
        this.showDeleteButton = false;
    }

    public PollAdapter(List<Poll> pollList, Context context, OnPollClickListener listener,
                       OnPollDeleteListener deleteListener) {
        this.pollList = pollList;
        this.context = context;
        this.listener = listener;
        this.deleteListener = deleteListener;
        this.showDeleteButton = true;
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

        if (showDeleteButton && deleteListener != null) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                deleteListener.onPollDelete(poll.getIdPoll(), position);
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
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

    public void removeItem(int position) {
        pollList.remove(position);
        notifyItemRemoved(position);
    }

    public static class PollViewHolder extends RecyclerView.ViewHolder {
        TextView tvPollTitle, tvPollQuestion;
        ImageButton btnDelete;

        public PollViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPollTitle = itemView.findViewById(R.id.tvPollTitle);
            tvPollQuestion = itemView.findViewById(R.id.tvPollQuestion);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}