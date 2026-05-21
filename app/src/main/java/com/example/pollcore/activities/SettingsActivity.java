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
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Error al cargar usuario", Toast.LENGTH_SHORT).show();
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
        builder.setTitle("Editar nombre de usuario");

        final EditText input = new EditText(this);
        input.setText(currentUser.getUsername());
        input.setHint("Nuevo nombre de usuario");
        builder.setView(input);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String nuevoUsername = input.getText().toString().trim();
            if (!nuevoUsername.isEmpty() && nuevoUsername.length() >= 3) {
                newUsername = nuevoUsername;
                tvCurrentUsername.setText(newUsername + " (pendiente)");
                Toast.makeText(this, "Nombre de usuario pendiente de confirmar", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El nombre debe tener al menos 3 caracteres", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void mostrarDialogoEditarEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar correo electrónico");

        final EditText input = new EditText(this);
        input.setText(currentUser.getEmail());
        input.setHint("nuevo@email.com");
        builder.setView(input);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String nuevoEmail = input.getText().toString().trim();
            if (!nuevoEmail.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoEmail).matches()) {
                newEmail = nuevoEmail;
                tvCurrentEmail.setText(newEmail + " (pendiente)");
                Toast.makeText(this, "Email pendiente de confirmar", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void mostrarDialogoEditarPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar contraseña");

        final EditText inputPass = new EditText(this);
        inputPass.setHint("Nueva contraseña");
        inputPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText inputConfirm = new EditText(this);
        inputConfirm.setHint("Confirmar contraseña");
        inputConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        layout.addView(inputPass);
        layout.addView(inputConfirm);
        builder.setView(layout);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String nuevaPass = inputPass.getText().toString().trim();
            String confirmPass = inputConfirm.getText().toString().trim();

            if (nuevaPass.isEmpty()) {
                Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
            } else if (nuevaPass.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            } else if (!nuevaPass.equals(confirmPass)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else {
                newPassword = nuevaPass;
                tvCurrentPassword.setText("******** (pendiente)");
                Toast.makeText(this, "Contraseña pendiente de confirmar", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void confirmarCambios() {
        boolean hayCambios = (newUsername != null || newEmail != null || newPassword != null ||
                swPrivateProfile.isChecked() != currentUser.isPrivate());

        if (!hayCambios) {
            Toast.makeText(this, "No hay cambios pendientes", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder mensaje = new StringBuilder("¿Confirmar los siguientes cambios?\n\n");
        if (newUsername != null) mensaje.append("• Username: ").append(currentUser.getUsername()).append(" → ").append(newUsername).append("\n");
        if (newEmail != null) mensaje.append("• Email: ").append(currentUser.getEmail()).append(" → ").append(newEmail).append("\n");
        if (newPassword != null) mensaje.append("• Contraseña: *******\n");
        if (swPrivateProfile.isChecked() != currentUser.isPrivate()) {
            mensaje.append("• Privacidad: → ").append(swPrivateProfile.isChecked() ? "Privada" : "Pública").append("\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar cambios")
                .setMessage(mensaje.toString())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Confirmar", (dialog, which) -> guardarCambios())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Ver detalles", (dialog, which) -> {
                    Toast.makeText(this, "Revisa los cambios antes de confirmar", Toast.LENGTH_LONG).show();
                })
                .show();
    }

    private void guardarCambios() {
        if (newUsername != null) currentUser.setUsername(newUsername);
        if (newEmail != null) currentUser.setEmail(newEmail);
        if (newPassword != null) currentUser.setPasswordHash(newPassword);
        currentUser.setPrivate(swPrivateProfile.isChecked());

        boolean exito = userDAO.updateUser(currentUser);

        if (exito) {
            Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();

            newUsername = null;
            newEmail = null;
            newPassword = null;

            tvCurrentUsername.setText(currentUser.getUsername());
            tvCurrentEmail.setText(currentUser.getEmail());
            tvCurrentPassword.setText("********");

            Intent resultIntent = new Intent();
            resultIntent.putExtra("usuario_actualizado", true);
            setResult(RESULT_OK, resultIntent);
        } else {
            Toast.makeText(this, "Error al guardar cambios", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoEliminarCuenta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Cuenta")
                .setMessage("¿Estás seguro de que quieres eliminar tu cuenta?\n\n"
                        + "⚠️ Esta acción es irreversible y perderás:\n"
                        + "• Todas tus encuestas creadas\n"
                        + "• Todos tus votos realizados\n"
                        + "• Todos tus comentarios\n"
                        + "• Todos tus datos personales")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("Eliminar", (dialog, which) -> mostrarDialogoConfirmacionFinal())
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    Toast.makeText(this, "Eliminación cancelada", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNeutralButton("Más información", (dialog, which) -> {
                    Toast.makeText(this, "Se eliminarán permanentemente todos tus datos de la plataforma", Toast.LENGTH_LONG).show();
                });
        builder.show();
    }

    private void mostrarDialogoConfirmacionFinal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación Final")
                .setMessage("ESTA ACCIÓN NO SE PUEDE DESHACER\n\n"
                        + "Para confirmar que deseas eliminar tu cuenta, escribe: ELIMINAR")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false);

        final EditText input = new EditText(this);
        input.setHint("Escribe ELIMINAR");
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Confirmar Eliminación", (dialog, which) -> {
            String confirmacion = input.getText().toString().trim();
            if (confirmacion.equals("ELIMINAR")) {
                eliminarCuenta();
            } else {
                Toast.makeText(this, "Texto incorrecto. Eliminación cancelada", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            Toast.makeText(this, "Eliminación cancelada", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        builder.show();
    }

    private void eliminarCuenta() {
        AlertDialog progresoDialog = new AlertDialog.Builder(this)
                .setTitle("Eliminando cuenta...")
                .setMessage("Por favor espera")
                .setCancelable(false)
                .create();
        progresoDialog.show();

        new Thread(() -> {
            boolean exito = userDAO.deleteUser(userId);

            runOnUiThread(() -> {
                progresoDialog.dismiss();

                if (exito) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle("Cuenta Eliminada")
                            .setMessage("Tu cuenta ha sido eliminada correctamente.\n\n"
                                    + "Lamentamos verte partir. ¡Esperamos verte de vuelta algún día!")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setCancelable(false)
                            .setPositiveButton("Salir", (dialog, which) -> {
                                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle("Error")
                            .setMessage("No se pudo eliminar la cuenta. Por favor, inténtalo de nuevo más tarde.")
                            .setIcon(android.R.drawable.ic_dialog_alert)//iconos de anroid disponibles
                            .setPositiveButton("Aceptar", null);
                    builder.show();
                }
            });
        }).start();
    }
}