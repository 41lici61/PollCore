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

    public boolean createComment(Comment comment) {
        String sql = "INSERT INTO pollcore.comments (id_poll, id_user, content, reply_to) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, comment.getIdPoll());
            ps.setInt(2, comment.getIdUser());
            ps.setString(3, comment.getContent());

            if (comment.getReplyTo() != null) {
                ps.setInt(4, comment.getReplyTo());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);

                if (comment.getReplyTo() != null) {
                    String updateSql = "UPDATE pollcore.comments SET replies_ids = array_append(replies_ids, ?) WHERE id_comment=?";
                    try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                        ps2.setInt(1, newId);
                        ps2.setInt(2, comment.getReplyTo());
                        ps2.executeUpdate();
                    }
                }
            }
            return true;

        } catch (SQLException e) {
            Log.e("SQL", "create comment error", e);
            return false;
        }
    }

    public List<Comment> getCommentsByPoll(int pollId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.username FROM pollcore.comments c " +
                "JOIN pollcore.users u ON c.id_user = u.id_user " +
                "WHERE c.id_poll=? AND c.reply_to IS NULL " +
                "ORDER BY c.created_at ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pollId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Comment comment = mapComment(rs);
                comment.setUsername(rs.getString("username"));
                comment.setRepliesIds(getRepliesIds(comment.getIdComment()));
                comments.add(comment);
            }
        } catch (SQLException e) {
            Log.e("SQL", "get comments error", e);
        }
        return comments;
    }

    public List<Comment> getReplies(int commentId) {
        List<Comment> replies = new ArrayList<>();
        String sql = "SELECT c.*, u.username FROM pollcore.comments c " +
                "JOIN pollcore.users u ON c.id_user = u.id_user " +
                "WHERE c.reply_to=? ORDER BY c.created_at ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Comment reply = mapComment(rs);
                reply.setUsername(rs.getString("username"));
                replies.add(reply);
            }
        } catch (SQLException e) {
            Log.e("SQL", "get replies error", e);
        }
        return replies;
    }

    private Integer[] getRepliesIds(int commentId) {
        String sql = "SELECT replies_ids FROM pollcore.comments WHERE id_comment=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Array array = rs.getArray("replies_ids");
                if (array != null) {
                    return (Integer[]) array.getArray();
                }
            }
        } catch (SQLException e) {
            Log.e("SQL", "get replies ids error", e);
        }
        return new Integer[0];
    }

    private Comment mapComment(ResultSet rs) throws SQLException {
        return new Comment(
                rs.getInt("id_comment"),
                rs.getInt("id_poll"),
                rs.getInt("id_user"),
                rs.getString("content"),
                (Integer) rs.getObject("reply_to"),
                null,
                rs.getTimestamp("created_at")
        );
    }
}