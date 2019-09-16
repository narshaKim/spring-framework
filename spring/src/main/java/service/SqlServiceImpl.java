package service;

import java.util.Map;

public class SqlServiceImpl implements SqlService {

    private Map<String, String> sqlMap;

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);
        if(sql==null)
            throw new SqlRetrievalFailureException(key + "에 대한 Sql을 찾을 수 없습니다.");
        return sql;
    }
}
