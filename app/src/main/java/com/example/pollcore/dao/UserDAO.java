package com.example.pollcore.dao;

import android.util.Log;

import com.example.pollcore.connection.ConexionBBDD;
import com.example.pollcore.models.User;

import java.sql.*;

public class UserDAO {

    private Connection conn;

    public UserDAO() {
        conn = new ConexionBBDD().conectar();
        if (conn == null) {
            Log.e("UsuarioDAO", "¡Error! La conexión a la BBDD es null");
        } else {
            Log.i("UsuarioDAO", "Conexión a la BBDD exitosa");
        }
    }

    // LOGIN

    public User login(String email, String password) {
        String sql = "SELECT * FROM pollcore.users WHERE email=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Obtener el hash
                String storedHash = rs.getString("password_hash");

                // Generar hash
                String hashedInput = com.example.pollcore.security.SecurityUtils.hashPasswordSimple(password);

                // Comparar hashes
                if (storedHash.equals(hashedInput)) {
                    Array arr = rs.getArray("answered_polls");
                    Integer[] answered = arr != null ? (Integer[]) arr.getArray() : new Integer[]{};

                    return new User(
                            rs.getInt("id_user"),
                            rs.getString("username"),
                            rs.getString("email"),
                            storedHash,
                            rs.getBoolean("is_private"),
                            answered,
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (Exception e) {
            Log.e("SQL", "login error", e);
        }
        return null;
    }
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM pollcore.users WHERE id_user=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            Log.e("SQL", "delete user error", e);
            return false;
        }
    }

    public boolean updateUser(User u) {
        String sql = "UPDATE pollcore.users SET username=?, email=?, password_hash=?, is_private=? WHERE id_user=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setBoolean(4, u.isPrivate());
            ps.setInt(5, u.getIdUser());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            Log.e("SQL", "update user error", e);
            return false;
        }
    }

    // REGISTER

    public boolean register(User u) {
        String sql = "INSERT INTO pollcore.users (username, email, password_hash, is_private) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());

            // Hash de la contraseña antes de guardarla
            String hashedPassword = com.example.pollcore.security.SecurityUtils.hashPasswordSimple(u.getPasswordHash());
            ps.setString(3, hashedPassword);  // Guarda el hash, no la contraseña

            ps.setBoolean(4, u.isPrivate());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            Log.e("SQL", "register error", e);
            return false;
        }
    }

    // OBTENER USUARIO POR ID
    public User getById(int id) {

        String sql = "SELECT * FROM pollcore.users WHERE id_user=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getBoolean("is_private"),
                        null,
                        rs.getTimestamp("created_at")
                );
            }

        } catch (SQLException e) {
            Log.e("SQL", "getById error", e);
        }

        return null;
    }
}