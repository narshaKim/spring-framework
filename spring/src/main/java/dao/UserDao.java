package dao;

import component.JdbcContext;
import domain.User;
import org.springframework.dao.EmptyResultDataAccessException;
import strategy.ResultSetStrategy;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private JdbcContext jdbcContext;

    public void setJdbcContext(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    public void deleteAll() throws SQLException {
        jdbcContext.executeSql("DELETE FROM USERS");
    }

    public void add(final User user) throws SQLException {
        jdbcContext.executeSql("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?,?,?)", user.getId(), user.getName(), user.getPassword());
    }

    public int getCount() throws SQLException {
        ResultSetStrategy resultSetStrategy = new ResultSetStrategy() {
            public <T> T getResult(ResultSet rs) throws SQLException {
                rs.next();
                return (T) (Integer) rs.getInt(1);
            }
        };

        return jdbcContext.executeSql(resultSetStrategy, "SELECT COUNT(*) FROM USERS");
    }

    public User get(final String id) throws SQLException, ClassNotFoundException {
        ResultSetStrategy resultSetStrategy = new ResultSetStrategy() {
            public <T> T getResult(ResultSet rs) throws SQLException {
                User user = null;
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getString("ID"));
                    user.setName(rs.getString("NAME"));
                    user.setPassword(rs.getString("PASSWORD"));
                }
                if (user == null) {
                    throw new EmptyResultDataAccessException(1);
                }
                return (T) user;
            }
        };

        return jdbcContext.executeSql(resultSetStrategy, "SELECT * FROM USERS WHERE ID=?", id);
    }
}
