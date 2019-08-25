package service;

import dao.UserDao;
import domain.Level;
import domain.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

public class UserService {

    UserDao userDao;
    PlatformTransactionManager transactionManager;

    public final static int MIN_LOGCOUNT_FOR_SILVER = 50;
    public final static int MIN_RECCOMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void add(User user) {
        if(user.getLevel()==null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    public void upgradeLevels() {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();
            for(User user : users) {
                if(canUpgradeLevel(user))
                    upgradeLevel(user);
            }
            this.transactionManager.commit(status);
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
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
