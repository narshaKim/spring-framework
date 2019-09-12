package Proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyTest {

    @Test
    public void invokeMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String name = "Spring";

        // length()
        Assert.assertThat(name.length(), CoreMatchers.is(6));

        Method lengthMethod = name.getClass().getMethod("length");
        Assert.assertThat((Integer) lengthMethod.invoke(name), CoreMatchers.is(6));

        // charAt()
        Assert.assertThat(name.charAt(0), CoreMatchers.is('S'));

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        Assert.assertThat((Character) charAtMethod.invoke(name, 0), CoreMatchers.is('S'));

    }

    @Test
    public void simpleProxy() {
        Hello hello = new HelloTarget();
        Assert.assertThat(hello.sayHello("Toby"), CoreMatchers.is("Hello Toby"));
        Assert.assertThat(hello.sayHi("Toby"), CoreMatchers.is("Hi Toby"));
        Assert.assertThat(hello.sayThankYou("Toby"), CoreMatchers.is("Thank you Toby"));

        Hello helloProxy = new HelloUppercase(new HelloTarget());
        Assert.assertThat(helloProxy.sayHello("Toby"), CoreMatchers.is("HELLO TOBY"));
        Assert.assertThat(helloProxy.sayHi("Toby"), CoreMatchers.is("HI TOBY"));
        Assert.assertThat(helloProxy.sayThankYou("Toby"), CoreMatchers.is("THANK YOU TOBY"));

    }

    @Test
    public void dinamicProxy() {
        Hello dinamicProxy = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {Hello.class},
                new UppercaseHandler(new HelloTarget())
        );

        Assert.assertThat(dinamicProxy.sayHello("Toby"), CoreMatchers.is("HELLO TOBY"));
        Assert.assertThat(dinamicProxy.sayHi("Toby"), CoreMatchers.is("HI TOBY"));
        Assert.assertThat(dinamicProxy.sayThankYou("Toby"), CoreMatchers.is("THANK YOU TOBY"));

    }

    @Test
    public void proxyFactoryBean() {

        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        Assert.assertThat(proxiedHello.sayHello("Toby"), CoreMatchers.is("HELLO TOBY"));
        Assert.assertThat(proxiedHello.sayHi("Toby"), CoreMatchers.is("HI TOBY"));
        Assert.assertThat(proxiedHello.sayThankYou("Toby"), CoreMatchers.is("Thank you Toby"));
    }

    private class UppercaseAdvice implements MethodInterceptor {
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            String ret = (String) methodInvocation.proceed();
            return ret.toUpperCase();
        }
    }
}
