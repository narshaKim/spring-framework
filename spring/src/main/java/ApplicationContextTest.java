import domain.Hello;
import domain.Printer;
import domain.StringPrinter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
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

    @Test
    public void genericApplicationContext() {
        GenericApplicationContext ac = new GenericApplicationContext();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
        reader.loadBeanDefinitions("/hello-printer.xml");

        ac.refresh();

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        Assert.assertThat(ac.getBean("printer").toString(), CoreMatchers.is("Hello Spring"));
    }

    @Test
    public void treeApplicationContext() {
        ApplicationContext parent = new GenericXmlApplicationContext("/parent-context.xml");
        GenericApplicationContext child = new GenericApplicationContext(parent);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
        reader.loadBeanDefinitions("/child-context.xml");
        child.refresh();

        Printer printer = child.getBean("printer", Printer.class);
        Assert.assertThat(printer, CoreMatchers.is(CoreMatchers.<Printer>notNullValue()));

        Hello hello = child.getBean("hello", Hello.class);
        Assert.assertThat(hello, CoreMatchers.is(CoreMatchers.<Hello>notNullValue()));

        hello.print();
        Assert.assertThat(printer.toString(), CoreMatchers.is("Hello Child"));
    }
}
