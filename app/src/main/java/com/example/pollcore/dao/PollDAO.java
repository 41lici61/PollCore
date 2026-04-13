package com.example.pollcore.dao;

import android.util.Log;

import com.example.pollcore.connection.ConexionBBDD;
import com.example.pollcore.models.Poll;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PollDAO {

    private Connection conn;

    public PollDAO() {
        conn = new ConexionBBDD().conectar();
    }

    // CREAR ENCUESTA
    public boolean create(Poll p) {

        String sql = "INSERT INTO pollcore.polls " +
                "(id_user, title, description, question, option1, option2, option3, option4, is_anonymous) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getIdUser());
            ps.setString(2, p.getTitle());
            ps.setString(3, p.getDescription());
            ps.setString(4, p.getQuestion());
            ps.setString(5, p.getOption1());
            ps.setString(6, p.getOption2());
            ps.setString(7, p.getOption3());
            ps.setString(8, p.getOption4());
            ps.setBoolean(9, p.isAnonymous());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            Log.e("SQL", "create poll error", e);
            return false;
        }
    }

    // FEED
    public List<Poll> getFeed() {

        List<Poll> list = new ArrayList<>();
        String sql = "SELECT * FROM pollcore.polls ORDER BY created_at DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapPoll(rs));
            }

        } catch (SQLException e) {
            Log.e("SQL", "feed error", e);
        }

        return list;
    }

    // DETALLE
    public Poll getById(int id) {

        String sql = "SELECT * FROM pollcore.polls WHERE id_poll=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapPoll(rs);

        } catch (SQLException e) {
            Log.e("SQL", "get poll error", e);
        }

        return null;
    }

    //Convierte una fila de la BBDD (ResultSet) en un objeto Poll. Metodo para reutilizar codigo.
    private Poll mapPoll(ResultSet rs) throws SQLException {
        return new Poll(
                rs.getInt("id_poll"),
                rs.getInt("id_user"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("question"),
                rs.getString("option1"),
                rs.getString("option2"),
                rs.getString("option3"),
                rs.getString("option4"),
                rs.getInt("count_option1"),
                rs.getInt("count_option2"),
                rs.getInt("count_option3"),
                rs.getInt("count_option4"),
                rs.getInt("total_votes"),
                rs.getBoolean("is_anonymous"),
                rs.getTimestamp("created_at")
        );
    }
}