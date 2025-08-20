package edu.pahana.service.dao;

import edu.pahana.service.model.User;
import edu.pahana.service.util.DB;

import java.sql.*;

public class UserDAO {
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, role FROM users WHERE username = ?";
        try(Connection con = DB.get().connect(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                }
            }
        }
        return null;
    }
}
