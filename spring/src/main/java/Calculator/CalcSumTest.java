package Calculator;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class CalcSumTest {

    @Test
    public void sumOfNumbers() throws IOException {
        Calculator calculator = new Calculator();
        int sum = calculator.calcSum(getClass().getResource("/number.txt").getPath());
        Assert.assertThat(sum, CoreMatchers.is(10));
    }

    @Test
    public void multiplyOfNumbers() throws IOException {
        Calculator calculator = new Calculator();
        int mul = calculator.calcMultiply(getClass().getResource("/number.txt").getPath());
        Assert.assertThat(mul, CoreMatchers.is(24));
    }

    @Test
    public void concatenateNumbers() throws IOException {
        Calculator calculator = new Calculator();
        String concate = calculator.concatenate(getClass().getResource("/number.txt").getPath());
        Assert.assertThat(concate, CoreMatchers.is("1234"));
    }

}
