package PointcutExpression;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

public class PointcutExpressionTest {

    @Test
    public void methodSignaturePointcut() throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int " +
                "PointcutExpression.Target.minus(int,int) " +
                "throws java.lang.RuntimeException)");

        Assert.assertThat(
                pointcut.getClassFilter().matches(Target.class)
                        && pointcut.getMethodMatcher().matches( Target.class.getMethod("minus", int.class, int.class), null),
                CoreMatchers.is(true)
        );

        Assert.assertThat(
                pointcut.getClassFilter().matches(Target.class)
                        && pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null),
                CoreMatchers.is(false)
        );

        Assert.assertThat(
                pointcut.getClassFilter().matches(Bean.class)
                        && pointcut.getMethodMatcher().matches(Target.class.getMethod("method"), null),
                CoreMatchers.is(false)
        );
    }

    @Test
    public void pointcut() throws NoSuchMethodException {
        targetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);

    }

    public void targetClassPointcutMatches(String expression, boolean... expected) throws NoSuchMethodException {
        pointcutMatches(expression, expected[0], Target.class, "hello");
        pointcutMatches(expression, expected[1], Target.class, "hello", String.class);
        pointcutMatches(expression, expected[2], Target.class, "plus", int.class, int.class);
        pointcutMatches(expression, expected[3], Target.class, "minus", int.class, int.class);
        pointcutMatches(expression, expected[4], Target.class, "method");
        pointcutMatches(expression, expected[5], Bean.class, "method");

    }

    public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>... args) throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);

        Assert.assertThat(pointcut.getClassFilter().matches(clazz)
        && pointcut.getMethodMatcher().matches(clazz.getMethod(methodName, args), null),
                CoreMatchers.is(expected));
    }

}
