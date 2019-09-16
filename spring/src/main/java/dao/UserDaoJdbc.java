package dao;

import domain.Level;
import domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;

    private Map<String, String> sqlMap;
    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    RowMapper<User> rowMapper = new RowMapper<User>() {
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            String id = resultSet.getString("ID");
            String name = resultSet.getString("NAME");
            String password = resultSet.getString("PASSWORD");
            Level level = Level.valueOf(resultSet.getInt("LEVEL"));
            int login = resultSet.getInt("LOGIN");
            int recommend = resultSet.getInt("RECOMMEND");
            String email = resultSet.getString("EMAIL");
            return new User(id, name, password, level, login, recommend, email);
        }
    };

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteAll() {
        jdbcTemplate.update(sqlMap.get("delete"));
    }

    public void add(final User user) {
        jdbcTemplate.update(sqlMap.get("add"),
                user.getId(),
                user.getName(),
                user.getPassword(),
                user.getLevel().intValue(),
                user.getLogin(),
                user.getRecommend(),
                user.getEmail()
        );
    }

    public void update(User user) {
        jdbcTemplate.update(sqlMap.get("update"),
                user.getName(),
                user.getPassword(),
                user.getLevel().intValue(),
                user.getLogin(),
                user.getRecommend(),
                user.getEmail(),
                user.getId()
        );
    }

    public int getCount() {
        return jdbcTemplate.queryForObject(sqlMap.get("count"), new RowMapper<Integer>() {
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt(1);
            }
        });
    }

    public User get(final String id) {
        return jdbcTemplate.queryForObject(sqlMap.get("get"), new Object[]{id}, this.rowMapper);
    }

    public List<User> getAll() {
        return jdbcTemplate.query(sqlMap.get("getAll"), this.rowMapper);
    }
}
