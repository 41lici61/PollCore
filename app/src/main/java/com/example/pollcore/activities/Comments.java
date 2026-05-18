package com.example.pollcore.activities;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pollcore.R;
import com.example.pollcore.adapters.CommentAdapter;
import com.example.pollcore.dao.CommentDAO;
import com.example.pollcore.dao.PollDAO;
import com.example.pollcore.dao.ReportDAO;
import com.example.pollcore.models.Comment;
import com.example.pollcore.models.Poll;

import java.util.ArrayList;
import java.util.List;

public class Comments extends AppCompatActivity {

    private TextView tvPollQuestion, tvTotalVotes;
    private TextView tvOption1Text, tvOption2Text, tvOption3Text, tvOption4Text;
    private ProgressBar progressOption1, progressOption2, progressOption3, progressOption4;
    private TextView tvOption1Percent, tvOption2Percent, tvOption3Percent, tvOption4Percent;
    private LinearLayout layoutOption3, layoutOption4;

    private EditText etComment;
    private ImageButton btnPostComment, btnReportPoll;
    private RecyclerView rvComments;

    private PollDAO pollDAO;
    private CommentDAO commentDAO;
    private ReportDAO reportDAO;
    private Poll currentPoll;
    //private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    private int pollId;
    private int userId;
    private Integer replyToCommentId = null;
    private String replyingToUsername = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comments);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pollId = getIntent().getIntExtra("poll_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);

        if (pollId == -1) {
            Toast.makeText(this, "Error: Poll not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pollDAO = new PollDAO();
        commentDAO = new CommentDAO();
        reportDAO = new ReportDAO();

        initializeViews();
        setupRecyclerView();

        loadPollData();
        loadPollResults();
        loadComments();
        checkIfOwnerAndSetupReportButton();

        btnPostComment.setOnClickListener(v -> showPostCommentDialog());
    }

    private void initializeViews() {
        tvPollQuestion = findViewById(R.id.tvPollQuestion);
        tvTotalVotes = findViewById(R.id.tvTotalVotes);

        tvOption1Text = findViewById(R.id.tvOption1Text);
        tvOption2Text = findViewById(R.id.tvOption2Text);
        tvOption3Text = findViewById(R.id.tvOption3Text);
        tvOption4Text = findViewById(R.id.tvOption4Text);

        progressOption1 = findViewById(R.id.progressOption1);
        progressOption2 = findViewById(R.id.progressOption2);
        progressOption3 = findViewById(R.id.progressOption3);
        progressOption4 = findViewById(R.id.progressOption4);

        tvOption1Percent = findViewById(R.id.tvOption1Percent);
        tvOption2Percent = findViewById(R.id.tvOption2Percent);
        tvOption3Percent = findViewById(R.id.tvOption3Percent);
        tvOption4Percent = findViewById(R.id.tvOption4Percent);

        layoutOption3 = findViewById(R.id.layoutOption3);
        layoutOption4 = findViewById(R.id.layoutOption4);

        etComment = findViewById(R.id.etComment);
        btnPostComment = findViewById(R.id.btnPostComment);
        btnReportPoll = findViewById(R.id.btnReportPoll);
        rvComments = findViewById(R.id.rvComments);
    }

    private void setupRecyclerView() {
        rvComments.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadPollData() {
        currentPoll = pollDAO.getById(pollId);

        if (currentPoll == null) {
            Toast.makeText(this, "Error loading poll", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvPollQuestion.setText(currentPoll.getQuestion());

        tvOption1Text.setText(currentPoll.getOption1());
        tvOption2Text.setText(currentPoll.getOption2());

        if (currentPoll.getOption3() != null && !currentPoll.getOption3().isEmpty()) {
            tvOption3Text.setText(currentPoll.getOption3());
            layoutOption3.setVisibility(android.view.View.VISIBLE);
        } else {
            layoutOption3.setVisibility(android.view.View.GONE);
        }

        if (currentPoll.getOption4() != null && !currentPoll.getOption4().isEmpty()) {
            tvOption4Text.setText(currentPoll.getOption4());
            layoutOption4.setVisibility(android.view.View.VISIBLE);
        } else {
            layoutOption4.setVisibility(android.view.View.GONE);
        }
    }

    private void loadPollResults() {
        int[] results = pollDAO.getPollResults(pollId);

        int countOption1 = results[0];
        int countOption2 = results[1];
        int countOption3 = results[2];
        int countOption4 = results[3];
        int totalVotes = results[4];

        tvTotalVotes.setText("Total votes: " + totalVotes);

        if (totalVotes > 0) {
            int percent1 = (countOption1 * 100) / totalVotes;
            int percent2 = (countOption2 * 100) / totalVotes;

            progressOption1.setProgress(percent1);
            progressOption2.setProgress(percent2);
            tvOption1Percent.setText(percent1 + "%");
            tvOption2Percent.setText(percent2 + "%");

            if (currentPoll.getOption3() != null && !currentPoll.getOption3().isEmpty()) {
                int percent3 = (countOption3 * 100) / totalVotes;
                progressOption3.setProgress(percent3);
                tvOption3Percent.setText(percent3 + "%");
            }

            if (currentPoll.getOption4() != null && !currentPoll.getOption4().isEmpty()) {
                int percent4 = (countOption4 * 100) / totalVotes;
                progressOption4.setProgress(percent4);
                tvOption4Percent.setText(percent4 + "%");
            }
        } else {
            progressOption1.setProgress(0);
            progressOption2.setProgress(0);
            tvOption1Percent.setText("0%");
            tvOption2Percent.setText("0%");

            if (currentPoll.getOption3() != null && !currentPoll.getOption3().isEmpty()) {
                progressOption3.setProgress(0);
                tvOption3Percent.setText("0%");
            }

            if (currentPoll.getOption4() != null && !currentPoll.getOption4().isEmpty()) {
                progressOption4.setProgress(0);
                tvOption4Percent.setText("0%");
            }
        }
    }



    private void loadComments() {
        new Thread(() -> {
            List<Comment> comments = commentDAO.getCommentsByPoll(pollId);
            runOnUiThread(() -> {
                commentAdapter = new CommentAdapter(comments, this, userId);
                rvComments.setAdapter(commentAdapter);
            });
        }).start();
    }

    private void checkIfOwnerAndSetupReportButton() {
        new Thread(() -> {
            boolean isOwner = pollDAO.isPollOwner(pollId, userId);
            runOnUiThread(() -> {
                if (!isOwner) {
                    btnReportPoll.setVisibility(android.view.View.VISIBLE);
                    btnReportPoll.setOnClickListener(v -> showReportDialog());
                }
            });
        }).start();
    }

    private void showReportDialog() {
        String[] reportReasons = {
                "Contenido inapropiado",
                "Spam o publicidad",
                "Lenguaje ofensivo",
                "Información falsa",
                "Otro motivo"
        };

        new AlertDialog.Builder(this)
                .setTitle("Report Poll")
                .setItems(reportReasons, (dialog, which) -> {
                    String reason = reportReasons[which];
                    showReasonDetailsDialog(reason);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showReasonDetailsDialog(String reason) {
        // Diálogo para detalles adicionales
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Report Poll - " + reason);

        final EditText input = new EditText(this);
        input.setHint("Additional details (optional)");
        input.setPadding(50, 20, 50, 20);
        builder.setView(input);

        builder.setPositiveButton("Send Report", (dialog, which) -> {
            String details = input.getText().toString().trim();
            sendReport(reason, details);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void sendReport(String reason, String details) {
        new Thread(() -> {
            boolean success = reportDAO.reportPoll(userId, pollId, reason, details);
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "Report sent successfully. Thank you!", Toast.LENGTH_LONG).show();
                    btnReportPoll.setVisibility(android.view.View.GONE);
                } else {
                    Toast.makeText(this, "Error sending report. You may have already reported this poll.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void showPostCommentDialog() {
        String content = etComment.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        String message;
        if (replyToCommentId != null) {
            message = "Post reply to " + replyingToUsername + "?";
        } else {
            message = "Post this comment?";
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage(message)
                .setPositiveButton("Post", (dialog, which) -> postComment(content))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void postComment(String content) {
        Comment comment = new Comment();
        comment.setIdPoll(pollId);
        comment.setIdUser(userId);
        comment.setContent(content);
        comment.setReplyTo(replyToCommentId);

        new Thread(() -> {
            boolean success = commentDAO.createComment(comment);
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "Comment posted!", Toast.LENGTH_SHORT).show();
                    replyToCommentId = null;
                    replyingToUsername = null;
                    etComment.setText("");
                    etComment.setHint("Write a comment...");
                    loadComments();
                } else {
                    Toast.makeText(this, "Error posting comment", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }


}