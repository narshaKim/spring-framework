import component.MockMailSender;
import dao.UserDao;
import domain.Level;
import domain.User;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import service.TestUserService;
import service.UserService;
import service.UserServiceImpl;
import service.UserServiceTx;

import java.util.Arrays;
import java.util.List;

import static service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserServiceTest {

    @Autowired
    UserService userService;

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
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        Assert.assertThat(userWithLevelRead.getLevel(), CoreMatchers.is(userWithLevel.getLevel()));
        Assert.assertThat(userWithoutLevelRead.getLevel(), CoreMatchers.is(Level.BASIC));
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() throws Exception {
        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);

        List<String> requests = mockMailSender.getRequests();
        Assert.assertThat(requests.size(), CoreMatchers.is(2));
        Assert.assertThat(requests.get(0), CoreMatchers.is(users.get(1).getEmail()));
        Assert.assertThat(requests.get(1), CoreMatchers.is(users.get(3).getEmail()));

    }

    @Test
    public void upgradeAllOrNothing() {
        UserServiceImpl testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
        testUserService.setMailSender(mailSender);


        UserServiceTx txUserService = new UserServiceTx();
        txUserService.setTransactionManager(transactionManager);
        txUserService.setUserService(testUserService);

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
