package sql;

import java.util.HashMap;
import java.util.Map;

public class HashMapSqlRegistry implements SqlRegistry {

    protected Map<String, String> sqlMap = new HashMap<String, String>();

    public void registerSql(String key, String sql) {
        sqlMap.put(key, sql);
    }

    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);
        if(sql==null)
             throw new SqlNotFoundException(key+"를 이용해서 SQL을 찾을 수 없습니다");
        else
            return sql;
    }
}
