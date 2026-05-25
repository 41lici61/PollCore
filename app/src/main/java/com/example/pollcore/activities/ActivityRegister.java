package com.example.pollcore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pollcore.R;
import com.example.pollcore.dao.UserDAO;
import com.example.pollcore.models.User;

public class ActivityRegister extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Switch swPrivateAccount;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        swPrivateAccount = findViewById(R.id.swPrivateAccount);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registrarUsuario());
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void registrarUsuario() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, R.string.register_error_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, R.string.register_error_email_invalid, Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.register_error_password_mismatch, Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, R.string.register_error_password_short, Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }

        if (username.length() < 3) {
            Toast.makeText(this, R.string.register_error_username_short, Toast.LENGTH_SHORT).show();
            etUsername.requestFocus();
            return;
        }

        User user = new User(username, email, password);
        user.setPrivate(swPrivateAccount.isChecked());

        UserDAO dao = new UserDAO();
        boolean insertado = dao.register(user);

        if (insertado) {
            Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, R.string.register_error, Toast.LENGTH_SHORT).show();
        }
    }
}