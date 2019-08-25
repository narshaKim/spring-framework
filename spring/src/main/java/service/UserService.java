package service;

import dao.UserDao;
import domain.Level;
import domain.User;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

public class UserService {

    UserDao userDao;
    DataSource dataSource;

    public final static int MIN_LOGCOUNT_FOR_SILVER = 50;
    public final static int MIN_RECCOMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(User user) {
        if(user.getLevel()==null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    public void upgradeLevels() throws Exception {
        TransactionSynchronizationManager.initSynchronization();
        Connection c = DataSourceUtils.getConnection(dataSource);
        c.setAutoCommit(false);

        try {
            List<User> users = userDao.getAll();
            for(User user : users) {
                if(canUpgradeLevel(user))
                    upgradeLevel(user);
            }
            c.commit();
        } catch (Exception e) {
            c.rollback();
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(c, dataSource);
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }

    private Boolean canUpgradeLevel(User user) {
        switch (user.getLevel()) {
            case BASIC:
                return (user.getLogin()>= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER:
                return (user.getRecommend()>= MIN_RECCOMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + user.getLevel());
        }
    }

}
