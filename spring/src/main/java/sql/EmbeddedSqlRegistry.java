package sql;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;

public class EmbeddedSqlRegistry implements UpdatableSqlRegistry {
    SimpleJdbcTemplate jdbc;

    public void setDataSource(DataSource dataSource) {
        jdbc = new SimpleJdbcTemplate(dataSource);
    }

    public void updateSql(String key, String sql) throws SqlUpdateFailureException {
        int affected = jdbc.update("UPDATE SQLMAP SET SQL_=? WHERE KEY_=?", sql, key);
        if(affected==0)
            throw new SqlUpdateFailureException(key+"에 해당하는 SQL을 찾을 수 없습니다.");
    }

    public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
        for(Map.Entry<String, String> entry: sqlmap.entrySet()) {
            updateSql(entry.getKey(), entry.getValue());
        }
    }

    public void registerSql(String key, String sql) {
        jdbc.update("INSERT INTO SQLMAP(KEY_, SQL_) VALUES(?,?)", key, sql);
    }

    public String findSql(String key) throws SqlNotFoundException {
        try {
            return jdbc.queryForObject("SELECT SQL_ FROM SQLMAP WHERE KEY_=?", String.class, key);
        } catch (EmptyResultDataAccessException e) {
            throw new SqlNotFoundException(key + "에 해당하는 SQL을 찾을 수 없습니다", e);
        }
    }
}
