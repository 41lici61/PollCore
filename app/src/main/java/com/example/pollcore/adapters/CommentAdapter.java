package com.example.pollcore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pollcore.R;
import com.example.pollcore.models.Comment;
import com.example.pollcore.dao.CommentDAO;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private Context context;
    private int userId;
    private int pollId;
    private OnReplyClickListener replyListener;
    private CommentDAO commentDAO;

    public interface OnReplyClickListener {
        void onReplyClick(int commentId, String username);
    }

    public CommentAdapter(List<Comment> commentList, Context context, int userId, int pollId, OnReplyClickListener replyListener) {
        this.commentList = commentList;
        this.context = context;
        this.userId = userId;
        this.pollId = pollId;
        this.replyListener = replyListener;
        this.commentDAO = new CommentDAO();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        holder.tvUsername.setText(comment.getUsername());
        holder.tvContent.setText(comment.getContent());
        if (comment.getCreatedAt() != null) {
            holder.tvDate.setText(sdf.format(comment.getCreatedAt()));
        }

        loadReplies(holder.llRepliesContainer, comment.getIdComment());

        holder.btnReply.setOnClickListener(v -> {
            if (replyListener != null) {
                replyListener.onReplyClick(comment.getIdComment(), comment.getUsername());
            }
        });
    }

    private void loadReplies(LinearLayout container, int parentCommentId) {
        container.removeAllViews();
        List<Comment> replies = commentDAO.getReplies(parentCommentId);

        for (Comment reply : replies) {
            View replyView = LayoutInflater.from(context).inflate(R.layout.item_comment, null);
            TextView tvUsername = replyView.findViewById(R.id.tvCommentUsername);
            TextView tvDate = replyView.findViewById(R.id.tvCommentDate);
            TextView tvContent = replyView.findViewById(R.id.tvCommentContent);
            Button btnReply = replyView.findViewById(R.id.btnReply);
            LinearLayout repliesContainer = replyView.findViewById(R.id.llRepliesContainer);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvUsername.setText(reply.getUsername());
            tvContent.setText(reply.getContent());
            if (reply.getCreatedAt() != null) {
                tvDate.setText(sdf.format(reply.getCreatedAt()));
            }

            btnReply.setVisibility(View.GONE);
            repliesContainer.setVisibility(View.GONE);

            container.addView(replyView);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void updateList(List<Comment> newList) {
        commentList.clear();
        commentList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvDate, tvContent;
        Button btnReply;
        LinearLayout llRepliesContainer;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvCommentUsername);
            tvDate = itemView.findViewById(R.id.tvCommentDate);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
            btnReply = itemView.findViewById(R.id.btnReply);
            llRepliesContainer = itemView.findViewById(R.id.llRepliesContainer);
        }
    }
}