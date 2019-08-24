package service;

import dao.UserDao;
import domain.Level;
import domain.User;

import java.util.List;

public class UserService {
    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for(User user : users) {
            Boolean changed = false;

            if(user.getLevel()==Level.BASIC && user.getLogin()>=50 ) {
                user.setLevel(Level.SILVER);
                changed = true;
            }
            else if(user.getLevel()==Level.SILVER && user.getRecommend()>=30) {
                user.setLevel(Level.GOLD);
                changed = true;
            }

            if(changed) {
                userDao.update(user);
            }
        }
    }

}
