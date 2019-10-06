import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sql.SqlNotFoundException;
import sql.SqlUpdateFailureException;
import sql.UpdatableSqlRegistry;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractUpdatableSqlRegistryTest {
    UpdatableSqlRegistry sqlRegistry;

    @Before
    public void setUp() {
        sqlRegistry = createUpdatableSqlRegistry();
        sqlRegistry.registerSql("KEY1", "SQL1");
        sqlRegistry.registerSql("KEY2", "SQL2");
        sqlRegistry.registerSql("KEY3", "SQL3");
    }

    protected abstract UpdatableSqlRegistry createUpdatableSqlRegistry();

    @Test
    public void find() {
        checkFindResult("SQL1", "SQL2", "SQL3");
    }

    protected void checkFindResult(String expected1, String expected2, String expected3) {
        Assert.assertThat(sqlRegistry.findSql("KEY1"), CoreMatchers.is(expected1));
        Assert.assertThat(sqlRegistry.findSql("KEY2"), CoreMatchers.is(expected2));
        Assert.assertThat(sqlRegistry.findSql("KEY3"), CoreMatchers.is(expected3));
    }

    @Test(expected = SqlNotFoundException.class)
    public void unknownKey() {
        sqlRegistry.findSql("krwopgrwkog");
    }

    @Test
    public void updateSingle() throws SqlUpdateFailureException {
        sqlRegistry.updateSql("KEY2", "Modified2");
        checkFindResult("SQL1", "Modified2", "SQL3");
    }

    @Test
    public void updateMulti() throws SqlUpdateFailureException {
        Map<String, String> sqlmap = new HashMap<String, String>();
        sqlmap.put("KEY1", "Modified1");
        sqlmap.put("KEY3", "Modified3");

        sqlRegistry.updateSql(sqlmap);
        checkFindResult("Modified1", "SQL2", "Modified3");
    }

    @Test(expected = SqlUpdateFailureException.class)
    public void uddateWithNotExistingKey() throws SqlUpdateFailureException {
        sqlRegistry.updateSql("SQLefEWragkop", "Modified2");
    }

}
