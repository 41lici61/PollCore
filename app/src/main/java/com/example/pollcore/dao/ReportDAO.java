package com.example.pollcore.dao;

import android.util.Log;

import com.example.pollcore.connection.ConexionBBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReportDAO {

    private Connection conn;

    public ReportDAO() {
        conn = new ConexionBBDD().conectar();
    }

    // REPORTAR ENCUESTA
    public boolean reportPoll(int userId, int pollId, String reason, String details) {

        String sql = "INSERT INTO pollcore.poll_reports (id_poll, id_user, reason, details) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pollId);
            ps.setInt(2, userId);
            ps.setString(3, reason);
            ps.setString(4, details);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            Log.e("SQL", "report poll error", e);
            return false;
        }
    }

    // REPORTAR COMENTARIO
    public boolean reportComment(int userId, int commentId, String reason, String details) {

        String sql = "INSERT INTO pollcore.comment_reports (id_comment, id_user, reason, details) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, commentId);
            ps.setInt(2, userId);
            ps.setString(3, reason);
            ps.setString(4, details);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            Log.e("SQL", "report comment error", e);
            return false;
        }
    }
}