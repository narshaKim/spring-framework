import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = AppContext.class)
public class BeanTest {

    @Autowired
    GenericApplicationContext context;

    @Test
    public void beans() {
        List<List<String>> roleBeanInfos = new ArrayList<List<String>>();
        roleBeanInfos.add(new ArrayList<String>());
        roleBeanInfos.add(new ArrayList<String>());
        roleBeanInfos.add(new ArrayList<String>());

        for(String name : context.getBeanDefinitionNames()) {
            int role = context.getBeanDefinition(name).getRole();
            List<String> beanInfos = roleBeanInfos.get(role);
            beanInfos.add(role+"\t"+name+"\t"+context.getBean(name).getClass().getName());
        }

        for(List<String> beanInfos : roleBeanInfos) {
            for(String beanInfo : beanInfos) {
                System.out.println(beanInfo);
            }
        }
    }
}
