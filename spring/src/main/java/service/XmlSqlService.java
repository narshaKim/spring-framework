package service;

import JAXB.SqlType;
import JAXB.Sqlmap;
import sql.SqlNotFoundException;
import sql.SqlReader;
import sql.SqlRegistry;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {

    private SqlReader sqlReader;
    private SqlRegistry sqlRegistry;

    public void setSqlReader(SqlReader sqlReader) {
        this.sqlReader = sqlReader;
    }

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }

    public void registerSql(String key, String sql) {
        sqlMap.put(key, sql);
    }

    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);
        if(sql==null)
            throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
        return sql;
    }

    public void read(SqlRegistry sqlRegistry) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Sqlmap.class);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            InputStream is = getClass().getResourceAsStream(sqlmapFile);
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

            for(SqlType sql : sqlmap.getSql()) {
                sqlRegistry.registerSql(sql.getKey(), sql.getValue());
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> sqlMap = new HashMap<String, String>();

    private String sqlmapFile;

    public void setSqlmapFile(String sqlmapFile) {
        this.sqlmapFile = sqlmapFile;
    }

    @PostConstruct
    public void loadSql() {
        this.sqlReader.read(this.sqlRegistry);
    }

    public String getSql(String key) throws SqlRetrievalFailureException {
        try {
            return this.sqlRegistry.findSql(key);
        } catch (SqlNotFoundException e) {
            throw new SqlRetrievalFailureException(e);
        }
    }
}
