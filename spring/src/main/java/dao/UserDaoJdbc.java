package dao;

import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import sql.SqlService;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    private SqlService sqlService;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("USERS").usingColumns("id", "name", "password", "level", "login", "recommend", "email");
    }

    public void deleteAll() {
        jdbcTemplate.update(sqlService.getSql("userDelete"));
    }

    public void add(final User user) {
        simpleJdbcInsert.execute(new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("name", user.getName())
                .addValue("password", user.getPassword())
                .addValue("level", user.getLevelInt())
                .addValue("login", user.getLogin())
                .addValue("recommend", user.getRecommend())
                .addValue("email", user.getEmail())
        );
    }

    public void update(User user) {
        namedParameterJdbcTemplate.update(sqlService.getSql("userUpdate"), new BeanPropertySqlParameterSource(user));
    }

    public int getCount() {
        return jdbcTemplate.query(sqlService.getSql("userCount"), new SingleColumnRowMapper<Integer>(Integer.class)).get(0);
    }

    public User get(final String id) {
        return namedParameterJdbcTemplate.queryForObject(sqlService.getSql("userGet"), new MapSqlParameterSource().addValue("id", id), new BeanPropertyRowMapper<User>(User.class));
    }

    public List<User> getAll() {
        return jdbcTemplate.query(sqlService.getSql("userGetAll"), new BeanPropertyRowMapper<User>(User.class));
    }
}
