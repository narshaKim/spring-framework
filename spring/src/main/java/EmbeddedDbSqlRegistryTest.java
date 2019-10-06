import org.junit.After;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import sql.EmbeddedSqlRegistry;
import sql.UpdatableSqlRegistry;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
    EmbeddedDatabase db;

    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:embeddeddb/scheme.sql")
                .build();
        EmbeddedSqlRegistry embeddedSqlRegistry = new EmbeddedSqlRegistry();
        embeddedSqlRegistry.setDataSource(db);
        return embeddedSqlRegistry;
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

}
