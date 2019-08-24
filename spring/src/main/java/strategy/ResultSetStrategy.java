package strategy;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetStrategy {
    public <T> T getResult(ResultSet rs) throws SQLException;
}
