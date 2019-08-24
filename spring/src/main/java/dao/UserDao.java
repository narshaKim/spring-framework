package dao;

import domain.User;

import java.util.List;

public interface UserDao {

    void deleteAll();
    void add(User user);
    void update(User user);
    int getCount();
    User get(String id);
    List<User> getAll();

}
