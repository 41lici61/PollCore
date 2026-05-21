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
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
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
            etTitle.setError("El título es requerido");
            /*solicita que ese componente reciba el foco de entrada del usuario, abre el teclado*/
            etTitle.requestFocus();
            return false;
        }

        if (question.isEmpty()) {
            etQuestion.setError("La pregunta es requerida");
            etQuestion.requestFocus();
            return false;
        }

        if (option1.isEmpty()) {
            etOption1.setError("La opción 1 es requerida");
            etOption1.requestFocus();
            return false;
        }

        if (option2.isEmpty()) {
            etOption2.setError("La opción 2 es requerida");
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
        resumen.append("📋 Título: ").append(title).append("\n\n");
        if (!description.isEmpty()) {
            resumen.append("📝 Descripción: ").append(description).append("\n\n");
        }
        resumen.append("❓ Pregunta: ").append(question).append("\n\n");
        resumen.append("📌 Opciones:\n");
        resumen.append("   1. ").append(option1).append("\n");
        resumen.append("   2. ").append(option2).append("\n");
        if (!option3.isEmpty()) {
            resumen.append("   3. ").append(option3).append("\n");
        }
        if (!option4.isEmpty()) {
            resumen.append("   4. ").append(option4).append("\n");
        }
        resumen.append("\n🔒 Anónima: ").append(isAnonymous ? "Sí" : "No");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar creación de encuesta")
                .setMessage(resumen.toString())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Crear Encuesta", (dialog, which) -> crearEncuesta())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Editar", (dialog, which) -> {
                    Toast.makeText(this, "Puedes editar los campos", Toast.LENGTH_SHORT).show();
                });
        builder.show();
    }

    private void crearEncuesta() {
        btnCreatePoll.setEnabled(false);
        btnCreatePoll.setText("Creando...");

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String question = etQuestion.getText().toString().trim();
        String option1 = etOption1.getText().toString().trim();
        String option2 = etOption2.getText().toString().trim();
        String option3 = etOption3.getText().toString().trim();
        String option4 = etOption4.getText().toString().trim();
        boolean isAnonymous = cbAnonymous.isChecked();//no sirve para nada ahora mismo (a checkear 💅)

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
                btnCreatePoll.setText("Crear Encuesta");

                if (exito) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreatePollActivity.this);
                    builder.setTitle("¡Encuesta creada!")
                            .setMessage("Tu encuesta ha sido publicada exitosamente.")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("Ver encuestas", (dialog, which) -> {

                                finish();
                            })
                            .setNeutralButton("Crear otra", (dialog, which) -> {

                                limpiarFormulario();
                            });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreatePollActivity.this);
                    builder.setTitle("Error")
                            .setMessage("No se pudo crear la encuesta. Por favor, inténtalo de nuevo.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Aceptar", null);
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