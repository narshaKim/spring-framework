package service;

import JAXB.SqlType;
import JAXB.Sqlmap;
import dao.UserDao;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlSqlService implements SqlService {

    private Map<String, String> sqlMap = new HashMap<String, String>();

    public XmlSqlService() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Sqlmap.class);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            InputStream is = getClass().getResourceAsStream("/sqlmap.xml");
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

            for(SqlType sql : sqlmap.getSql()) {
                sqlMap.put(sql.getKey(), sql.getValue());
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);
        if(sql==null)
            throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
        return sql;
    }
}
