import Proxy.TrasactionHandler;
import Proxy.TxProxyFactoryBean;
import component.MockMailSender;
import dao.MockUserDao;
import dao.UserDao;
import domain.Level;
import domain.User;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import service.TestUserService;
import service.UserService;
import service.UserServiceImpl;

import java.util.Arrays;
import java.util.List;

import static service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserServiceTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    UserDao userDao;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    MailSender mailSender;

    List<User> users;

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("jk", "정국", "jkpassword", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, MIN_RECCOMEND_FOR_GOLD-1, "jk@solip.com"),
                new User("jin", "진", "jinpassword", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, MIN_RECCOMEND_FOR_GOLD-1, "jin@solip.com"),
                new User("v", "태형", "vpassword", Level.SILVER, MIN_LOGCOUNT_FOR_SILVER, MIN_RECCOMEND_FOR_GOLD-1, "v@solip.com"),
                new User("jm", "지민", "jmpassword", Level.SILVER, MIN_LOGCOUNT_FOR_SILVER, MIN_RECCOMEND_FOR_GOLD+1,"jm@solip.com"),
                new User("rm", "남준", "rmpassword", Level.GOLD, MIN_LOGCOUNT_FOR_SILVER, MIN_RECCOMEND_FOR_GOLD+1,"rm@solip.com")
        );
    }

    @Test
    public void bean() {
        Assert.assertThat(this.userServiceImpl, CoreMatchers.is(CoreMatchers.<UserServiceImpl>notNullValue()));
    }

    @Test
    public void add() {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(users);
        userServiceImpl.setUserDao(mockUserDao);

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userServiceImpl.add(userWithLevel);
        userServiceImpl.add(userWithoutLevel);

        User userWithLevelRead = mockUserDao.get(userWithLevel.getId());
        User userWithoutLevelRead = mockUserDao.get(userWithoutLevel.getId());

        Assert.assertThat(userWithLevelRead.getLevel(), CoreMatchers.is(userWithLevel.getLevel()));
        Assert.assertThat(userWithoutLevelRead.getLevel(), CoreMatchers.is(Level.BASIC));
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(users);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        Assert.assertThat(updated.size(), CoreMatchers.is(2));
        checkUserAndLevel(updated.get(0), "jin", Level.SILVER);
        checkUserAndLevel(updated.get(1), "jm", Level.GOLD);

        List<String> requests = mockMailSender.getRequests();
        Assert.assertThat(requests.size(), CoreMatchers.is(2));
        Assert.assertThat(requests.get(0), CoreMatchers.is(users.get(1).getEmail()));
        Assert.assertThat(requests.get(1), CoreMatchers.is(users.get(3).getEmail()));

    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        Assert.assertThat(updated.getId(), CoreMatchers.is(expectedId));
        Assert.assertThat(updated.getLevel(), CoreMatchers.is(expectedLevel));
    }

    @Test
    @DirtiesContext
    public void upgradeAllOrNothing() {
        UserServiceImpl testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
        testUserService.setMailSender(mailSender);

        TrasactionHandler trasactionHandler = new TrasactionHandler();
        trasactionHandler.setTransactionManager(transactionManager);
        trasactionHandler.setTarget(testUserService);
        trasactionHandler.setPattern("upgradeLevels");

        TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class);
        txProxyFactoryBean.setTarget(testUserService);

        UserService txUserService = (UserService) txProxyFactoryBean.getObject();

        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try {
            txUserService.upgradeLevels();
            Assert.fail("TestUserServiceException expected");
        } catch (Exception e) {}

        checkLevelUpgraded(users.get(1), false);
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if(upgraded)
            Assert.assertThat(userUpdate.getLevel(), CoreMatchers.is(user.getLevel().nextValue()));
        else
            Assert.assertThat(userUpdate.getLevel(), CoreMatchers.is(user.getLevel()));
    }

}
