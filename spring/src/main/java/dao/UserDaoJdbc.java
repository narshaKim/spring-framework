package dao;

import domain.Level;
import domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;

    RowMapper<User> rowMapper = new RowMapper<User>() {
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            String id = resultSet.getString("ID");
            String name = resultSet.getString("NAME");
            String password = resultSet.getString("PASSWORD");
            Level level = Level.valueOf(resultSet.getInt("LEVEL"));
            int login = resultSet.getInt("LOGIN");
            int recommend = resultSet.getInt("RECOMMEND");
            return new User(id, name, password, level, login, recommend);
        }
    };

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM USERS");
    }

    public void add(final User user) {
        jdbcTemplate.update("INSERT INTO USERS(ID, NAME, PASSWORD, LEVEL, LOGIN, RECOMMEND) VALUES(?,?,?,?,?,?)",
                user.getId(),
                user.getName(),
                user.getPassword(),
                user.getLevel().intValue(),
                user.getLogin(),
                user.getRecommend()
        );
    }

    public int getCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USERS", new RowMapper<Integer>() {
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt(1);
            }
        });
    }

    public User get(final String id) {
        return jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE ID=?", new Object[]{id}, this.rowMapper);
    }

    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM USERS ORDER BY ID ASC", this.rowMapper);
    }
}
