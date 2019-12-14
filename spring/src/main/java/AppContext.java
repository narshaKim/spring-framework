import component.DummyMailSender;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
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

    @Value("${db.driverClass}")
    private Class<? extends Driver> driverClass;
    @Value("${db.url}")
    private String url;
    @Value(("${db.username}"))
    private String username;
    @Value("${db.password}")
    private String password;

    /** DB */
    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    /** 트랜잭션 */
    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = sqlSessionFactoryBean();
        return new SqlSessionTemplate(sqlSessionFactoryBean.getObject());
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean() {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
        return sqlSessionFactoryBean;
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
