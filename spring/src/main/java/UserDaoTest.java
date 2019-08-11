import dao.UserDao;
import domain.User;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;

public class UserDaoTest {

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {

        ApplicationContext context = new GenericXmlApplicationContext("/spring-config.xml");
        UserDao dao = context.getBean("userDao", UserDao.class);

        dao.deleteAll();
        Assert.assertThat(dao.getCount(), CoreMatchers.is(0));

        User user = new User();
        user.setId("bts-v");
        user.setName("김태형");
        user.setPassword("v");

        dao.add(user);
        Assert.assertThat(dao.getCount(), CoreMatchers.is(1));

        User user2 = dao.get(user.getId());

        Assert.assertThat(user2.getName(), CoreMatchers.is(user.getName()));
        Assert.assertThat(user2.getPassword(), CoreMatchers.is(user.getPassword()));

    }

}
