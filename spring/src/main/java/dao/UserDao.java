package dao;

import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import strategy.ResultSetStrategy;
import strategy.StatementStrategy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    @Autowired
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void deleteAll() throws SQLException {
        StatementStrategy strategy = new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                return c.prepareStatement("DELETE FROM USERS");
            }
        };
        jdbcContextWithStatementStrategy(strategy);
    }

    public void add(final User user) throws SQLException {
        StatementStrategy strategy = new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?,?,?)");
                ps.setString(1, user.getId());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPassword());
                return ps;
            }
        };
        jdbcContextWithStatementStrategy(strategy);
    }

    private void jdbcContextWithStatementStrategy(StatementStrategy strategy) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = dataSource.getConnection();
            ps = strategy.makePreparedStatement(c);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if(ps!=null) {
                try {
                    ps.close();
                } catch (SQLException e) {}
            }
            if(c!=null) {
                try {
                    c.close();
                } catch (SQLException e) {}
            }
        }
    }

    public int getCount() throws SQLException {
        StatementStrategy statement = new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                return c.prepareStatement("SELECT COUNT(*) FROM USERS");
            }
        };
        ResultSetStrategy resultSet = new ResultSetStrategy() {
            public <T> T getResult(ResultSet rs) throws SQLException {
                rs.next();
                return (T) (Integer) rs.getInt(1);
            }
        };

        return (Integer) jdbcContextWithStatementStrategyAndResultSetStrategy(statement, resultSet);
    }

    public User get(final String id) throws SQLException, ClassNotFoundException {
        StatementStrategy statement = new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement("SELECT * FROM USERS WHERE ID=?");
                ps.setString(1, id);
                return ps;
            }
        };
        ResultSetStrategy resultSet = new ResultSetStrategy() {
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
        };

        return (User)jdbcContextWithStatementStrategyAndResultSetStrategy(statement, resultSet);
    }

    private <T> T jdbcContextWithStatementStrategyAndResultSetStrategy(StatementStrategy statement, ResultSetStrategy resultSet) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();
            ps = statement.makePreparedStatement(c);
            rs = ps.executeQuery();
            return resultSet.getResult(rs);
        } catch (SQLException e) {
            throw e;
        } finally {
            if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {}
            }
            if(ps!=null) {
                try {
                    ps.close();
                } catch (SQLException e) {}
            }
            if(c!=null) {
                try {
                    c.close();
                } catch (SQLException e) {}
            }
        }
    }

}
