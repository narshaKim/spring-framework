package strategy;

import domain.User;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InfoResultSet implements ResultSetStrategy {

    public <T> T getResult(ResultSet rs) throws SQLException {
        User user = null;
        if(rs.next()) {
            user = new User();
            user.setId(rs.getString("ID"));
            user.setName(rs.getString("NAME"));
            user.setPassword(rs.getString("PASSWORD"));
        }
        if(user==null) {
            throw new EmptyResultDataAccessException(1);
        }
        return (T) user;
    }

}
