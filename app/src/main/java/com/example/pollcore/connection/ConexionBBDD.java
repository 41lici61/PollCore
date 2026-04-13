package com.example.pollcore.connection;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBBDD {

    private static final String TAG = "ConexionBBDD";

    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://10.0.2.2:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "12020206";

    private Connection connection;

    public Connection conectar() {

        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Driver no encontrado", e);
            return null;
        }

        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Log.i(TAG, "Conectado a PollCore");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error conexión", e);
            return null;
        }

        return connection;
    }

    public void cerrar() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Log.i(TAG, "Conexión cerrada");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error cerrando conexión", e);
        }
    }
}