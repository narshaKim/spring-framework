package sql;

import JAXB.SqlType;
import JAXB.Sqlmap;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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

    public void setSqlmap(Resource sqlmap) {
        reader.setSqlmap(sqlmap);
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
        private Resource sqlmap = new ClassPathResource("/sqlmap.xml");

        public void setUnmarshaller(Unmarshaller unmarshaller) {
            this.unmarshaller = unmarshaller;
        }

        public void setSqlmap(Resource sqlmap) {
            this.sqlmap = sqlmap;
        }

        public void read(SqlRegistry sqlRegistry) {
            try {
                Source source = new StreamSource(this.sqlmap.getInputStream());
                Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);

                for(SqlType sql : sqlmap.getSql()) {
                    sqlRegistry.registerSql(sql.getKey(), sql.getValue());
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(this.sqlmap.getFilename() + "을 가져올 수 없습니다,",e);
            }
        }
    }

}
