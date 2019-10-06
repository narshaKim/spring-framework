import sql.ConcurrentHashMapSqlRegistry;
import sql.UpdatableSqlRegistry;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        return new ConcurrentHashMapSqlRegistry();
    }
}
