package service;

import dao.UserDao;
import domain.Level;
import domain.User;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;

public class UserServiceImpl implements UserService {

    UserDao userDao;
    MailSender mailSender;

    public final static int MIN_LOGCOUNT_FOR_SILVER = 50;
    public final static int MIN_RECCOMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void add(User user) {
        if(user.getLevel()==null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    public User get(String id) {
        return userDao.get(id);
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public void deleteAll() {
        userDao.deleteAll();
    }

    public void update(User user) {
        userDao.update(user);
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for(User user : users) {
            if(canUpgradeLevel(user))
                upgradeLevel(user);
        }
    }

    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEmail(user);
    }

    private void sendUpgradeEmail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("SpringFramework@solip.com");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());

        mailSender.send(mailMessage);
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
