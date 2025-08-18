package edu.pahana.service.dao;

import edu.pahana.service.model.Customer;
import edu.pahana.service.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // ----- Existing: list all -----
    public List<Customer> list() throws SQLException {
        List<Customer> out = new ArrayList<>();
        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id, account_number, name, address, phone FROM customers ORDER BY id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Customer(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)));
            }
        }
        return out;
    }

    // ----- Existing: get by id -----
    public Customer get(int id) throws SQLException {
        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id, account_number, name, address, phone FROM customers WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5));
                }
            }
        }
        return null;
    }

    // ----- Existing: create -----
    public int create(Customer c) throws SQLException {
        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO customers(account_number, name, address, phone) VALUES(?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getAccountNumber());
            ps.setString(2, c.getName());
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getPhone());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ----- Existing: update -----
    public boolean update(Customer c) throws SQLException {
        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE customers SET account_number=?, name=?, address=?, phone=? WHERE id=?")) {
            ps.setString(1, c.getAccountNumber());
            ps.setString(2, c.getName());
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getPhone());
            ps.setInt(5, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ----- Existing: delete -----
    public boolean delete(int id) throws SQLException {
        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement("DELETE FROM customers WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== NEW: search + pagination support =====

    /** Count customers matching an optional query (by account_number, name, or phone). */
    public int count(String q) throws SQLException {
        String where = (q == null || q.isBlank()) ? "" :
                " WHERE account_number LIKE ? OR name LIKE ? OR phone LIKE ?";
        String sql = "SELECT COUNT(*) FROM customers" + where;

        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            if (!where.isEmpty()) {
                String like = "%" + q.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /**
     * Paged list with optional search query.
     * @param q optional search (matches account_number, name, phone)
     * @param offset zero-based row offset
     * @param limit max rows to return
     */
    public List<Customer> listPaged(String q, int offset, int limit) throws SQLException {
        String where = (q == null || q.isBlank()) ? "" :
                " WHERE account_number LIKE ? OR name LIKE ? OR phone LIKE ?";
        String sql = "SELECT id, account_number, name, address, phone FROM customers"
                + where
                + " ORDER BY id DESC LIMIT ?, ?";

        try (Connection con = DB.get().connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            if (!where.isEmpty()) {
                String like = "%" + q.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx, limit);

            try (ResultSet rs = ps.executeQuery()) {
                List<Customer> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new Customer(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5)));
                }
                return out;
            }
        }
    }
}
