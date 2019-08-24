package dao;

import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import strategy.DeleteAllStatement;
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
        StatementStrategy strategy = new DeleteAllStatement();
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
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement("SELECT COUNT(*) FROM USERS");

            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
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

    public void add(User user) throws SQLException, ClassNotFoundException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();

            ps = c.prepareStatement("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?,?,?)");
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());

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

    public User get(String id) throws SQLException, ClassNotFoundException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();

            ps = c.prepareStatement("SELECT * FROM USERS WHERE ID=?");
            ps.setString(1, id);

            rs = ps.executeQuery();
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
            return user;
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
