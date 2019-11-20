import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;
import java.util.Set;

public class ScopeTest {

    /**
     * 싱글톤 테스트
     */
    @Test
    public void singletonScope() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(SingletonBean.class, SingletonClientBean.class);

        Set<SingletonBean> beans = new HashSet<SingletonBean>();
        beans.add(ac.getBean(SingletonBean.class));
        beans.add(ac.getBean(SingletonBean.class));
        Assert.assertThat(beans.size(), CoreMatchers.is(1));

        beans.add(ac.getBean(SingletonClientBean.class).bean1);
        beans.add(ac.getBean(SingletonClientBean.class).bean2);
        Assert.assertThat(beans.size(), CoreMatchers.is(1));

    }

    static class SingletonBean {
    }

    static class SingletonClientBean {
        @Autowired
        SingletonBean bean1;
        @Autowired
        SingletonBean bean2;
    }

    @Test
    public void prototypeScope() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, PrototypeClientBean.class);

        Set<PrototypeBean> beans = new HashSet<PrototypeBean>();
        beans.add(ac.getBean(PrototypeBean.class));
        beans.add(ac.getBean(PrototypeBean.class));
        Assert.assertThat(beans.size(), CoreMatchers.is(2));

        beans.add(ac.getBean(PrototypeClientBean.class).bean1);
        beans.add(ac.getBean(PrototypeClientBean.class).bean2);
        Assert.assertThat(beans.size(), CoreMatchers.is(4));
    }

    @Scope("prototype")
    static class PrototypeBean {
    }

    static class PrototypeClientBean {
        @Autowired
        PrototypeBean bean1;
        @Autowired
        PrototypeBean bean2;
    }

}
