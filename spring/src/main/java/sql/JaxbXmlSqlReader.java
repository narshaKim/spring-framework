package sql;

import JAXB.SqlType;
import JAXB.Sqlmap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class JaxbXmlSqlReader implements SqlReader {

    private final static String DEFAULT_SQLMAP_FILE = "/sqlmap.xml";
    private String sqlmapFile = DEFAULT_SQLMAP_FILE;

    public void setSqlmapFile(String sqlmapFile) {
        this.sqlmapFile = sqlmapFile;
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
}
