package JAXB;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.List;

public class JaxbTest {

    @Test
    public void readSqlmap() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Sqlmap.class);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(getClass().getResourceAsStream("/jaxbtest.xml"));

        List<SqlType> sqlList = sqlmap.getSql();
        Assert.assertThat(sqlList.get(0).getKey(), CoreMatchers.is("add"));
        Assert.assertThat(sqlList.get(0).getValue(), CoreMatchers.is("insert"));
        Assert.assertThat(sqlList.get(1).getKey(), CoreMatchers.is("get"));
        Assert.assertThat(sqlList.get(1).getValue(), CoreMatchers.is("select"));
        Assert.assertThat(sqlList.get(2).getKey(), CoreMatchers.is("delete"));
        Assert.assertThat(sqlList.get(2).getValue(), CoreMatchers.is("delete"));
    }

}
