package com.example.pollcore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pollcore.R;
import com.example.pollcore.dao.ReportDAO;
import com.example.pollcore.models.Comment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private Context context;
    private int userId;
    private ReportDAO reportDAO;

    public CommentAdapter(List<Comment> commentList, Context context, int userId) {
        this.commentList = commentList;
        this.context = context;
        this.userId = userId;
        this.reportDAO = new ReportDAO();
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

        // Report comment button
        holder.btnReportComment.setOnClickListener(v -> {
            showReportCommentDialog(comment.getIdComment(), comment.getUsername());
        });
    }

    private void showReportCommentDialog(int commentId, String username) {
        String[] reportReasons = {
                "Contenido inapropiado",
                "Spam o publicidad",
                "Lenguaje ofensivo",
                "Acoso o intimidación",
                "Información falsa",
                "Otro motivo"
        };

        new AlertDialog.Builder(context)
                .setTitle("Report Comment from " + username)
                .setItems(reportReasons, (dialog, which) -> {
                    String reason = reportReasons[which];
                    showReasonDetailsDialog(commentId, reason);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showReasonDetailsDialog(int commentId, String reason) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report Comment - " + reason);

        final android.widget.EditText input = new android.widget.EditText(context);
        input.setHint("Additional details (optional)");
        input.setPadding(50, 20, 50, 20);
        builder.setView(input);

        builder.setPositiveButton("Send Report", (dialog, which) -> {
            String details = input.getText().toString().trim();
            sendReport(commentId, reason, details);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void sendReport(int commentId, String reason, String details) {
        new Thread(() -> {
            boolean success = reportDAO.reportComment(userId, commentId, reason, details);
            ((android.app.Activity) context).runOnUiThread(() -> {
                if (success) {
                    android.widget.Toast.makeText(context, "Report sent successfully. Thank you!", android.widget.Toast.LENGTH_LONG).show();
                } else {
                    android.widget.Toast.makeText(context, "Error sending report. You may have already reported this comment.", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void updateList(List<Comment> newList) {
        this.commentList = newList;
        notifyDataSetChanged();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvDate, tvContent;
        ImageButton btnReportComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvCommentUsername);
            tvDate = itemView.findViewById(R.id.tvCommentDate);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
            btnReportComment = itemView.findViewById(R.id.btnReportComment);
        }
    }
}