package dao;

import domain.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

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
        return jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE ID=?", new Object[]{id}, new RowMapper<User>() {
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(resultSet.getString("ID"));
                user.setName(resultSet.getString("NAME"));
                user.setPassword(resultSet.getString("PASSWORD"));
                return user;
            }
        });
    }
}
