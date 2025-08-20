package edu.pahana.service.dao;

import edu.pahana.service.model.Item;
import edu.pahana.service.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public List<Item> list() throws SQLException {
        List<Item> out = new ArrayList<>();
        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id, code, description, unit_price FROM items ORDER BY id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Item(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4)));
            }
        }
        return out;
    }

    public Item get(int id) throws SQLException {
        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id, code, description, unit_price FROM items WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Item(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4));
                }
            }
        }
        return null;
    }

    public int create(Item i) throws SQLException {
        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO items(code, description, unit_price) VALUES(?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, i.getCode());
            ps.setString(2, i.getDescription());
            ps.setDouble(3, i.getUnitPrice());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean update(Item i) throws SQLException {
        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE items SET code=?, description=?, unit_price=? WHERE id=?")) {
            ps.setString(1, i.getCode());
            ps.setString(2, i.getDescription());
            ps.setDouble(3, i.getUnitPrice());
            ps.setInt(4, i.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM items WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- NEW: Search + Pagination ----------

    /** Total number of items matching the optional query (code or description). */
    public int count(String q) throws SQLException {
        String where = (q == null || q.isBlank()) ? "" : " WHERE code LIKE ? OR description LIKE ?";
        String sql = "SELECT COUNT(*) FROM items" + where;
        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            if (!where.isEmpty()) {
                String like = "%" + q.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** Page of items filtered by optional query, ordered by newest first. */
    public List<Item> listPaged(String q, int offset, int limit) throws SQLException {
        String where = (q == null || q.isBlank()) ? "" : " WHERE code LIKE ? OR description LIKE ?";
        String sql = "SELECT id, code, description, unit_price FROM items"
                   + where + " ORDER BY id DESC LIMIT ?, ?";
        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            if (!where.isEmpty()) {
                String like = "%" + q.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx, limit);

            List<Item> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Item(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4)));
                }
            }
            return out;
        }
    }
}
