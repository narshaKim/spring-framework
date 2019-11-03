import JAXB.SqlType;
import JAXB.Sqlmap;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {AppContext.class})
public class OxmTest {

    @Autowired
    Unmarshaller unmarshaller;

    @Test
    public void unmarshallerSqlMap() throws IOException {
        Source xmlSource = new StreamSource(getClass().getResourceAsStream("/jaxbtest.xml"));
        Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(xmlSource);

        List<SqlType> sqlList = sqlmap.getSql();
        Assert.assertThat(sqlList.size(), CoreMatchers.is(3));
        Assert.assertThat(sqlList.get(0).getKey(), CoreMatchers.is("add"));
        Assert.assertThat(sqlList.get(0).getValue(), CoreMatchers.is("insert"));
        Assert.assertThat(sqlList.get(1).getKey(), CoreMatchers.is("get"));
        Assert.assertThat(sqlList.get(1).getValue(), CoreMatchers.is("select"));
        Assert.assertThat(sqlList.get(2).getKey(), CoreMatchers.is("delete"));
        Assert.assertThat(sqlList.get(2).getValue(), CoreMatchers.is("delete"));


    }

}