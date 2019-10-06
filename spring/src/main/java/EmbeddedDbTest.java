import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class EmbeddedDbTest {
    EmbeddedDatabase db;
    SimpleJdbcTemplate template;

    @Before
    public void setUp() {
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:embeddeddb/scheme.sql")
                .addScript("classpath:embeddeddb/data.sql")
                .build();
        template = new SimpleJdbcTemplate(db);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void initData() {
        Assert.assertThat(template.queryForObject("SELECT COUNT(*) FROM SQLMAP", new RowMapper<Integer>() {
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt(1);
            }
        }), CoreMatchers.is(2));

        List<Map<String, Object>> list = template.queryForList("SELECT * FROM SQLMAP ORDER BY KEY_");
        Assert.assertThat((String)list.get(0).get("KEY_"), CoreMatchers.is("KEY1"));
        Assert.assertThat((String)list.get(0).get("SQL_"), CoreMatchers.is("SQL1"));
        Assert.assertThat((String)list.get(1).get("KEY_"), CoreMatchers.is("KEY2"));
        Assert.assertThat((String)list.get(1).get("SQL_"), CoreMatchers.is("SQL2"));
    }

    @Test
    public void insert() {
        template.update("INSERT INTO SQLMAP(KEY_, SQL_) VALUES(?,?)", "KEY3", "SQL3");
        Assert.assertThat(template.queryForObject("SELECT COUNT(*) FROM SQLMAP", new RowMapper<Integer>() {
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt(1);
            }
        }), CoreMatchers.is(3));
    }
}
