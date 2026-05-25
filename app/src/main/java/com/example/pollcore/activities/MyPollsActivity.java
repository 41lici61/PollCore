package com.example.pollcore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pollcore.R;
import com.example.pollcore.adapters.PollAdapter;
import com.example.pollcore.dao.PollDAO;
import com.example.pollcore.models.Poll;
import java.util.ArrayList;
import java.util.List;

public class MyPollsActivity extends AppCompatActivity {

    private RecyclerView rvMyPolls;
    private PollAdapter pollAdapter;
    private List<Poll> pollList;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_polls);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.menu_my_polls);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        userId = getIntent().getIntExtra("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, R.string.error_user_not_identified, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvMyPolls = findViewById(R.id.rvMyPolls);
        rvMyPolls.setLayoutManager(new LinearLayoutManager(this));
        pollList = new ArrayList<>();

        loadMyPolls();
    }

    private void loadMyPolls() {
        new Thread(() -> {
            PollDAO pollDAO = new PollDAO();
            List<Poll> polls = pollDAO.getPollsByUser(userId);

            runOnUiThread(() -> {
                pollList.clear();
                pollList.addAll(polls);

                pollAdapter = new PollAdapter(pollList, this,
                        pollId -> {
                            Intent intent = new Intent(MyPollsActivity.this, Comments.class);
                            intent.putExtra("poll_id", pollId);
                            intent.putExtra("user_id", userId);
                            startActivity(intent);
                        },
                        (pollId, position) -> showDeleteConfirmationDialog(pollId, position)
                );
                rvMyPolls.setAdapter(pollAdapter);

                if (polls.isEmpty()) {
                    Toast.makeText(this, R.string.no_polls_created, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void showDeleteConfirmationDialog(int pollId, int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_poll_title)
                .setMessage(R.string.delete_poll_warning)
                .setPositiveButton(R.string.delete, (dialog, which) -> deletePoll(pollId, position))
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    private void deletePoll(int pollId, int position) {
        new Thread(() -> {
            PollDAO pollDAO = new PollDAO();
            boolean success = pollDAO.deletePoll(pollId, userId);

            runOnUiThread(() -> {
                if (success) {
                    pollAdapter.removeItem(position);
                    Toast.makeText(this, R.string.poll_deleted_success, Toast.LENGTH_SHORT).show();
                    if (pollList.isEmpty()) {
                        Toast.makeText(this, R.string.no_polls_created, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, R.string.poll_deleted_error, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyPolls();
    }
}