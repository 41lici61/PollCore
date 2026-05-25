package com.example.pollcore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.pollcore.R;
import com.example.pollcore.dao.PollDAO;
import com.example.pollcore.models.Poll;

public class PollDetailActivity extends AppCompatActivity {

    private TextView tvPollTitle, tvPollDescription, tvPollQuestion, tvVotesInfo;
    private RadioGroup rgOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private ImageButton btnAccept;

    private PollDAO pollDAO;
    private Poll currentPoll;
    private int pollId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_poll_detail);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        pollId = intent.getIntExtra("poll_id", -1);
        userId = intent.getIntExtra("user_id", -1);

        if (pollId == -1) {
            Toast.makeText(this, R.string.error_poll_not_identified, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (userId == -1) {
            Toast.makeText(this, R.string.error_user_not_identified, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();

        pollDAO = new PollDAO();

        loadPoll();

        btnAccept.setOnClickListener(v -> processVote());
    }

    private void initializeViews() {
        tvPollTitle = findViewById(R.id.tvPollTitle);
        tvPollDescription = findViewById(R.id.tvPollDescription);
        tvPollQuestion = findViewById(R.id.tvPollQuestion);
        tvVotesInfo = findViewById(R.id.tvVotesInfo);
        rgOptions = findViewById(R.id.rgOptions);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnAccept = findViewById(R.id.btnAccept);
    }

    private void loadPoll() {
        currentPoll = pollDAO.getById(pollId);

        if (currentPoll == null) {
            Toast.makeText(this, R.string.error_loading_poll, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvPollTitle.setText(currentPoll.getTitle());

        if (currentPoll.getDescription() != null && !currentPoll.getDescription().isEmpty()) {
            tvPollDescription.setText(currentPoll.getDescription());
            tvPollDescription.setVisibility(android.view.View.VISIBLE);
        } else {
            tvPollDescription.setVisibility(android.view.View.GONE);
        }

        tvPollQuestion.setText(currentPoll.getQuestion());
        tvVotesInfo.setText(currentPoll.getTotalVotes() + " " + getString(R.string.poll_total_votes_label));

        rbOption1.setText(currentPoll.getOption1());
        rbOption2.setText(currentPoll.getOption2());

        if (currentPoll.getOption3() != null && !currentPoll.getOption3().isEmpty()) {
            rbOption3.setText(currentPoll.getOption3());
            rbOption3.setVisibility(android.view.View.VISIBLE);
        } else {
            rbOption3.setVisibility(android.view.View.GONE);
        }

        if (currentPoll.getOption4() != null && !currentPoll.getOption4().isEmpty()) {
            rbOption4.setText(currentPoll.getOption4());
            rbOption4.setVisibility(android.view.View.VISIBLE);
        } else {
            rbOption4.setVisibility(android.view.View.GONE);
        }
    }

    private void processVote() {
        int selectedId = rgOptions.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, R.string.poll_select_option, Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedOption = 0;
        if (selectedId == R.id.rbOption1) {
            selectedOption = 1;
        } else if (selectedId == R.id.rbOption2) {
            selectedOption = 2;
        } else if (selectedId == R.id.rbOption3) {
            selectedOption = 3;
        } else if (selectedId == R.id.rbOption4) {
            selectedOption = 4;
        }

        final int option = selectedOption;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_vote_title)
                .setMessage(getString(R.string.confirm_vote_message) + "\n\n" +
                        getString(R.string.selected_option_label) + " " + getOptionText(option))
                .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                    registerVote(option);
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    private String getOptionText(int option) {
        switch (option) {
            case 1: return rbOption1.getText().toString();
            case 2: return rbOption2.getText().toString();
            case 3: return rbOption3.getText().toString();
            case 4: return rbOption4.getText().toString();
            default: return "";
        }
    }

    private void registerVote(int selectedOption) {
        btnAccept.setEnabled(false);

        new Thread(() -> {
            boolean success = pollDAO.vote(userId, pollId, selectedOption);

            runOnUiThread(() -> {
                btnAccept.setEnabled(true);

                if (success) {
                    Toast.makeText(PollDetailActivity.this, R.string.poll_vote_success, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PollDetailActivity.this, Comments.class);
                    intent.putExtra("poll_id", pollId);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("selected_option", selectedOption);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PollDetailActivity.this);
                    builder.setTitle(R.string.error)
                            .setMessage(R.string.poll_already_voted)
                            .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                                Intent intent = new Intent(PollDetailActivity.this, Comments.class);
                                intent.putExtra("poll_id", pollId);
                                intent.putExtra("user_id", userId);
                                startActivity(intent);
                                finish();
                            })
                            .show();
                }
            });
        }).start();
    }
}