package component;

import dao.ConnectionMaker;
import dao.DConnectionMaker;
import dao.UserDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao() {
        UserDao userDao = new UserDao();
        userDao.setDataSource(dataSource());
        return userDao;
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/solip_spring");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    @Deprecated
    @Bean
    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }

}
