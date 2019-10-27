import component.DummyMailSender;
import dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import service.UserService;
import sql.EmbeddedSqlRegistry;
import sql.OxmSqlService;
import sql.SqlRegistry;
import sql.SqlService;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages="dao")
public class TestApplicationContext {

    @Autowired
    private UserDao userDao;

    /** DB */
    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/solip_spring");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    /** 메일 송신자 */
    @Bean
    public MailSender mailSender() {
        MailSender mailSender = new DummyMailSender();
        return mailSender;
    }


    /** 트랜잭션 */
    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    /** Test 서비스 */
    @Bean
    public UserService testService() {
        UserServiceTest.TestUserServiceImpl testService = new UserServiceTest.TestUserServiceImpl();
        testService.setMailSender(mailSender());
        testService.setUserDao(userDao);
        return testService;
    }

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
