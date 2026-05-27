package com.example.pollcore.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.pollcore.R;
import com.example.pollcore.dao.UserDAO;
import com.example.pollcore.models.User;
import com.example.pollcore.security.SecurityUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvCurrentUsername, tvCurrentEmail, tvCurrentPassword;
    private SwitchMaterial swPrivateProfile;
    private Button btnEditUsername, btnEditEmail, btnEditPassword, btnConfirmChanges, btnDeleteAccount;

    private UserDAO userDAO;
    private User currentUser;
    private int userId;
    private String newUsername = null;
    private String newEmail = null;
    private String newPassword = null;
    private String newPasswordPlain = null;
    private Boolean newIsPrivate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

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

        userDAO = new UserDAO();
        cargarUsuario();

        initializeViews();

        setupListeners();
    }

    private void cargarUsuario() {
        currentUser = userDAO.getById(userId);
        if (currentUser == null) {
            Toast.makeText(this, R.string.error_loading_user, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        tvCurrentUsername = findViewById(R.id.tvCurrentUsername);
        tvCurrentEmail = findViewById(R.id.tvCurrentEmail);
        tvCurrentPassword = findViewById(R.id.tvCurrentPassword);
        swPrivateProfile = findViewById(R.id.swPrivateProfile);
        btnEditUsername = findViewById(R.id.btnEditUsername);
        btnEditEmail = findViewById(R.id.btnEditEmail);
        btnEditPassword = findViewById(R.id.btnEditPassword);
        btnConfirmChanges = findViewById(R.id.btnConfirmChanges);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        if (currentUser != null) {
            tvCurrentUsername.setText(currentUser.getUsername());
            tvCurrentEmail.setText(currentUser.getEmail());
            tvCurrentPassword.setText("********");
            swPrivateProfile.setChecked(currentUser.isPrivate());
        }
    }

    private void setupListeners() {
        btnEditUsername.setOnClickListener(v -> mostrarDialogoEditarUsername());
        btnEditEmail.setOnClickListener(v -> mostrarDialogoEditarEmail());
        btnEditPassword.setOnClickListener(v -> mostrarDialogoEditarPassword());
        btnConfirmChanges.setOnClickListener(v -> confirmarCambios());
        btnDeleteAccount.setOnClickListener(v -> mostrarDialogoEliminarCuenta());
    }

    private void mostrarDialogoEditarUsername() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_username_title);

        final EditText input = new EditText(this);
        input.setText(currentUser.getUsername());
        input.setHint(R.string.new_username_hint);
        builder.setView(input);

        builder.setPositiveButton(R.string.accept, (dialog, which) -> {
            String nuevoUsername = input.getText().toString().trim();
            if (!nuevoUsername.isEmpty() && nuevoUsername.length() >= 3) {
                newUsername = nuevoUsername;
                tvCurrentUsername.setText(newUsername + " (" + getString(R.string.pending) + ")");
                Toast.makeText(this, R.string.username_pending, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.error_username_short, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void mostrarDialogoEditarEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_email_title);

        final EditText input = new EditText(this);
        input.setText(currentUser.getEmail());
        input.setHint("nuevo@email.com");
        builder.setView(input);

        builder.setPositiveButton(R.string.accept, (dialog, which) -> {
            String nuevoEmail = input.getText().toString().trim();
            if (!nuevoEmail.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoEmail).matches()) {
                newEmail = nuevoEmail;
                tvCurrentEmail.setText(newEmail + " (" + getString(R.string.pending) + ")");
                Toast.makeText(this, R.string.email_pending, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.error_email_invalid, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void mostrarDialogoEditarPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_password_title);

        final EditText inputPass = new EditText(this);
        inputPass.setHint(R.string.new_password_hint);
        inputPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText inputConfirm = new EditText(this);
        inputConfirm.setHint(R.string.confirm_password_hint);
        inputConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        layout.addView(inputPass);
        layout.addView(inputConfirm);
        builder.setView(layout);

        builder.setPositiveButton(R.string.accept, (dialog, which) -> {
            String nuevaPass = inputPass.getText().toString().trim();
            String confirmPass = inputConfirm.getText().toString().trim();

            if (nuevaPass.isEmpty()) {
                Toast.makeText(this, R.string.error_password_required, Toast.LENGTH_SHORT).show();
            } else if (nuevaPass.length() < 6) {
                Toast.makeText(this, R.string.error_password_short, Toast.LENGTH_SHORT).show();
            } else if (!nuevaPass.equals(confirmPass)) {
                Toast.makeText(this, R.string.error_password_mismatch, Toast.LENGTH_SHORT).show();
            } else {
                // ✅ Aplicar hash SHA-256 a la nueva contraseña
                newPassword = SecurityUtils.hashPasswordSimple(nuevaPass);
                newPasswordPlain = nuevaPass; // Guardamos temporalmente para el resumen
                tvCurrentPassword.setText("******** (" + getString(R.string.pending) + ")");
                Toast.makeText(this, R.string.password_pending, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void confirmarCambios() {
        boolean hayCambios = (newUsername != null || newEmail != null || newPassword != null ||
                swPrivateProfile.isChecked() != currentUser.isPrivate());

        if (!hayCambios) {
            Toast.makeText(this, R.string.no_changes_pending, Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder mensaje = new StringBuilder(getString(R.string.confirm_changes_message) + "\n\n");
        if (newUsername != null) mensaje.append("• Username: ").append(currentUser.getUsername()).append(" → ").append(newUsername).append("\n");
        if (newEmail != null) mensaje.append("• Email: ").append(currentUser.getEmail()).append(" → ").append(newEmail).append("\n");
        if (newPassword != null) mensaje.append("• ").append(getString(R.string.password_label)).append(": *******\n");
        if (swPrivateProfile.isChecked() != currentUser.isPrivate()) {
            mensaje.append("• ").append(getString(R.string.privacy_label)).append(": → ").append(swPrivateProfile.isChecked() ? getString(R.string.privacy_private) : getString(R.string.privacy_public)).append("\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_changes_title)
                .setMessage(mensaje.toString())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> guardarCambios())
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.dismiss())
                .setNeutralButton(R.string.view_details, (dialog, which) -> {
                    Toast.makeText(this, R.string.review_changes, Toast.LENGTH_LONG).show();
                })
                .show();
    }

    private void guardarCambios() {
        if (newUsername != null) currentUser.setUsername(newUsername);
        if (newEmail != null) currentUser.setEmail(newEmail);
        if (newPassword != null) currentUser.setPasswordHash(newPassword);  // Guarda el hash
        currentUser.setPrivate(swPrivateProfile.isChecked());

        boolean exito = userDAO.updateUser(currentUser);

        if (exito) {
            Toast.makeText(this, R.string.profile_update_success, Toast.LENGTH_SHORT).show();

            newUsername = null;
            newEmail = null;
            newPassword = null;
            newPasswordPlain = null;

            tvCurrentUsername.setText(currentUser.getUsername());
            tvCurrentEmail.setText(currentUser.getEmail());
            tvCurrentPassword.setText("********");

            Intent resultIntent = new Intent();
            resultIntent.putExtra("usuario_actualizado", true);
            setResult(RESULT_OK, resultIntent);
        } else {
            Toast.makeText(this, R.string.profile_update_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoEliminarCuenta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_account_title)
                .setMessage(R.string.delete_account_warning)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(R.string.delete, (dialog, which) -> mostrarDialogoConfirmacionFinal())
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                    Toast.makeText(this, R.string.deletion_cancelled, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNeutralButton(R.string.more_info, (dialog, which) -> {
                    Toast.makeText(this, R.string.deletion_info, Toast.LENGTH_LONG).show();
                });
        builder.show();
    }

    private void mostrarDialogoConfirmacionFinal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.final_confirmation_title)
                .setMessage(R.string.final_confirmation_message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false);

        final EditText input = new EditText(this);
        input.setHint(R.string.confirmation_text_hint);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.confirm_deletion, (dialog, which) -> {
            String confirmacion = input.getText().toString().trim();
            if (confirmacion.equals("ELIMINAR")) {
                eliminarCuenta();
            } else {
                Toast.makeText(this, R.string.incorrect_text, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            Toast.makeText(this, R.string.deletion_cancelled, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        builder.show();
    }

    private void eliminarCuenta() {
        AlertDialog progresoDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.deleting_account)
                .setMessage(R.string.please_wait)
                .setCancelable(false)
                .create();
        progresoDialog.show();

        new Thread(() -> {
            boolean exito = userDAO.deleteUser(userId);

            runOnUiThread(() -> {
                progresoDialog.dismiss();

                if (exito) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle(R.string.account_deleted_title)
                            .setMessage(R.string.account_deleted_message)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setCancelable(false)
                            .setPositiveButton(R.string.exit, (dialog, which) -> {
                                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle(R.string.error)
                            .setMessage(R.string.delete_account_error)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(R.string.dialog_ok, null);
                    builder.show();
                }
            });
        }).start();
    }
}