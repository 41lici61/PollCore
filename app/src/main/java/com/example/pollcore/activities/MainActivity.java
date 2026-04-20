package com.example.pollcore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pollcore.R;
import com.example.pollcore.dao.UserDAO;
import com.example.pollcore.models.User;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivityRegister.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> loginUsuario());
    }

    private void loginUsuario() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.login(email, password);

        if (user != null) {
            Toast.makeText(this, "Bienvenido " + user.getUsername(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, PantallaPrincipal.class);
            intent.putExtra("usuario_id", user.getIdUser());
            intent.putExtra("usuario_username", user.getUsername());
            intent.putExtra("usuario_email", user.getEmail());

            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
}