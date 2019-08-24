package dao;

import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import strategy.*;

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
        StatementStrategy strategy = new DeleteAllStatement();
        jdbcContextWithStatementStrategy(strategy);
    }

    public void add(User user) throws SQLException, ClassNotFoundException {
        StatementStrategy strategy = new AddStatement(user);
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
        StatementStrategy statement = new CountStatement();
        ResultSetStrategy resultSet = new CountResultSet();

        return (Integer) jdbcContextWithStatementStrategyAndResultSetStrategy(statement, resultSet);
    }

    public User get(String id) throws SQLException, ClassNotFoundException {
        StatementStrategy statement = new InfoStatement(id);
        ResultSetStrategy resultSet = new InfoResultSet();

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
