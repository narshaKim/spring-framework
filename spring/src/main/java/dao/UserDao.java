package dao;

import component.JdbcContext;
import domain.User;
import org.springframework.dao.EmptyResultDataAccessException;
import strategy.ResultSetStrategy;
import strategy.StatementStrategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private JdbcContext jdbcContext;

    public void setJdbcContext(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    public void deleteAll() throws SQLException {
        jdbcContext.workWithStatementStrategy(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                return c.prepareStatement("DELETE FROM USERS");
            }
        });
    }

    public void add(final User user) throws SQLException {
        jdbcContext.workWithStatementStrategy(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?,?,?)");
                ps.setString(1, user.getId());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPassword());
                return ps;
            }
        });
    }

    public int getCount() throws SQLException {
        return (Integer) jdbcContext.workWithResultSetStrategy(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                return c.prepareStatement("SELECT COUNT(*) FROM USERS");
            }
        }, new ResultSetStrategy() {
            public <T> T getResult(ResultSet rs) throws SQLException {
                rs.next();
                return (T) (Integer) rs.getInt(1);
            }
        });
    }

    public User get(final String id) throws SQLException, ClassNotFoundException {
        return (User)jdbcContext.workWithResultSetStrategy(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement("SELECT * FROM USERS WHERE ID=?");
                ps.setString(1, id);
                return ps;
            }
        }, new ResultSetStrategy() {
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
        });
    }

}
