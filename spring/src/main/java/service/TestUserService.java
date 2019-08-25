package service;

import dao.UserDao;
import domain.User;
import exception.TestUserServiceException;
import org.springframework.mail.MailSender;

public class TestUserService extends UserService {

    private String id;

    @Override
    public void setUserDao(UserDao userDao) {
        super.setUserDao(userDao);
    }

    @Override
    public void setMailSender(MailSender mailSender) {
        super.setMailSender(mailSender);
    }

    public TestUserService(String id) {
        this.id = id;
    }

    @Override
    public void upgradeLevel(User user) {
        if(user.getId().equals(this.id))
            throw new TestUserServiceException();
        super.upgradeLevel(user);
    }
}
