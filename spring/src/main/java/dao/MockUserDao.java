package dao;

import domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockUserDao implements UserDao {
    private List<User> users;
    private List<User> updated = new ArrayList<User>();
    private Map<String, User> added = new HashMap<String, User>();

    public MockUserDao(List<User> users) {
        this.users = users;
    }

    public List<User> getUpdated() {
        return updated;
    }

    public List<User> getAll() {
        return users;
    }

    public void update(User user) {
        updated.add(user);
    }

    public void add(User user) {
        added.put(user.getId(), user);
    }

    public User get(String id) {
        return added.get(id);
    }


    public void deleteAll() {
        throw new UnsupportedOperationException();
    }
    public int getCount() {
        throw new UnsupportedOperationException();
    }
}
