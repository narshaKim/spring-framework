package dao;

import domain.User;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoJdbc implements UserDao {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public void deleteAll() {
        sqlSessionTemplate.update("userDelete");
    }

    public void add(final User user) {
        sqlSessionTemplate.insert("userAdd", user);
    }

    public void update(User user) {
        sqlSessionTemplate.update("userUpdate", user);
    }

    public int getCount() {
        return sqlSessionTemplate.selectOne("userCount");
    }

    public User get(final String id) {
        return sqlSessionTemplate.selectOne("userGet", id);
    }

    public List<User> getAll() {
        return sqlSessionTemplate.selectList("userGetAll");
    }
}
