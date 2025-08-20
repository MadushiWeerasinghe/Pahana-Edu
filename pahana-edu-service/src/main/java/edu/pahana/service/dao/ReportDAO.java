package edu.pahana.service.dao;
import edu.pahana.service.dto.AggregatePoint;
import edu.pahana.service.dto.BillSummaryResponse;
import edu.pahana.service.dto.CustomerReportRow;
import edu.pahana.service.util.DB;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public List<CustomerReportRow> customersAll() throws SQLException {
        String sql = "SELECT c.id, c.account_number, c.name, c.address, c.phone, c.created_at, " +
                     " (SELECT MAX(b.created_at) FROM bills b WHERE b.customer_id = c.id) AS last_bill_at " +
                     "FROM customers c ORDER BY c.name";
        try(Connection con = DB.get().connect();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            List<CustomerReportRow> out = new ArrayList<>();
            while(rs.next()){
                out.add(new CustomerReportRow(
                    rs.getInt("id"),
                    rs.getString("account_number"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getTimestamp("created_at").toInstant().toString(),
                    rs.getTimestamp("last_bill_at")==null? null: rs.getTimestamp("last_bill_at").toInstant().toString()
                ));
            }
            return out;
        }
    }

    /** Newly registered within last N days */
    public List<CustomerReportRow> customersNewlyRegistered(int days) throws SQLException {
        String sql = "SELECT c.id, c.account_number, c.name, c.address, c.phone, c.created_at, " +
                     " (SELECT MAX(b.created_at) FROM bills b WHERE b.customer_id = c.id) AS last_bill_at " +
                     "FROM customers c WHERE c.created_at >= (NOW() - INTERVAL ? DAY) ORDER BY c.created_at DESC";
        try(Connection con = DB.get().connect();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, days);
            try(ResultSet rs = ps.executeQuery()){
                List<CustomerReportRow> out = new ArrayList<>();
                while(rs.next()){
                    out.add(new CustomerReportRow(
                        rs.getInt("id"),
                        rs.getString("account_number"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getTimestamp("created_at").toInstant().toString(),
                        rs.getTimestamp("last_bill_at")==null? null: rs.getTimestamp("last_bill_at").toInstant().toString()
                    ));
                }
                return out;
            }
        }
    }

    /** Active customers = at least one bill in the last N days */
    public List<CustomerReportRow> customersActiveWithinDays(int days) throws SQLException {
        String sql = "SELECT c.id, c.account_number, c.name, c.address, c.phone, c.created_at, MAX(b.created_at) AS last_bill_at " +
                     "FROM customers c JOIN bills b ON b.customer_id = c.id " +
                     "WHERE b.created_at >= (NOW() - INTERVAL ? DAY) " +
                     "GROUP BY c.id, c.account_number, c.name, c.address, c.phone, c.created_at " +
                     "ORDER BY last_bill_at DESC";
        try(Connection con = DB.get().connect();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, days);
            try(ResultSet rs = ps.executeQuery()){
                List<CustomerReportRow> out = new ArrayList<>();
                while(rs.next()){
                    out.add(new CustomerReportRow(
                        rs.getInt("id"),
                        rs.getString("account_number"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getTimestamp("created_at").toInstant().toString(),
                        rs.getTimestamp("last_bill_at")==null? null: rs.getTimestamp("last_bill_at").toInstant().toString()
                    ));
                }
                return out;
            }
        }
    }

    /** Bills for a given customer account number */
    public List<Object[]> billsByAccount(String accountNumber) throws SQLException {
        String sql = "SELECT b.id, b.created_at, b.sub_total, b.tax, b.grand_total " +
                     "FROM bills b JOIN customers c ON c.id = b.customer_id " +
                     "WHERE c.account_number = ? ORDER BY b.created_at DESC";
        try(Connection con = DB.get().connect();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, accountNumber);
            try(ResultSet rs = ps.executeQuery()){
                List<Object[]> out = new ArrayList<>();
                while(rs.next()){
                    out.add(new Object[]{ rs.getInt(1), rs.getTimestamp(2).toInstant().toString(),
                                          rs.getDouble(3), rs.getDouble(4), rs.getDouble(5)});
                }
                return out;
            }
        }
    }

    /** Summary (count + sums) and daily breakdown for a date range (inclusive) */
    public BillSummaryResponse summary(LocalDate from, LocalDate to) throws SQLException {
        String totalsSql = "SELECT COUNT(*), COALESCE(SUM(sub_total),0), COALESCE(SUM(tax),0), COALESCE(SUM(grand_total),0) " +
                           "FROM bills WHERE DATE(created_at) BETWEEN ? AND ?";
        String dailySql = "SELECT DATE(created_at) as d, COALESCE(SUM(grand_total),0) " +
                          "FROM bills WHERE DATE(created_at) BETWEEN ? AND ? GROUP BY d ORDER BY d";
        try(Connection con = DB.get().connect()){
            int count; double sub=0, tax=0, grand=0;
            try(PreparedStatement ps = con.prepareStatement(totalsSql)){
                ps.setDate(1, java.sql.Date.valueOf(from));
                ps.setDate(2, java.sql.Date.valueOf(to));
                try(ResultSet rs = ps.executeQuery()){
                    rs.next();
                    count = rs.getInt(1); sub = rs.getDouble(2); tax = rs.getDouble(3); grand = rs.getDouble(4);
                }
            }
            List<AggregatePoint> daily = new ArrayList<>();
            try(PreparedStatement ps = con.prepareStatement(dailySql)){
                ps.setDate(1, java.sql.Date.valueOf(from));
                ps.setDate(2, java.sql.Date.valueOf(to));
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        String d = rs.getDate(1).toString();
                        daily.add(new AggregatePoint(d, d, d, rs.getDouble(2)));
                    }
                }
            }
            return new BillSummaryResponse(count, sub, tax, grand, daily);
        }
    }

    /** Revenue grouped by day/week/month */
    public List<AggregatePoint> revenue(LocalDate from, LocalDate to, String granularity) throws SQLException {
        String sql;
        if ("week".equalsIgnoreCase(granularity)) {
            sql = "SELECT YEARWEEK(created_at, 1) as yw, MIN(DATE(created_at)) as start_d, MAX(DATE(created_at)) as end_d, SUM(grand_total) " +
                  "FROM bills WHERE DATE(created_at) BETWEEN ? AND ? GROUP BY yw ORDER BY start_d";
        } else if ("month".equalsIgnoreCase(granularity)) {
            sql = "SELECT DATE_FORMAT(created_at, '%Y-%m') as ym, MIN(DATE(created_at)) as start_d, MAX(DATE(created_at)) as end_d, SUM(grand_total) " +
                  "FROM bills WHERE DATE(created_at) BETWEEN ? AND ? GROUP BY ym ORDER BY start_d";
        } else { // day
            sql = "SELECT DATE(created_at) as d, DATE(created_at) as start_d, DATE(created_at) as end_d, SUM(grand_total) " +
                  "FROM bills WHERE DATE(created_at) BETWEEN ? AND ? GROUP BY d ORDER BY d";
        }
        try(Connection con = DB.get().connect(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setDate(1, java.sql.Date.valueOf(from));
            ps.setDate(2, java.sql.Date.valueOf(to));
            try(ResultSet rs = ps.executeQuery()){
                List<AggregatePoint> out = new ArrayList<>();
                while(rs.next()){
                    String label = rs.getString(1);
                    String start = rs.getDate(2).toString();
                    String end   = rs.getDate(3).toString();
                    double total = rs.getDouble(4);
                    out.add(new AggregatePoint(label, start, end, total));
                }
                return out;
            }
        }
    }
}
