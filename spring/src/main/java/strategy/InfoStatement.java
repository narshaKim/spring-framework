package strategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InfoStatement implements StatementStrategy {

    private String id;

    public InfoStatement(String id) {
        this.id = id;
    }

    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("SELECT * FROM USERS WHERE ID=?");
        ps.setString(1, this.id);
        return ps;
    }
}
