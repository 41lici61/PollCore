package com.example.pollcore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pollcore.R;
import com.example.pollcore.adapters.PollAdapter;
import com.example.pollcore.dao.PollDAO;
import com.example.pollcore.models.Poll;

import java.util.ArrayList;
import java.util.List;

public class PantallaPrincipal extends AppCompatActivity {

    private RecyclerView rvPolls;
    private PollAdapter pollAdapter;
    private List<Poll> pollList;
    private List<Poll> pollListFull;
    private String username;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("PollCore");
        }

        Intent intent = getIntent();
        username = intent.getStringExtra("usuario_username");
        userId = intent.getIntExtra("usuario_id", -1);  // Recibir el ID del usuario

        if (username == null) {
            username = "usuario";
        }

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
        }

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("¡Bienvenido/a, " + username + "!");

        rvPolls = findViewById(R.id.rvPolls);
        rvPolls.setLayoutManager(new LinearLayoutManager(this));
        pollList = new ArrayList<>();
        pollListFull = new ArrayList<>();
        //pollAdapter = new PollAdapter(pollList, this);
        rvPolls.setAdapter(pollAdapter);
        cargarEncuestas();
    }

    private void cargarEncuestas() {
        PollDAO pollDAO = new PollDAO();
        List<Poll> polls = pollDAO.getFeed();

        if (polls != null && !polls.isEmpty()) {
            pollList.clear();
            pollList.addAll(polls);
            pollListFull.clear();
            pollListFull.addAll(polls);

            pollAdapter = new PollAdapter(pollList, this, pollId -> {
                Intent intent = new Intent(PantallaPrincipal.this, PollDetailActivity.class);
                intent.putExtra("poll_id", pollId);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            });
            rvPolls.setAdapter(pollAdapter);
        } else {
            Toast.makeText(this, "No hay encuestas disponibles", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pantalla_principal, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Buscar encuestas...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarEncuestas(newText);
                return true;
            }
        });

        return true;
    }

    private void filtrarEncuestas(String texto) {
        List<Poll> listaFiltrada = new ArrayList<>();
        if (texto == null || texto.isEmpty()) {
            listaFiltrada.addAll(pollListFull);
        } else {
            for (Poll poll : pollListFull) {
                if (poll.getTitle().toLowerCase().contains(texto.toLowerCase())) {
                    listaFiltrada.add(poll);
                }
            }
        }
        pollAdapter.updateList(listaFiltrada);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(PantallaPrincipal.this, SettingsActivity.class);
            intent.putExtra("usuario_id", userId);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_create_poll) {
            Intent intent = new Intent(PantallaPrincipal.this, CreatePollActivity.class);
            intent.putExtra("usuario_id", userId);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            cerrarSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cerrarSesion() {
        Intent intent = new Intent(PantallaPrincipal.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEncuestas();
    }
}