package sql;

import JAXB.SqlType;
import JAXB.Sqlmap;
import dao.UserDao;
import org.springframework.oxm.Unmarshaller;
import service.SqlRetrievalFailureException;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class OxmSqlService implements SqlService {

    private final OxmSqlReader reader = new OxmSqlReader();
    private SqlRegistry sqlRegistry = new HashMapSqlRegistry();

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        reader.setUnmarshaller(unmarshaller);
    }

    public void setSqlmapFile(String sqlmapFile) {
        reader.setSqlmapFile(sqlmapFile);
    }

    @PostConstruct
    public void loadSql() {
        this.reader.read(this.sqlRegistry);
    }

    public String getSql(String key) throws SqlRetrievalFailureException {
        try {
            return this.sqlRegistry.findSql(key);
        } catch (SqlNotFoundException e) {
            throw new SqlRetrievalFailureException(e);
        }
    }

    private class OxmSqlReader implements SqlReader {

        private Unmarshaller unmarshaller;
        private final static String DEFAULT_SQLMAP_FILE = "/sqlmap.xml";
        private String sqlmapFile = DEFAULT_SQLMAP_FILE;

        public void setUnmarshaller(Unmarshaller unmarshaller) {
            this.unmarshaller = unmarshaller;
        }

        public void setSqlmapFile(String sqlmapFile) {
            this.sqlmapFile = sqlmapFile;
        }

        public void read(SqlRegistry sqlRegistry) {
            try {
                Source source = new StreamSource(UserDao.class.getResourceAsStream(this.sqlmapFile));
                Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);

                for(SqlType sql : sqlmap.getSql()) {
                    sqlRegistry.registerSql(sql.getKey(), sql.getValue());
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(this.sqlmapFile + "을 가져올 수 없습니다,",e);
            }
        }
    }

}
