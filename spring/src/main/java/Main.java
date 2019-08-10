import dao.DUserDao;
import dao.UserDao;
import domain.User;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        UserDao dao = new DUserDao();

        User user = new User();
        user.setId("narsha");
        user.setName("김솔잎");
        user.setPassword("solip");
        dao.add(user);
        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());
        System.out.println(user2.getId() + " 조회 성공");
    }

}
