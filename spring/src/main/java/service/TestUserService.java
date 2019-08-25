package service;

import dao.UserDao;
import domain.User;
import exception.TestUserServiceException;

public class TestUserService extends UserService {

    private String id;
    private UserDao userDao;

    @Override
    public void setUserDao(UserDao userDao) {
        super.setUserDao(userDao);
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
