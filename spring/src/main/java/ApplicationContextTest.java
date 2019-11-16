import domain.Hello;
import domain.StringPrinter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;

public class ApplicationContextTest {

    @Test
    public void registerBean() {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerSingleton("hello1", Hello.class);
        Hello hello1 = ac.getBean("hello1", Hello.class);
        Assert.assertThat(hello1, CoreMatchers.is(CoreMatchers.notNullValue()));

        BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        ac.registerBeanDefinition("hello2", helloDef);
        Hello hello2 = ac.getBean("hello2", Hello.class);
        Assert.assertThat(hello2.sayHello(), CoreMatchers.is("Hello Spring"));

        Assert.assertThat(hello1, CoreMatchers.not(hello2));
        Assert.assertThat(ac.getBeanDefinitionCount(), CoreMatchers.is(2));
    }

    @Test
    public void registerBeanwithDependency() {
        StaticApplicationContext ac = new StaticApplicationContext();

        ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));

        BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        helloDef.getPropertyValues().addPropertyValue("printer", new RuntimeBeanReference("printer"));
        ac.registerBeanDefinition("hello", helloDef);

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        Assert.assertThat(ac.getBean("printer").toString(), CoreMatchers.is("Hello Spring"));
    }
}
