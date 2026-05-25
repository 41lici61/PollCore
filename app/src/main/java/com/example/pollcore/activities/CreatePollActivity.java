package com.example.pollcore.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CreatePollActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription, etQuestion;
    private TextInputEditText etOption1, etOption2, etOption3, etOption4;
    private CheckBox cbAnonymous;
    private MaterialButton btnCreatePoll;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_poll);

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
        userId = intent.getIntExtra("usuario_id", -1);

        if (userId == -1) {
            Toast.makeText(this, R.string.error_user_not_identified, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();

        btnCreatePoll.setOnClickListener(v -> mostrarDialogoResumen());
    }

    private void initializeViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etQuestion = findViewById(R.id.etQuestion);
        etOption1 = findViewById(R.id.etOption1);
        etOption2 = findViewById(R.id.etOption2);
        etOption3 = findViewById(R.id.etOption3);
        etOption4 = findViewById(R.id.etOption4);
        cbAnonymous = findViewById(R.id.cbAnonymous);
        btnCreatePoll = findViewById(R.id.btnCreatePoll);
    }

    private boolean validarCampos() {
        String title = etTitle.getText().toString().trim();
        String question = etQuestion.getText().toString().trim();
        String option1 = etOption1.getText().toString().trim();
        String option2 = etOption2.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError(getString(R.string.create_poll_title_required));
            etTitle.requestFocus();
            return false;
        }

        if (question.isEmpty()) {
            etQuestion.setError(getString(R.string.create_poll_question_required));
            etQuestion.requestFocus();
            return false;
        }

        if (option1.isEmpty()) {
            etOption1.setError(getString(R.string.create_poll_option1_required));
            etOption1.requestFocus();
            return false;
        }

        if (option2.isEmpty()) {
            etOption2.setError(getString(R.string.create_poll_option2_required));
            etOption2.requestFocus();
            return false;
        }

        return true;
    }

    private void mostrarDialogoResumen() {
        if (!validarCampos()) {
            return;
        }

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String question = etQuestion.getText().toString().trim();
        String option1 = etOption1.getText().toString().trim();
        String option2 = etOption2.getText().toString().trim();
        String option3 = etOption3.getText().toString().trim();
        String option4 = etOption4.getText().toString().trim();
        boolean isAnonymous = cbAnonymous.isChecked();

        StringBuilder resumen = new StringBuilder();
        resumen.append("📋 ").append(getString(R.string.create_poll_title_hint)).append(": ").append(title).append("\n\n");
        if (!description.isEmpty()) {
            resumen.append("📝 ").append(getString(R.string.create_poll_description_hint)).append(": ").append(description).append("\n\n");
        }
        resumen.append("❓ ").append(getString(R.string.create_poll_question_hint)).append(": ").append(question).append("\n\n");
        resumen.append(getString(R.string.poll_options)).append(":\n");
        resumen.append("   1. ").append(option1).append("\n");
        resumen.append("   2. ").append(option2).append("\n");
        if (!option3.isEmpty()) {
            resumen.append("   3. ").append(option3).append("\n");
        }
        if (!option4.isEmpty()) {
            resumen.append("   4. ").append(option4).append("\n");
        }
        resumen.append("\n🔒 ").append(getString(R.string.create_poll_anonymous)).append(": ").append(isAnonymous ? getString(R.string.yes) : getString(R.string.no));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_poll_title)
                .setMessage(resumen.toString())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.create_poll_button, (dialog, which) -> crearEncuesta())
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.dismiss())
                .setNeutralButton(R.string.poll_edit_fields_button, (dialog, which) -> {
                    Toast.makeText(this, R.string.poll_edit_fields, Toast.LENGTH_SHORT).show();
                });
        builder.show();
    }

    private void crearEncuesta() {
        btnCreatePoll.setEnabled(false);
        btnCreatePoll.setText(R.string.poll_creating);

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String question = etQuestion.getText().toString().trim();
        String option1 = etOption1.getText().toString().trim();
        String option2 = etOption2.getText().toString().trim();
        String option3 = etOption3.getText().toString().trim();
        String option4 = etOption4.getText().toString().trim();
        boolean isAnonymous = cbAnonymous.isChecked();

        if (option3.isEmpty()) option3 = null;
        if (option4.isEmpty()) option4 = null;

        Poll newPoll = new Poll();
        newPoll.setIdUser(userId);
        newPoll.setTitle(title);
        newPoll.setDescription(description);
        newPoll.setQuestion(question);
        newPoll.setOption1(option1);
        newPoll.setOption2(option2);
        newPoll.setOption3(option3);
        newPoll.setOption4(option4);
        newPoll.setAnonymous(isAnonymous);

        new Thread(() -> {
            PollDAO pollDAO = new PollDAO();
            boolean exito = pollDAO.create(newPoll);

            runOnUiThread(() -> {
                btnCreatePoll.setEnabled(true);
                btnCreatePoll.setText(R.string.create_poll_button);

                if (exito) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreatePollActivity.this);
                    builder.setTitle(R.string.poll_create_success_title)
                            .setMessage(R.string.poll_create_success_message)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton(R.string.poll_create_success_button, (dialog, which) -> {
                                finish();
                            })
                            .setNeutralButton(R.string.poll_create_another_button, (dialog, which) -> {
                                limpiarFormulario();
                            });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreatePollActivity.this);
                    builder.setTitle(R.string.poll_create_error_title)
                            .setMessage(R.string.poll_create_error_message)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(R.string.dialog_ok, null);
                    builder.show();
                }
            });
        }).start();
    }

    private void limpiarFormulario() {
        etTitle.setText("");
        etDescription.setText("");
        etQuestion.setText("");
        etOption1.setText("");
        etOption2.setText("");
        etOption3.setText("");
        etOption4.setText("");
        cbAnonymous.setChecked(false);
        etTitle.requestFocus();
    }
}