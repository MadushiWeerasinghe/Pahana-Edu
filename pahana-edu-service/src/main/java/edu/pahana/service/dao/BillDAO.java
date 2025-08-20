package edu.pahana.service.dao;

import edu.pahana.service.model.Bill;
import edu.pahana.service.model.BillItem;
import edu.pahana.service.util.DB;

import java.sql.*;
import java.time.LocalDateTime;

public class BillDAO {

    public int create(Bill bill) throws SQLException {
        try (Connection con = DB.get().connect()) {
            con.setAutoCommit(false);
            try {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO bills(customer_id, created_at, sub_total, tax, grand_total) VALUES(?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setInt(1, bill.getCustomerId());
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setDouble(3, bill.getSubTotal());
                ps.setDouble(4, bill.getTax());
                ps.setDouble(5, bill.getGrandTotal());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                int billId = -1;
                if (rs.next()) billId = rs.getInt(1);
                rs.close();
                ps.close();

                PreparedStatement psi = con.prepareStatement(
                        "INSERT INTO bill_items(bill_id, item_id, qty, unit_price, description) VALUES(?,?,?,?,?)"
                );
                for (BillItem bi : bill.getItems()) {
                    psi.setInt(1, billId);
                    psi.setInt(2, bi.getItemId());
                    psi.setInt(3, bi.getQuantity());
                    psi.setDouble(4, bi.getUnitPrice());
                    psi.setString(5, bi.getDescription());
                    psi.addBatch();
                }
                psi.executeBatch();
                psi.close();

                con.commit();
                return billId;
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /** Fetch a bill with its items by id (used for print/PDF). */
    public Bill get(int id) throws SQLException {
        try (Connection con = DB.get().connect()) {
            Bill b = null;

            // Fetch bill header
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id, customer_id, created_at, sub_total, tax, grand_total FROM bills WHERE id=?"
            )) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        b = new Bill();
                        b.setId(rs.getInt("id"));
                        b.setCustomerId(rs.getInt("customer_id"));
                        Timestamp ts = rs.getTimestamp("created_at");
                        if (ts != null) {
                            b.setCreatedAt(ts.toLocalDateTime());
                        }
                        b.setSubTotal(rs.getDouble("sub_total"));
                        b.setTax(rs.getDouble("tax"));
                        b.setGrandTotal(rs.getDouble("grand_total"));
                    }
                }
            }

            if (b == null) return null; // not found

            // Fetch bill items
            try (PreparedStatement psi = con.prepareStatement(
                    "SELECT item_id, qty, unit_price, description FROM bill_items WHERE bill_id=?"
            )) {
                psi.setInt(1, id);
                try (ResultSet rs2 = psi.executeQuery()) {
                    while (rs2.next()) {
                        BillItem bi = new BillItem(
                                rs2.getInt("item_id"),
                                rs2.getInt("qty"),
                                rs2.getDouble("unit_price"),
                                rs2.getString("description")
                        );
                        b.getItems().add(bi);
                    }
                }
            }

            return b;
        }
    }
}
