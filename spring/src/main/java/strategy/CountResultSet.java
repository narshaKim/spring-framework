package strategy;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountResultSet implements ResultSetStrategy {

    public <T> T getResult(ResultSet rs) throws SQLException {
        rs.next();
        return (T) (Integer) rs.getInt(1);
    }

}
