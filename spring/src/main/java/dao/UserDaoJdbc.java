package dao;

import domain.Level;
import domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import sql.SqlService;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;

    private SqlService sqlService;

    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
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

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void deleteAll() {
        jdbcTemplate.update(sqlService.getSql("userDelete"));
    }

    public void add(final User user) {
        jdbcTemplate.update(sqlService.getSql("userAdd"),
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
        jdbcTemplate.update(sqlService.getSql("userUpdate"),
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
        return jdbcTemplate.queryForObject(sqlService.getSql("userCount"), new RowMapper<Integer>() {
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt(1);
            }
        });
    }

    public User get(final String id) {
        return jdbcTemplate.queryForObject(sqlService.getSql("userGet"), new Object[]{id}, this.rowMapper);
    }

    public List<User> getAll() {
        return jdbcTemplate.query(sqlService.getSql("userGetAll"), this.rowMapper);
    }
}
