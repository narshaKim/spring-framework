import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import sql.EmbeddedSqlRegistry;
import sql.OxmSqlService;
import sql.SqlRegistry;
import sql.SqlService;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages="dao")
public class SqlServiceContext {

    /** 언마샬러 */
    @Bean
    public Unmarshaller unmarshaller() {
        Jaxb2Marshaller unmarsharller = new Jaxb2Marshaller();
        unmarsharller.setClassesToBeBound(JAXB.Sqlmap.class);
        return unmarsharller;
    }

    /** SQL 등록자 */
    @Bean
    public SqlRegistry sqlRegistry() {
        EmbeddedSqlRegistry sqlRegistry = new EmbeddedSqlRegistry();
        sqlRegistry.setDataSource(embeddedDatabase());
        return sqlRegistry;
    }

    /** SQL 서비스 */
    @Bean
    public SqlService sqlService() {
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        return sqlService;
    }

    /** 내장 DB */
    @Bean
    public DataSource embeddedDatabase() {
        return new EmbeddedDatabaseBuilder().setName("embeddedDatabase")
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:embeddeddb/scheme.sql")
                .build();
    }

}
