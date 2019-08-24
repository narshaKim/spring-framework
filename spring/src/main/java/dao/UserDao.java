package dao;

import domain.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM USERS");
    }

    public void add(final User user) {
        jdbcTemplate.update("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?,?,?)", user.getId(), user.getName(), user.getPassword());
    }

    public int getCount() {
        return jdbcTemplate.query(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                return connection.prepareStatement("SELECT COUNT(*) FROM USERS");
            }
        }, new ResultSetExtractor<Integer>() {
            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                resultSet.next();
                return resultSet.getInt(1);
            }
        });
    }

    public User get(final String id) {
        return jdbcTemplate.query(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM USERS WHERE ID=?");
                preparedStatement.setString(1, id);
                return preparedStatement;
            }
        }, new ResultSetExtractor<User>() {
            public User extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                User user = null;
                if (resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getString("ID"));
                    user.setName(resultSet.getString("NAME"));
                    user.setPassword(resultSet.getString("PASSWORD"));
                }
                if (user == null) {
                    throw new EmptyResultDataAccessException(1);
                }
                return user;
            }
        });
    }
}
