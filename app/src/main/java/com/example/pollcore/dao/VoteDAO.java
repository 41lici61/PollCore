package com.example.pollcore.dao;

import android.util.Log;

import com.example.pollcore.connection.ConexionBBDD;

import java.sql.*;

public class VoteDAO {

    private Connection conn;

    public VoteDAO() {
        conn = new ConexionBBDD().conectar();
    }

    public boolean vote(int userId, int pollId, int option) {

        boolean yaVoto = false;

        try {
            // comprobar
            String check = "SELECT * FROM pollcore.votes WHERE id_user=? AND id_poll=?";
            PreparedStatement ps = conn.prepareStatement(check);
            ps.setInt(1, userId);
            ps.setInt(2, pollId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                yaVoto = true;
            }

            if (yaVoto) return false;

            // insertar voto
            String insert = "INSERT INTO pollcore.votes (id_user, id_poll, selected_option) VALUES (?, ?, ?)";
            PreparedStatement ps2 = conn.prepareStatement(insert);
            ps2.setInt(1, userId);
            ps2.setInt(2, pollId);
            ps2.setInt(3, option);
            ps2.executeUpdate();

            return true;

        } catch (SQLException e) {
            Log.e("SQL", "vote error", e);
            return false;
        }
    }
}