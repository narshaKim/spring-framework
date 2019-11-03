import component.DummyMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import service.UserService;

import javax.sql.DataSource;
import java.sql.Driver;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages="dao")
@PropertySource("/database.properties")
@Import({SqlServiceContext.class})
public class AppContext {

    @Autowired
    private Environment env;

    /** DB */
    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        try {
            dataSource.setDriverClass(
                    (Class<? extends Driver>) Class.forName(env.getProperty("db.driverClass"))
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
        return dataSource;
    }

    /** 트랜잭션 */
    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Configuration
    @Profile("production")
    public static class ProductionAppContext {

        /** 메일 서비스 */
        @Bean
        public MailSender mailSender() {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("mail.mycompany.com");
            return mailSender;
        }

    }


    @Configuration
    @Profile("test")
    public static class TestAppContext {

        /** Test 서비스 */
        @Bean
        public UserService testService() {
            return new UserServiceTest.TestUserServiceImpl();
        }


        /** 메일 송신자 */
        @Bean
        public MailSender mailSender() {
            MailSender mailSender = new DummyMailSender();
            return mailSender;
        }

    }

}
