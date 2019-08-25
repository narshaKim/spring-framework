import domain.Level;
import domain.User;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

    User user;

    @Before
    public void setUp() {
        user = new User();
    }

    @Test
    public void upgradeLevel() {
        Level[] levels = Level.values();
        for(Level level : levels) {
            if(level.nextValue()==null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            Assert.assertThat(user.getLevel(), CoreMatchers.is(level.nextValue()));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void cannotUpgradeLevel() {
        Level[] levels = Level.values();
        for(Level level : levels) {
            if(level.nextValue()!=null) continue;
            user.setLevel(level);
            user.upgradeLevel();
        }
    }

}
