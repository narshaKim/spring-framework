import dao.UserDao;
import domain.User;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;

public class UserDaoTest {

    private UserDao dao;

    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {
        ApplicationContext context = new GenericXmlApplicationContext("/spring-config.xml");
        this.dao = context.getBean("userDao", UserDao.class);

        this.user1 = new User("bts-jk", "전정국", "jk");
        this.user2 = new User("bts-rm", "김남준", "rm");
        this.user3 = new User("bts-v", "김태형", "v");
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {

        dao.deleteAll();
        Assert.assertThat(dao.getCount(), CoreMatchers.is(0));

        dao.add(user1);
        dao.add(user2);
        Assert.assertThat(dao.getCount(), CoreMatchers.is(2));

        User userget1 = dao.get(user1.getId());
        Assert.assertThat(userget1.getName(), CoreMatchers.is(user1.getName()));
        Assert.assertThat(userget1.getPassword(), CoreMatchers.is(user1.getPassword()));

        User userget2 = dao.get(user2.getId());
        Assert.assertThat(userget2.getName(), CoreMatchers.is(user2.getName()));
        Assert.assertThat(userget2.getPassword(), CoreMatchers.is(user2.getPassword()));

    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException, ClassNotFoundException {

        dao.deleteAll();
        Assert.assertThat(dao.getCount(), CoreMatchers.is(0));

        dao.get("unkonw_id");

    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {

        dao.deleteAll();
        Assert.assertThat(dao.getCount(), CoreMatchers.is(0));

        dao.add(user1);
        Assert.assertThat(dao.getCount(), CoreMatchers.is(1));

        dao.add(user2);
        Assert.assertThat(dao.getCount(), CoreMatchers.is(2));

        dao.add(user3);
        Assert.assertThat(dao.getCount(), CoreMatchers.is(3));
    }

}
