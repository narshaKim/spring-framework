package component;

import dao.ConnectionMaker;
import dao.DConnectionMaker;
import dao.UserDao;

public class DaoFactory {

    public UserDao userDao() {
        UserDao userDao = new UserDao(connectionMaker());
        return userDao;
    }

    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }

}
