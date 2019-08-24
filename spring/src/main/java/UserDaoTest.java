import dao.UserDao;
import domain.User;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserDaoTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserDao dao;

    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {
        this.user1 = new User("bts-jk", "전정국", "jk");
        this.user2 = new User("bts-rm", "김남준", "rm");
        this.user3 = new User("bts-v", "김태형", "v");
    }

    @Test(expected = DuplicateKeyException.class)
    public void duplicateKey() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user1);
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

    @Test
    public void getAll() {
        dao.deleteAll();
        List<User> users0 = dao.getAll();
        Assert.assertThat(users0.size(), CoreMatchers.is(0));


        dao.add(user1);
        List<User> users1 = dao.getAll();
        Assert.assertThat(users1.size(), CoreMatchers.is(1));
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        Assert.assertThat(users2.size(), CoreMatchers.is(2));
        checkSameUser(user1, users1.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        Assert.assertThat(users3.size(), CoreMatchers.is(3));
        checkSameUser(user1, users1.get(0));
        checkSameUser(user2, users2.get(1));
        checkSameUser(user3, users3.get(2));
    }

    private void checkSameUser(User user1, User user2) {
        Assert.assertThat(user1.getId(), CoreMatchers.is(user2.getId()));
        Assert.assertThat(user1.getName(), CoreMatchers.is(user2.getName()));
        Assert.assertThat(user1.getPassword(), CoreMatchers.is(user2.getPassword()));

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
