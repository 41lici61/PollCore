package com.example.pollcore.dao;

import android.util.Log;

import com.example.pollcore.connection.ConexionBBDD;
import com.example.pollcore.models.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    private Connection conn;

    public CommentDAO() {
        conn = new ConexionBBDD().conectar();
    }

    // CREAR COMENTARIO
    public boolean create(Comment c) {

        String sql = "INSERT INTO pollcore.comments (id_poll, id_user, content, reply_to) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, c.getIdPoll());
            ps.setInt(2, c.getIdUser());
            ps.setString(3, c.getContent());

            if (c.getReplyTo() != null)
                ps.setInt(4, c.getReplyTo());
            else
                ps.setNull(4, Types.INTEGER);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);

                // añadir a replies_ids
                if (c.getReplyTo() != null) {
                    String update = "UPDATE pollcore.comments SET replies_ids = array_append(replies_ids, ?) WHERE id_comment=?";
                    try (PreparedStatement ps2 = conn.prepareStatement(update)) {
                        ps2.setInt(1, newId);
                        ps2.setInt(2, c.getReplyTo());
                        ps2.executeUpdate();
                    }
                }
            }

            return true;

        } catch (SQLException e) {
            Log.e("SQL", "comment error", e);
            return false;
        }
    }

    // OBTENER COMENTARIOS POR ENCUESTA
    public List<Comment> getByPoll(int pollId) {

        List<Comment> list = new ArrayList<>();

        String sql = "SELECT * FROM pollcore.comments WHERE id_poll=? ORDER BY created_at ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pollId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Comment(
                        rs.getInt("id_comment"),
                        rs.getInt("id_poll"),
                        rs.getInt("id_user"),
                        rs.getString("content"),
                        (Integer) rs.getObject("reply_to"),
                        null,
                        rs.getTimestamp("created_at")
                ));
            }

        } catch (SQLException e) {
            Log.e("SQL", "get comments error", e);
        }

        return list;
    }
}