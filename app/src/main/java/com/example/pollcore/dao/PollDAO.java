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
    public Poll getById(int pollId) {
        String sql = "SELECT * FROM pollcore.polls WHERE id_poll=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pollId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapPoll(rs);
            }
        } catch (SQLException e) {
            Log.e("SQL", "get poll error", e);
        }
        return null;
    }

    public List<Poll> getPollsByUser(int userId) {
        List<Poll> list = new ArrayList<>();
        String sql = "SELECT * FROM pollcore.polls WHERE id_user=? ORDER BY created_at DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapPoll(rs));
            }
        } catch (SQLException e) {
            Log.e("SQL", "get polls by user error", e);
        }
        return list;
    }

    public boolean deletePoll(int pollId, int userId) {
        String checkSql = "SELECT id_user FROM pollcore.polls WHERE id_poll=?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, pollId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int ownerId = rs.getInt("id_user");
                if (ownerId != userId) {
                    return false;
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            Log.e("SQL", "check poll owner error", e);
            return false;
        }

        String deleteSql = "DELETE FROM pollcore.polls WHERE id_poll=?";
        try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setInt(1, pollId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            Log.e("SQL", "delete poll error", e);
            return false;
        }
    }

    private void updateCounters(int pollId, int selectedOption) {
        String sql = "UPDATE pollcore.polls SET " +
                "count_option" + selectedOption + " = count_option" + selectedOption + " + 1, " +
                "total_votes = total_votes + 1 " +
                "WHERE id_poll=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pollId);
            ps.executeUpdate();
        } catch (SQLException e) {
            Log.e("SQL", "update counters error", e);
        }
    }

    public boolean vote(int userId, int pollId, int selectedOption) {
        String checkSql = "SELECT * FROM pollcore.votes WHERE id_user=? AND id_poll=?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, pollId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return false;
            }
        } catch (SQLException e) {
            Log.e("SQL", "check vote error", e);
            return false;
        }

        String insertSql = "INSERT INTO pollcore.votes (id_user, id_poll, selected_option) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, pollId);
            ps.setInt(3, selectedOption);
            ps.executeUpdate();

            // Update counters
            updateCounters(pollId, selectedOption);

            return true;
        } catch (SQLException e) {
            Log.e("SQL", "vote error", e);
            return false;
        }
    }

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

    //results
    public int[] getPollResults(int pollId) {
        String sql = "SELECT count_option1, count_option2, count_option3, count_option4, total_votes FROM pollcore.polls WHERE id_poll=?";
        int[] results = new int[5]; // [option1, option2, option3, option4, totalVotes]

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pollId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                results[0] = rs.getInt("count_option1");
                results[1] = rs.getInt("count_option2");
                results[2] = rs.getInt("count_option3");
                results[3] = rs.getInt("count_option4");
                results[4] = rs.getInt("total_votes");
            }
        } catch (SQLException e) {
            Log.e("SQL", "get poll results error", e);
        }

        return results;
    }

    public boolean hasUserVoted(int userId, int pollId) {
        String sql = "SELECT * FROM pollcore.votes WHERE id_user=? AND id_poll=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, pollId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            Log.e("SQL", "check user voted error", e);
            return false;
        }
    }
}